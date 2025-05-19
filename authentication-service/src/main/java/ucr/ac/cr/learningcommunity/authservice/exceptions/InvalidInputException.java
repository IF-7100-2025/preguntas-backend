package ucr.ac.cr.learningcommunity.authservice.exceptions;

public class InvalidInputException extends RuntimeException {

    private final String field;

    public InvalidInputException(String field) {
        super("Invalid field %s".formatted(field));
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
