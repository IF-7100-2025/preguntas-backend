package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

public record UserProgressResponse(
        int currentXP,
        RankInfoCurrent currentRank,
        RankInfoNext nextRank
) {}