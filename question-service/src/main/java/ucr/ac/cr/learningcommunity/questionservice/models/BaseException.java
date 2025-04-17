package ucr.ac.cr.learningcommunity.questionservice.models;


public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    private BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public static BaseExceptionBuilder exceptionBuilder() {
        return new BaseExceptionBuilder();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public static class BaseExceptionBuilder {

        private ErrorCode errorCode;
        private String message;

        public BaseExceptionBuilder code(ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public BaseExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public BaseException build() {
            return new BaseException(errorCode, message);
        }
    }
}

