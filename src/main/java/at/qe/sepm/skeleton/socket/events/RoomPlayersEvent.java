package at.qe.sepm.skeleton.socket.events;

import java.util.List;

public class RoomPlayersEvent extends SocketEvent {

    private int num;
    private List<PlayerJSON> players;

    public RoomPlayersEvent(){}

    public RoomPlayersEvent(List<PlayerJSON> players){
        this.num = players.size();
        this.players = players;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<PlayerJSON> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerJSON> players) {
        this.players = players;
    }
}
