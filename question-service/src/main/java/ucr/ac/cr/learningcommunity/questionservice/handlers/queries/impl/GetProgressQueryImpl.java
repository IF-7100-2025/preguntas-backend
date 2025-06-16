package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RankInfoCurrent;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RankInfoNext;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProgressResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetProgressQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.RankEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.RankRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;

import java.util.Optional;

@Service
public class GetProgressQueryImpl implements GetProgressQuery {
    private final UserRepository userRepository;
    private final RankRepository rankRepository;

    public GetProgressQueryImpl(UserRepository userRepository, RankRepository rankRepository) {
        this.userRepository = userRepository;
        this.rankRepository = rankRepository;
    }

    @Override
    public Result getProgressUser(String username) {
        try {
            Optional<UserEntity> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return new Result.Error(ErrorCode.USER_NOT_FOUND.getHttpStatus(), ErrorCode.USER_NOT_FOUND.getDefaultMessage());
            }

            UserEntity user = userOpt.get();
            int currentXP = user.getXpAmount(); // <-- aquí corregido

            Optional<RankEntity> currentRankOpt = rankRepository.findRankByXp(currentXP);
            if (currentRankOpt.isEmpty()) {
                return new Result.Error(ErrorCode.RANK_NOT_FOUND.getHttpStatus(), ErrorCode.RANK_NOT_FOUND.getDefaultMessage());
            }

            RankEntity currentRankEntity = currentRankOpt.get();

            RankInfoCurrent currentRank = new RankInfoCurrent(
                    currentRankEntity.getRank(),
                    currentRankEntity.getMinXP()
            );

            Optional<RankEntity> nextRankOpt = rankRepository.findNextRankByCurrentXP(currentXP);
            RankInfoNext nextRank;

            if (nextRankOpt.isPresent()) {
                RankEntity nextRankEntity = nextRankOpt.get();
                int requiredXP = nextRankEntity.getMinXP() - currentXP;

                nextRank = new RankInfoNext(
                        nextRankEntity.getRank(),
                        requiredXP
                );
            } else {
                nextRank = new RankInfoNext(null, null);
            }

            UserProgressResponse response = new UserProgressResponse(
                    currentXP,
                    currentRank,
                    nextRank
            );

            return new Result.Success(response);

        } catch (Exception e) {
            return new Result.Error(ErrorCode.ERROR_NOT_IDENTIFIED.getHttpStatus(), "error obtaining user rank: " + e.getMessage());
        }
    }
}
