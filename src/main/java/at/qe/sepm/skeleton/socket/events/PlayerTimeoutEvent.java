package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;

/**
 * Class for JSON representation of the 'onTimeoutStart' QuizRoom event.
 */
public class PlayerTimeoutEvent extends SocketEvent {

    private long remaining;
    private int playerId;

    public PlayerTimeoutEvent(){}

    public PlayerTimeoutEvent(Player p, long remaining){
        this.remaining = remaining;
        this.playerId = p.getId();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public long getRemaining() {
        return remaining;
    }

    public void setRemaining(long remaining) {
        this.remaining = remaining;
    }
}
