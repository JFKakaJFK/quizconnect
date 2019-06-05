package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.utils.ScrollPaginator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean that shows all players for the {@link Player} overview.
 * For each online user, the users are loaded only once per session and
 * are locally updated if the {@link User} adds/removes a {@link Player}.
 */
@Controller
@Scope("view")
public class AllPlayersBean implements Serializable {

    private PlayerService playerService;

    private List<Player> allPlayers;
    private List<Player> allByManager;
    private String searchPhrase = "";
    private User user;
    private boolean onlyByManager;
    private ScrollPaginator<Player> paginator;

    @Autowired
    public AllPlayersBean(PlayerService playerService, SessionInfoBean sessionInfoBean){
        this.playerService = playerService;
        this.user = sessionInfoBean.getCurrentUser();
        this.paginator = null;
    }

    /**
     * Updates the currently shown players by filtering accoriding to user input
     *
     * @param event
     */
    public void handleSearch(AjaxBehaviorEvent event){
        filterAndUpdatePlayers();
    }

    /**
     * Filters the {@link Player}s using {@link this#searchPhrase} and updates {@link this#paginator}.
     *
     * Any mutation of {@link this#allPlayers} or {@link this#allByManager} must call this method to update the {@link ScrollPaginator}.
     */
    private void filterAndUpdatePlayers(){
        getPaginator().updateList((onlyByManager && isManager() ? getAllByManager() : getAllPlayers()).stream().parallel()
                .filter(player -> player.getUser().getUsername().toLowerCase().contains(searchPhrase.toLowerCase()))
                .collect(Collectors.toList()));
    }

    /**
     * Returns all {@link Player}s by the currently logged in {@link at.qe.sepm.skeleton.model.Manager} or {@link null}
     * if the current {@link User} is no {@link at.qe.sepm.skeleton.model.Manager}. Fetches from the Database if necessary.
     *
     * @return
     */
    private List<Player> getAllByManager(){
        if(isManager() && allByManager == null){
            allByManager = playerService.getPlayersOfManager(user.getManager());
        }
        return allByManager;
    }

    /**
     * Since lazy loading getters are used internally
     */
    public void refresh(){
        this.allByManager = null;
        this.allPlayers = null;
        filterAndUpdatePlayers();
    }

    /**
     * Returns true if the current {@link User} is a {@link at.qe.sepm.skeleton.model.Manager}
     *
     * @return
     */
    public boolean isManager(){
        return user.getManager() != null;
    }

    /**
     * Returns true if the current {@link User} is allowed to edit the {@link Player}
     *
     * @param p
     * @return
     */
    public boolean isEditable(Player p){
        return (user.getPlayer() != null && user.getPlayer().equals(p)) || isDeletable(p);
    }

    /**
     * Returns true if the current {@link User} is allowed to delete the {@link Player}
     *
     * @param p
     * @return
     */
    public boolean isDeletable(Player p){
        return isManager() && getAllByManager().contains(p);
    }

    /**
     * Returns all {@link Player}s . Fetches from the Database if necessary.
     *
     * @return
     */
    private List<Player> getAllPlayers(){
        if(allPlayers == null){
            allPlayers = new ArrayList<>(playerService.getAllPlayers());
        }
        return allPlayers;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase.trim();
    }

    public boolean isOnlyByManager() {
        return onlyByManager;
    }

    public void setOnlyByManager(boolean onlyByManager) {
        this.onlyByManager = onlyByManager;
        filterAndUpdatePlayers();
    }

    /**
     * Adds a {@link Player} to the list of {@link Player}.
     *
     * There is no check for duplicates since the method is only called when a new player is added, and the
     * additional O(number of all players) runtime is simply not necessary.
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
        filterAndUpdatePlayers();
    }

    /**
     * Removes a {@link Player} from the list of {@link Player}s
     *
     * @param p {@link Player} to remove
     */
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
        filterAndUpdatePlayers();
    }

    /**
     * Lazily initializes the paginator or returns the existing paginator;
     *
     * @return
     *      A {@link ScrollPaginator} of all filtered Players.
     */
    public ScrollPaginator<Player> getPaginator() {
        if(paginator == null){
            this.paginator = new ScrollPaginator<>(getAllPlayers(), 20); // about the number of players fitting into one viewport
        }
        return paginator;
    }

    public void setPaginator(ScrollPaginator<Player> paginator) {
        this.paginator = paginator;
    }
}
