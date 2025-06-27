package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.UserProgressResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetProgressQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.RankEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.UserEntity;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.RankRepository;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.UserRepository;
import ucr.ac.cr.learningcommunity.questionservice.models.ErrorCode;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RankInfoCurrent;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.RankInfoNext;

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
        return new Result.Error(ErrorCode.ERROR_NOT_IDENTIFIED.getHttpStatus(), "Method not implemented for username lookup by default now.");
    }


    public Result getProgressUserById(String userId) {
        try {
            Optional<UserEntity> userOpt = userRepository.findById(userId); // Se cambió a findById
            if (userOpt.isEmpty()) {
                return new Result.Error(ErrorCode.USER_NOT_FOUND.getHttpStatus(), ErrorCode.USER_NOT_FOUND.getDefaultMessage());
            }

            UserEntity user = userOpt.get();
            int currentXP = user.getXpAmount();

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

                requiredXP = Math.max(0, requiredXP);

                nextRank = new RankInfoNext(
                        nextRankEntity.getRank(),
                        requiredXP
                );
            } else {

                nextRank = new RankInfoNext(null, null);
            }


            String rankName = currentRank.name();
            String nextRankName = nextRank.name();
            double progress;
            if (nextRank.requiredXP() == null || nextRank.requiredXP() == 0) {
                progress = 100.0;
            } else {

                double xpInCurrentRank = currentXP - currentRankEntity.getMinXP();
                double xpToNextRankBoundary = currentRankEntity.getMaxXP() != null ?
                        (currentRankEntity.getMaxXP() - currentRankEntity.getMinXP()) :
                        (currentXP + nextRank.requiredXP() - currentRankEntity.getMinXP());

                if (xpToNextRankBoundary <= 0) {
                    progress = 100.0;
                } else {
                    progress = (xpInCurrentRank / xpToNextRankBoundary) * 100.0;
                }

                progress = Math.min(100.0, Math.max(0.0, progress));
            }


            UserProgressResponse response = new UserProgressResponse(
                    currentXP,
                    rankName,
                    nextRankName,
                    progress,
                    user.getLastActivity(),
                    user.getDailyStreak()
            );

            return new Result.Success(response);

        } catch (Exception e) {
            return new Result.Error(ErrorCode.ERROR_NOT_IDENTIFIED.getHttpStatus(), "Error obtaining user rank: " + e.getMessage());
        }
    }
}