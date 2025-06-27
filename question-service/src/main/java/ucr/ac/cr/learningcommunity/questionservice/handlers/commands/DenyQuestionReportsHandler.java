package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.ApiResponse;

public interface DenyQuestionReportsHandler {
    ApiResponse denyReports(String questionId);
}
