package ucr.ac.cr.learningcommunity.questionservice.models;


public class BaseException extends RuntimeException {

    private final ErrorCode status;

    private BaseException(ErrorCode status, String msg) {
        super(msg);
        this.status = status;
    }

    public static BaseExceptionBuilder exceptionBuilder() {
        return new BaseExceptionBuilder();
    }

    public ErrorCode getErrorCode() {
        return status;
    }

    public static class BaseExceptionBuilder {

        private ErrorCode status;
        private String msg;

        public BaseExceptionBuilder code(ErrorCode errorCode) {
            this.status = errorCode;
            return this;
        }

        public BaseExceptionBuilder message(String msg) {
            this.msg = msg;
            return this;
        }

        public BaseException build() {
            return new BaseException(status, msg);
        }
    }
}

