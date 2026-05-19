package id.ac.ui.cs.advprog.gatra.quiz.service;

import id.ac.ui.cs.advprog.gatra.quiz.model.MultipleChoiceQuestion;
import id.ac.ui.cs.advprog.gatra.quiz.model.Question;
import id.ac.ui.cs.advprog.gatra.quiz.model.TrueFalseQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionFactoryTest {

    private QuestionFactory questionFactory;

    @BeforeEach
    void setUp() {
        questionFactory = new QuestionFactory();
    }

    @Test
    void create_shouldReturnMultipleChoiceQuestion_whenTypeIsMultipleChoice() {
        Question question = questionFactory.create("MULTIPLE_CHOICE");
        
        assertNotNull(question);
        assertTrue(question instanceof MultipleChoiceQuestion);
    }

    @Test
    void create_shouldReturnMultipleChoiceQuestion_whenTypeIsMultipleChoiceLowercase() {
        Question question = questionFactory.create("multiple_choice");
        
        assertNotNull(question);
        assertTrue(question instanceof MultipleChoiceQuestion);
    }

    @Test
    void create_shouldReturnTrueFalseQuestion_whenTypeIsTrueFalse() {
        Question question = questionFactory.create("TRUE_FALSE");
        
        assertNotNull(question);
        assertTrue(question instanceof TrueFalseQuestion);
    }

    @Test
    void create_shouldReturnTrueFalseQuestion_whenTypeIsTrueFalseLowercase() {
        Question question = questionFactory.create("true_false");
        
        assertNotNull(question);
        assertTrue(question instanceof TrueFalseQuestion);
    }

    @Test
    void create_shouldThrowIllegalArgumentException_whenTypeIsUnknown() {
        String invalidType = "UNKNOWN_TYPE";
        
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> questionFactory.create(invalidType)
        );
        
        assertEquals("Unknown question type: " + invalidType, exception.getMessage());
    }
}