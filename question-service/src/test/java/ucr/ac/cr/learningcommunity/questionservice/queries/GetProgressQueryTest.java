package ucr.ac.cr.learningcommunity.questionservice.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProgressResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetProgressQuery;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl.GetProgressQueryImpl;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.RankEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.RankRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetProgressQueryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RankRepository rankRepository;

    @InjectMocks
    private GetProgressQueryImpl getProgressQuery;

    private UserEntity mockUser;
    private RankEntity currentRank;
    private RankEntity nextRank;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId("689a184b-2046-45e2-8ee2-8a00ff203df1");
        mockUser.setUsername("robert_gamer");
        mockUser.setXpAmount(150);
        mockUser.setDailyStreak(4);
        mockUser.setLastActivity(LocalDate.now());

        currentRank = new RankEntity("Aprendiz", 100, 199);
        nextRank = new RankEntity("Pensador", 200, 299);
    }

    @Test
    void testGetProgressUser() {
        when(userRepository.findByUsername("robert_gamer")).thenReturn(Optional.of(mockUser));
        when(rankRepository.findRankByXp(150)).thenReturn(Optional.of(currentRank));
        when(rankRepository.findNextRankByCurrentXP(150)).thenReturn(Optional.of(nextRank));

        GetProgressQuery.Result result = getProgressQuery.getProgressUser("robert_gamer");

        assertTrue(result instanceof GetProgressQuery.Result.Success);
        UserProgressResponse response = ((GetProgressQuery.Result.Success) result).userProgress();

        assertEquals(150, response.xp());
        assertEquals("Aprendiz", response.rank());
        assertEquals("Pensador", response.nextRank());
        assertEquals(4, response.dailyStreak());
        assertNotNull(response.lastActivity());
        assertTrue(response.progress() > 0 && response.progress() < 100);
    }
}