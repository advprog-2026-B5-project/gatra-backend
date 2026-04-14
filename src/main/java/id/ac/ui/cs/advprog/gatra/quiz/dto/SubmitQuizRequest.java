package id.ac.ui.cs.advprog.gatra.quiz.dto;

import java.util.List;
import java.util.UUID;

public class SubmitQuizRequest {
    private UUID articleID;
    private UUID userID;
    private List<AnswerItem> answers;

    public UUID getArticleID(){return articleID;}
    public UUID getUserID(){return userID;}
    public List<AnswerItem> getAnswers() {return answers;}

    public static class AnswerItem{
        private UUID questionID;
        private String answer;

        public UUID getQuestionID() { return questionID; }
        public String getAnswer() { return answer; }
    }
}
