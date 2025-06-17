package ucr.ac.cr.learningcommunity.questionservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.events.Event;
import ucr.ac.cr.learningcommunity.questionservice.events.EventType;
import ucr.ac.cr.learningcommunity.questionservice.events.actions.ResgisterUser;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.RankRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.BaseException;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

@Service
public class KafkaEventsListener {
    private final UserRepository userRepository;
    private final RankRepository rankRepository;

    public KafkaEventsListener(UserRepository userRepository, RankRepository rankRepository) {
        this.userRepository = userRepository;
        this.rankRepository = rankRepository;
    }

    @KafkaListener(topics = "user-registered-topic2", groupId = "user-sync-group")
    public void handleEvent(Event<?> event) {
        try {
            if (event.getEventType() == EventType.NEWUSER) {
                ResgisterUser registerUserData = (ResgisterUser) event.getData();
                if (userRepository.findById(registerUserData.getUserId()).isEmpty()) {
                    UserEntity user = new UserEntity();
                    user.setId(registerUserData.getUserId());
                    user.setUsername(registerUserData.getUsername());
                    user.setEmail(registerUserData.getEmail());
                    user.setRole(registerUserData.getRole());
                    user.setPassword(registerUserData.getPassword());
                    user.setXpAmount(0); // XP inicial
                    user.setDailyStreak(1); // Streak inicial opcional
                    user.setLastActivity(java.time.LocalDate.now()); // Fecha de actividad inicial

                    // Asignar rango según XP = 0
                    rankRepository.findRankByXp(0).ifPresent(rank ->
                            user.setCurrentRank(rank.getRank())
                    );

                    userRepository.save(user);
                }
            }
        } catch (Exception e) {
            throw BaseException.exceptionBuilder()
                    .code(ErrorCode.ERROR_NOT_IDENTIFIED)
                    .message(ErrorCode.ERROR_NOT_IDENTIFIED.getDefaultMessage())
                    .build();
        }
    }
}
