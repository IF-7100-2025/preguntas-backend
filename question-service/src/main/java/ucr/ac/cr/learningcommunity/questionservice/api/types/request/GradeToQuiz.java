package ucr.ac.cr.learningcommunity.questionservice.api.types.request;

import ucr.ac.cr.learningcommunity.questionservice.jpa.entities.QuestionEntity;

import java.util.List;

public record GradeToQuiz(
        List<QuestionEntity>


) {

}
