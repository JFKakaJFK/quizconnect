package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.logic.ActiveQuestion;

public class RemoveQuestionEvent extends ServerEvent {

    private int questionId;

    public RemoveQuestionEvent(){}

    public RemoveQuestionEvent(ActiveQuestion q){
        this.questionId = q.question.getId();
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
}
