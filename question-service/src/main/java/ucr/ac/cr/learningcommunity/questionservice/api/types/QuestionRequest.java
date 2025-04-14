package ucr.ac.cr.learningcommunity.questionservice.api.types;

public record QuestionRequest(String question, byte[] image, String[] categories) {
}
