package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Scope("session")
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

    /*
    public void handleSearch(ValueChangeEvent event){
        players = allPlayers.stream()
                .filter(player -> player.getUser().getUsername().toLowerCase().contains(event.getNewValue().toString()))
                .collect(Collectors.toList());

        // TODO use messageBean
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("form:players");
    }
    */

    public void handleSearch(AjaxBehaviorEvent event){
        players = allPlayers.stream()
                .filter(player -> player.getUser().getUsername().toLowerCase().contains(searchPhrase.toLowerCase())) // || player.getId().toString().toLowerCase().contains(searchPhrase.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Player> getAllPlayers() {
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

    //TODO: JavaDoc for getPlayers
    public List<Player> getPlayers() {
        if(players == null){
            this.players = getAllPlayers();
        }
        return players;
    }
    //TODO: JavaDoc for SetPlayers
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
        // this.allPlayers = null;
        // this.allByManager = null;
    }

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
}
