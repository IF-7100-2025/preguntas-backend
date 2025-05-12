package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;


import ucr.ac.cr.learningcommunity.questionservice.api.types.request.QuestionRequest;

import java.util.List;

public interface CreateQuestionHandler {

    sealed interface Result {
        record Success(int status, String msg) implements Result {}
        record Unauthorized(int status, String msg) implements Result {}
        record InternalError(int status, String msg) implements Result {}
    }

    Result createQuestion(QuestionRequest command);
}