package at.qe.sepm.skeleton.socket.events;

public class JokerUseEvent extends ServerEvent {

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
