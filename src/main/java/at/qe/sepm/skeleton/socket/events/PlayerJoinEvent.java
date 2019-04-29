package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;
import com.fasterxml.jackson.annotation.JsonRootName;

public class PlayerJoinEvent extends ServerEvent {

    private PlayerJSON player;

    public PlayerJoinEvent(){ }

    public PlayerJoinEvent(Player p, String pathPrefix){
        this.player = new PlayerJSON(p, pathPrefix);
    }

    public PlayerJSON getPlayer() {
        return player;
    }

    public void setPlayer(PlayerJSON player) {
        this.player = player;
    }
}
