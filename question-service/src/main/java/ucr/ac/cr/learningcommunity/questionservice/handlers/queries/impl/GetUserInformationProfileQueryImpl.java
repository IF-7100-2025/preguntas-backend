package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;


import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProfileResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetUserInformationProfileQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

@Service
public class GetUserInformationProfileQueryImpl implements GetUserInformationProfileQuery {
      private final UserRepository userRepository;
    public GetUserInformationProfileQueryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result getUserInformationProfile(String userId) {
        if (userRepository.findById(userId).isPresent()) {
            return new Result.Success(new UserProfileResponse(
                    userRepository.findById(userId).get().getUsername(),
                    userRepository.findById(userId).get().getEmail(),
                    userRepository.findById(userId).get().getProfileImage()
            ));
        }else {
            return new Result.Error(ErrorCode.USER_NOT_FOUND.getHttpStatus(), ErrorCode.USER_NOT_FOUND.getDefaultMessage());
        }
    }
}
