package at.qe.sepm.skeleton.socket.events;

/**
 * Class for JSON representation of the 'onJokerUse' QuizRoom event.
 */
public class JokerUseEvent extends SocketEvent {

    private int remaining;

    public JokerUseEvent(){}

    public JokerUseEvent(int newScore){
        this.remaining = newScore;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
