package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

public record ReportQuestionRequest(String question_id,
                                    String username,
                                    String reason,
                                    String comment)
{}
