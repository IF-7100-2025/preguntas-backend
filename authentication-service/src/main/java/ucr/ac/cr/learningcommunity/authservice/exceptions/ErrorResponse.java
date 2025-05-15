package ucr.ac.cr.learningcommunity.authservice.exceptions;


import java.util.UUID;

public record ErrorResponse(
        String message,
        int code,
        UUID correlationId
) {

}