package ucr.ac.cr.learningcommunity.questionservice.handlers.queries.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.CategoryResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.AnswerResponse;
import ucr.ac.cr.learningcommunity.questionservice.api.types.response.QuestionResponse;
import ucr.ac.cr.learningcommunity.questionservice.handlers.queries.GetQuestionsQuery;
import ucr.ac.cr.learningcommunity.questionservice.jpa.repositories.QuestionRepository;
import java.util.List;


@Service
public class GetQuestionsQueryImpl implements GetQuestionsQuery {

    private final QuestionRepository questionRepository;

    @Autowired
    public GetQuestionsQueryImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<QuestionResponse> getQuestionsByUserId(String userId) {
        return questionRepository.findByCreatedBy_Id(userId).stream().map(question -> {
            var categories = question.getCategories().stream()
                    .map(cat -> new CategoryResponse(cat.getId(), cat.getName()))
                    .toList();

            var options = question.getAnswerOptions().stream()
                    .map(opt -> new AnswerResponse(opt.getId(), opt.getText(), opt.isCorrect()))
                    .toList();

            return new QuestionResponse(
                    question.getId(),
                    question.getText(),
                    question.getExplanation(),
                    categories,
                    options,
                    question.getCreatedAt(),
                    question.getLikes(),
                    question.getDislikes()
            );
        }).toList();
    }


}
