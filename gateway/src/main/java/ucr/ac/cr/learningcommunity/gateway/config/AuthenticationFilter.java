package ucr.ac.cr.learningcommunity.gateway.config;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RefreshScope
public class AuthenticationFilter implements GlobalFilter {

    private final RouterValidator routerValidator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouterValidator routerValidator, JwtUtil jwtUtil) {
        this.routerValidator = routerValidator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isAnonymous.test(request)) {
            return chain.filter(exchange);
        }

        if (routerValidator.isProtected.test(request)) {
            String authHeader = request.getHeaders().getFirst("Authorization");
            String token = jwtUtil.getToken(authHeader);
            if (token == null || jwtUtil.isInvalid(token)) {
                return onError(exchange, HttpStatus.UNAUTHORIZED);
            }

            Claims claims = jwtUtil.getAllClaimsFromToken(token);
            String userRole = String.valueOf(claims.get("roles"));   // "COLAB" o "ADMIN"

            List<String> allowedRoles = routerValidator.requiredRoles(request);
            if (allowedRoles != null && !allowedRoles.contains(userRole)) {
                return onError(exchange, HttpStatus.FORBIDDEN);
            }


            ServerHttpRequest mutated = request.mutate()
                    .header("email",    String.valueOf(claims.get("email")))
                    .header("id",       String.valueOf(claims.get("id")))
                    .header("roles",    userRole)
                    .header("correlationId", UUID.randomUUID().toString())
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }

        return onError(exchange, HttpStatus.UNAUTHORIZED);
    }

    private Mono<Void> onError(ServerWebExchange ex, HttpStatus status) {
        ServerHttpResponse res = ex.getResponse();
        res.getHeaders().add("Content-Type", "application/json");
        res.setStatusCode(status);
        String json = String.format(
                "{\"error\":\"invalid session\", \"msg\":\"The session is invalid or expired\", \"status\": %d}",
                status.value()
        );
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return res.writeWith(Mono.just(res.bufferFactory().wrap(bytes)));
    }
}