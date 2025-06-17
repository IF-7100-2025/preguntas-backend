package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.time.LocalDate;

public record UserProgressResponse(
        int xp,
        String rank,
        String nextRank,
        double progress,
        LocalDate lastActivity,
        int dailyStreak
) {}
