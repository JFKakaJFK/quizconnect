package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.logic.ActiveQuestion;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionType;

import java.util.ArrayList;
import java.util.List;

public class AssignQuestionEvent extends ServerEvent {

    private int questionId;
    private String questionSet;
    private QuestionType type;
    private String question;
    private int playerId;
    private long timeRemaining;
    private List<AnswerJSON> answers;

    public AssignQuestionEvent(){}

    public AssignQuestionEvent(ActiveQuestion aq){
        Question q = aq.question;
        this.questionId = q.getId();
        this.type = q.getType();
        this.question = q.getQuestionString();
        this.playerId = aq.playerQuestion.getId();
        this.timeRemaining = aq.timeRemaining;
        this.answers = new ArrayList<>();

        answers.add(new AnswerJSON(0, q.getRightAnswerString(), aq.playerAnswer.getId()));
        // TODO init list w/ wrong answers
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(String questionSet) {
        this.questionSet = questionSet;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public List<AnswerJSON> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerJSON> answers) {
        this.answers = answers;
    }
}
