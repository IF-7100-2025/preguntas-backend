package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProfileResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetUserInformationProfileQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetUserInformationProfileQueryImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetUserInformationProfileQueryTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserInformationProfileQueryImpl getUserInformationProfileQuery;

    private String userId;
    private UserEntity user;

    @BeforeEach
    public void setUp() {
        userId = "user123";
        user = new UserEntity();
        user.setUsername("daniQ");
        user.setEmail("dani.q@outlook.com");
        user.setProfileImage("data:image/png;base64,newImage");
    }

    @Test
    public void testGetUserInformationProfileSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        GetUserInformationProfileQuery.Result result = getUserInformationProfileQuery.getUserInformationProfile(userId);
        assertInstanceOf(GetUserInformationProfileQuery.Result.Success.class, result);

        UserProfileResponse response = ((GetUserInformationProfileQuery.Result.Success) result).userInformationProfile();
        assertEquals("daniQ", response.username());
        assertEquals("dani.q@outlook.com", response.email());
        assertEquals("data:image/png;base64,newImage", response.profileImage());
    }

    @Test
    public void testUserNotFoundReturnsError() {
        String invalidUserId = "non-existent-id";
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        GetUserInformationProfileQuery.Result result = getUserInformationProfileQuery.getUserInformationProfile(invalidUserId);
        assertInstanceOf(GetUserInformationProfileQuery.Result.Error.class, result);

        GetUserInformationProfileQuery.Result.Error error = (GetUserInformationProfileQuery.Result.Error) result;
        assertEquals(ErrorCode.USER_NOT_FOUND.getHttpStatus(), error.status()); // o ErrorCode.USER_NOT_FOUND.getHttpStatus()
        assertEquals("User not found", error.message()); // o ErrorCode.USER_NOT_FOUND.getDefaultMessage()
    }
}