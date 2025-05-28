package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

public record UserProfileResponse(
        String username,
        String email,
        String profileImage
) {}