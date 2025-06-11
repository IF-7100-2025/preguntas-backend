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
                return new Result.Error(404, "Usuario no encontrado: " + username);
            }

            UserEntity user = userOpt.get();
            int currentXP = user.getXP_Amount();

            Optional<RankEntity> currentRankOpt = rankRepository.findRankByXp(currentXP);
            if (currentRankOpt.isEmpty()) {
                return new Result.Error(500, "No se encontró un rango para el XP: " + currentXP);
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
                // No hay siguiente rango (es el rango máximo)
                nextRank = new RankInfoNext(null, null);
            }

            UserProgressResponse response = new UserProgressResponse(
                    currentXP,
                    currentRank,
                    nextRank
            );

            return new Result.Success(response);

        } catch (Exception e) {
            return new Result.Error(500, "Error al obtener progreso del usuario: " + e.getMessage());
        }
    }
}