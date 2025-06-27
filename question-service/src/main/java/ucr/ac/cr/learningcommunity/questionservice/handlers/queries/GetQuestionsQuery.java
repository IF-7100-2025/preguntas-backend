package ucr.ac.cr.learningcommunity.questionservice.handlers.queries;

import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuestionResponse;


import java.util.List;

public interface GetQuestionsQuery {
    List<QuestionResponse> getQuestionsByUserId(String userId);
}
