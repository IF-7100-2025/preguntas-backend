package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuestionWithReportsResponse;

import java.util.List;

public interface GetReportedQuestionsQuery {
    List<QuestionWithReportsResponse> getReportedQuestions();
    QuestionWithReportsResponse getReportedQuestionById(String questionId);
}
