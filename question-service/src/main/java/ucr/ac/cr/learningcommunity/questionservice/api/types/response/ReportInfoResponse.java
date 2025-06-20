package ucr.ac.cr.learningcommunity.questionservice.api.types.response;

import java.util.Date;
import java.util.UUID;

public record ReportInfoResponse(
        UUID reportId,
        UUID userId,
        String reason,
        String description,
        Date reportedAt
) {}