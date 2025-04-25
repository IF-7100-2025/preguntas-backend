package ucr.ac.cr.learningcommunity.questionservice.models;

public enum ErrorCode {
    ERROR_NOT_IDENTIFIED(500, "Error not identified"),
    NOT_IMPLEMENTED(501, "Not implemented"),
    CATEGORY_SUGGESTION_NOT_FOUND(404, "Category suggestion not found"),
    CATEGORIES_NOT_FOUND(404, "Categories not found"),
    VALIDATION_ERROR(400, "Validation error"),
    NOT_AUTHORIZED(401, "User not authorized"),
    CATEGORY_ALREADY_EXISTS(401,"Category already exists"),
    IA_SERVICE_ERROR(400,"Error with IA service, please try again later"),
    IA_SERVICE_COMMUNICATION_ERROR(501,"Error comunicating with IA service, please try again later");
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
