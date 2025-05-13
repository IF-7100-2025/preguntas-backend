package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;

import org.apache.hc.core5.reactor.Command;

public interface CreateQuizHandler {

    sealed interface Result{
        record Success(int status, String msg) implements Result{}
        record Unauthorized(int status, String msg) implements Result{}
        record InternalError(int status, String msg) implements Result{}

    }
    Result createQuiz(Command command);
}
