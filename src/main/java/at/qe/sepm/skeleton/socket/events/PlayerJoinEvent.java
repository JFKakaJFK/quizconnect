package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;

public class PlayerJoinEvent extends SocketEvent {

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
