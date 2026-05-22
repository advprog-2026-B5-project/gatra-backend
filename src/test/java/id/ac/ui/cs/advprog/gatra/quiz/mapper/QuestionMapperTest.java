package id.ac.ui.cs.advprog.gatra.quiz.mapper;

import id.ac.ui.cs.advprog.gatra.article.model.Article;
import id.ac.ui.cs.advprog.gatra.quiz.dto.QuestionResponse;
import id.ac.ui.cs.advprog.gatra.quiz.model.MultipleChoiceQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.model.TrueFalseQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionMapperTest {

    private QuestionMapper questionMapper;
    private Article article;
    private UUID articleId;

    @BeforeEach
    void setUp() {
        questionMapper = new QuestionMapper();

        articleId = UUID.randomUUID();
        article = new Article();
        article.setId(articleId);
    }

    @Test
    void toResponse_givenTrueFalseQuestion_shouldMapCorrectly() {
        TrueFalseQuestion question = new TrueFalseQuestion();
        question.setId(UUID.randomUUID());
        question.setText("Is the sky blue?");
        question.setCorrectAnswer("True");
        question.setArticle(article);

        QuestionResponse response = questionMapper.toResponse(question);

        assertNotNull(response);
        assertEquals(question.getId(), response.getId());
        assertEquals("TRUE_FALSE", response.getType());
        assertEquals("Is the sky blue?", response.getText());
        assertEquals(articleId, response.getArticleId());
        assertEquals("True", response.getCorrectAnswer());
        assertNull(response.getOptions());
    }

    @Test
    void toResponse_givenMultipleChoiceQuestion_shouldMapCorrectly() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.setId(UUID.randomUUID());
        question.setText("What is 1 + 1?");
        question.setCorrectAnswer("B");
        question.setOptions(List.of("1", "2", "3", "4"));
        question.setArticle(article);

        QuestionResponse response = questionMapper.toResponse(question);

        assertNotNull(response);
        assertEquals(question.getId(), response.getId());
        assertEquals("MULTIPLE_CHOICE", response.getType());
        assertEquals("What is 1 + 1?", response.getText());
        assertEquals(articleId, response.getArticleId());
        assertEquals("B", response.getCorrectAnswer());
        assertEquals(List.of("1", "2", "3", "4"), response.getOptions());
    }

    @Test
    void toResponse_givenMultipleChoiceQuestion_optionsShouldNotBeNull() {
        MultipleChoiceQuestion question = new MultipleChoiceQuestion();
        question.setId(UUID.randomUUID());
        question.setText("Sample MCQ");
        question.setCorrectAnswer("A");
        question.setOptions(List.of("A", "B", "C"));
        question.setArticle(article);

        QuestionResponse response = questionMapper.toResponse(question);

        assertNotNull(response.getOptions());
        assertEquals(3, response.getOptions().size());
    }

    @Test
    void toResponse_givenTrueFalseQuestion_optionsShouldBeNull() {
        TrueFalseQuestion question = new TrueFalseQuestion();
        question.setId(UUID.randomUUID());
        question.setText("True or false?");
        question.setCorrectAnswer("False");
        question.setArticle(article);

        QuestionResponse response = questionMapper.toResponse(question);

        assertNull(response.getOptions());
    }
}