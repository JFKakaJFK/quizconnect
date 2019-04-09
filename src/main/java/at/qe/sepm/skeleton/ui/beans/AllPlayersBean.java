package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.ManagedBean;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
public class AllPlayersBean {

    @Autowired
    private PlayerService playerService;

    private List<Player> players;

    public List<Player> getPlayers() {
        if(players == null){
            this.players = new ArrayList<>(playerService.getAllPlayers());
        }
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
