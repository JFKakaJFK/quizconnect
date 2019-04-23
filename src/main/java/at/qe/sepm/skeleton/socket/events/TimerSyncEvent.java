package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.logic.ActiveQuestion;

public class TimerSyncEvent extends ServerEvent {

    private long remaining;
    private int questionId;

    public TimerSyncEvent(){}

    public TimerSyncEvent(ActiveQuestion q, long remaining){
        this.remaining = remaining;
        this.questionId = q.question.getId();
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }
}
