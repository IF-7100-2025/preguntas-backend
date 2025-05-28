package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}
