package ucr.ac.cr.learningcommunity.iaservice.models;

public enum ErrorCode {
  VALIDATION_ERROR(400, "Validation error"),
    OK(200, "OK"),
    NOT_AUTHORIZED(401, "User not authorized"),
    IA_SERVICE_ERROR(400,"Error with IA service, please try again later"),
    IA_SERVICE_COMMUNICATION_ERROR(501,"Error comunicating with IA service, please try again later"),
    IA_CATEGORY_SERVICE_ERROR(501,"Error comunicating with IA category service, please try again later");
    private final int status;
    private final String message;
    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public int getHttpStatus() {
        return status;
    }
    public String getDefaultMessage() {
        return message;
    }
}