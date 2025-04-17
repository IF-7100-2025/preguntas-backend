package ucr.ac.cr.learningcommunity.questionservice.models;

public enum ErrorCode {
    ERROR_NOT_IDENTIFIED(500, "Error not identified"),
    CATEGORIES_NOT_FOUND(404, "Categories not found"),
    NOT_AUTHORIZED(401, "User not authorized"),
    CATEGORY_ALREADY_EXISTS(401,"Category already exists"),
    IA_SERVICE_ERROR(400,"Error with IA service, please try again later"),
    IA_SERVICE_COMMUNICATION_ERROR(501,"Error comunicating with IA service, please try again later");
    ErrorCode(int statusCode, String message) {}
}
