package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

public record UpdateProfileRequest(
        String username,
        String email,
        String profileImage
) {}