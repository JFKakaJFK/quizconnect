package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("readyUp")
public class ReadyUpEvent extends ServerEvent {

    private PlayerJSON player;
    private int totalReady;

    public ReadyUpEvent(){}

    public ReadyUpEvent(Player p, int totalReady){
        this.player = new PlayerJSON(p);
        this.totalReady = totalReady;
    }

    public PlayerJSON getPlayer() {
        return player;
    }

    public void setPlayer(PlayerJSON player) {
        this.player = player;
    }

    public int getTotalReady() {
        return totalReady;
    }

    public void setTotalReady(int totalReady) {
        this.totalReady = totalReady;
    }
}
