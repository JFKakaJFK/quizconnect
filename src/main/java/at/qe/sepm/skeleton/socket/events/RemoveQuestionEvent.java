package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.logic.ActiveQuestion;

/**
 * Class for JSON representation of the 'removeQuestion' QuizRoom event.
 */
public class RemoveQuestionEvent extends SocketEvent {

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
