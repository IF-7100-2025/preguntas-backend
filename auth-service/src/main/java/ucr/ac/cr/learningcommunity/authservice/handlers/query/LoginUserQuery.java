// LoginUserQuery.java
package ucr.ac.cr.learningcommunity.authservice.handlers.query;

import ucr.ac.cr.learningcommunity.authservice.api.types.LoginRequest;
import ucr.ac.cr.learningcommunity.authservice.api.types.LoginResponse;

public interface LoginUserQuery {
    Result query(LoginRequest request);

    sealed interface Result {
        record Success(LoginResponse user) implements Result {}
        record InvalidCredentials() implements Result {}
        record UserNotFound() implements Result {}
    }
}
