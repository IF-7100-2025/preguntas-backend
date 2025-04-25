package ucr.ac.cr.learningcommunity.questionservice.api.rests;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ucr.ac.cr.learningcommunity.questionservice.api.types.request.CategorizeSuggestionRequest;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.Collections;
import java.util.List;

@Service
public class IAIntegrationClient {

    private final RestTemplate restTemplate;

    @Value("${ia-service.url}")
    private String iaServiceUrl;

    public IAIntegrationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CategoryResponse> getCategorySuggestionsFromIA(String questionText) {
        try {
            String url = iaServiceUrl + "/suggest";

            // configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // Crear request
            HttpEntity<CategorizeSuggestionRequest> requestEntity =
                    new HttpEntity<>(new CategorizeSuggestionRequest(questionText), headers);

            // realizar peticion al servicio de IA  Microservice
            ResponseEntity<List<CategoryResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<List<CategoryResponse>>() {}
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw BaseException.exceptionBuilder()
                        .code(ErrorCode.IA_SERVICE_ERROR)
                        .message(ErrorCode.IA_SERVICE_ERROR.getDefaultMessage())
                        .build();
            }
            return response.getBody() != null ? response.getBody() : Collections.emptyList();

        } catch (RestClientException e) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.IA_SERVICE_COMMUNICATION_ERROR)
                    .message(ErrorCode.IA_SERVICE_COMMUNICATION_ERROR.getDefaultMessage())
                    .build();
        }
    }
}