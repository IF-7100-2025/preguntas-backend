package ucr.ac.cr.learningcommunity.questionservice.handlers.commands;


public interface CreateCategoryHandler {
    record Command(String name) {}
    sealed interface Result {
        record Success(int status, String msg) implements Result {}
        record InternalError(int status, String msg) implements Result {}
    }

    Result createCategory(Command command);
}