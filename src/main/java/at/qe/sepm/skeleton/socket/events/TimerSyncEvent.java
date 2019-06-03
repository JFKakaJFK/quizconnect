package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.logic.ActiveQuestion;

/**
 * Class for JSON representation of the 'onTimerSync' QuizRoom event.
 */
public class TimerSyncEvent extends SocketEvent {

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
