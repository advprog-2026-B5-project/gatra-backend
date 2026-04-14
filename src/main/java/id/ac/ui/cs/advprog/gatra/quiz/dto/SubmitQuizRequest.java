package id.ac.ui.cs.advprog.gatra.quiz.dto;

import java.util.List;
import java.util.UUID;

public class SubmitQuizRequest {
    private UUID articleId;
    private UUID userId;
    private List<AnswerItem> answers;

    public SubmitQuizRequest() {}

    public UUID getArticleId(){return articleId;}
    public UUID getUserId(){return userId;}
    public List<AnswerItem> getAnswers() {return answers;}

    public void setArticleId(UUID articleId) { this.articleId = articleId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }

    public static class AnswerItem{
        private UUID questionId;
        private String answer;

        public AnswerItem() {}

        public UUID getQuestionId() { return questionId; }
        public String getAnswer() { return answer; }

        public void setQuestionId(UUID questionId) { this.questionId = questionId; }
        public void setAnswer(String answer) { this.answer = answer; }
    }


}
