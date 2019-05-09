package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.event.AjaxBehaviorEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean that shows all players for the {@link Player} overview
 */
@Controller
@Scope("view")
public class AllPlayersBean {

    private PlayerService playerService;

    private List<Player> allPlayers;
    private List<Player> allByManager;
    private List<Player> players;
    private String searchPhrase = "";
    private User user;
    private boolean onlyByManager;

    @Autowired
    public AllPlayersBean(PlayerService playerService, SessionInfoBean sessionInfoBean){
        this.playerService = playerService;
        this.user = sessionInfoBean.getCurrentUser();
    }

    /**
     * Updates the currently shown players by filtering accoriding to user input
     *
     * @param event
     */
    public void handleSearch(AjaxBehaviorEvent event){
        players = allPlayers.stream()
                .filter(player -> player.getUser().getUsername().toLowerCase().contains(searchPhrase.toLowerCase())) // || player.getId().toString().toLowerCase().contains(searchPhrase.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Returns all {@link Player}s or all {@link Player}s by the {@link at.qe.sepm.skeleton.model.Manager}, depending on the current selection
     *
     * @return
     */
    private List<Player> getAllPlayers() {
        if(onlyByManager && user.getManager() != null){
            if(allByManager == null){
                allByManager = playerService.getPlayersOfManager(user.getManager());
            }
            return allByManager;
        }
        if(allPlayers == null){
            allPlayers = new ArrayList<>(playerService.getAllPlayers());
        }
        return allPlayers;
    }

    public void setAllPlayers(List<Player> allPlayers) {
        this.allPlayers = allPlayers;
    }

    public List<Player> getPlayers() {
        if(players == null){
            this.players = getAllPlayers();
        }
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public boolean isOnlyByManager() {
        return onlyByManager;
    }

    public void setOnlyByManager(boolean onlyByManager) {
        this.onlyByManager = onlyByManager;
        this.players = null;
    }

    /**
     * Adds a {@link Player} to the list of {@link Player}s
     *
     * @param p {@link Player} to add
     */
    public void addPlayer(Player p){
        if(allPlayers == null){
            this.allPlayers = new ArrayList<>(playerService.getAllPlayers());
        } else {
            allPlayers.add(p);
        }
        if(allByManager == null){
            this.allByManager = playerService.getPlayersOfManager(user.getManager());
        } else {
            allByManager.add(p);
        }
    }

    public void removePlayer(Player p){
        if(allPlayers == null){
            this.allPlayers = new ArrayList<>(playerService.getAllPlayers());
        } else {
            allPlayers.remove(p);
        }
        if(allByManager == null){
            this.allByManager = playerService.getPlayersOfManager(user.getManager());
        } else {
            allByManager.remove(p);
        }
    }
}
