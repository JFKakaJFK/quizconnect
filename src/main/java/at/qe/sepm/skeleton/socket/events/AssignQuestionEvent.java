package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.logic.ActiveQuestion;
import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionType;

import java.util.ArrayList;
import java.util.List;

public class AssignQuestionEvent extends SocketEvent {

    private int questionId;
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
        if(aq.playersWrongAnswers == null || aq.playersWrongAnswers.size() == 0) return;
        answers.add(new AnswerJSON(1, q.getWrongAnswerString_1(), aq.playersWrongAnswers.get(0).getId()));
        if(aq.playersWrongAnswers.size() == 1) return;
        answers.add(new AnswerJSON(2, q.getWrongAnswerString_2(), aq.playersWrongAnswers.get(1).getId()));
        if(aq.playersWrongAnswers.size() == 2) return;
        answers.add(new AnswerJSON(3, q.getWrongAnswerString_3(), aq.playersWrongAnswers.get(2).getId()));
        if(aq.playersWrongAnswers.size() == 3) return;
        answers.add(new AnswerJSON(4, q.getWrongAnswerString_4(), aq.playersWrongAnswers.get(3).getId()));
        if(aq.playersWrongAnswers.size() == 4) return;
        answers.add(new AnswerJSON(5, q.getWrongAnswerString_5(), aq.playersWrongAnswers.get(4).getId()));

    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
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
