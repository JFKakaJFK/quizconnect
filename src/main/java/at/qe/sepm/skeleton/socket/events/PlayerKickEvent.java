package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;

public class PlayerKickEvent extends SocketEvent {

    private int playerId;

    public PlayerKickEvent(){}

    public PlayerKickEvent(Player p){
        this.playerId = p.getId();
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
