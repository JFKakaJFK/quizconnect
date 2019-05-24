package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;

public class ReadyUpEvent extends SocketEvent {

    private int playerId;
    private int totalReady;

    public ReadyUpEvent(){}

    public ReadyUpEvent(Player p, int totalReady){
        this.playerId = p.getId();
        this.totalReady = totalReady;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getTotalReady() {
        return totalReady;
    }

    public void setTotalReady(int totalReady) {
        this.totalReady = totalReady;
    }
}
