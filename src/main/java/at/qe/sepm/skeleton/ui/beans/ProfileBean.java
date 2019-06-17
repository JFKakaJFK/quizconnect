package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Bean for {@link Player} profiles
 */
@Controller
@Scope("session")
public class ProfileBean implements Serializable {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private PlayerService playerService;
    private SessionInfoBean sessionInfoBean;
    private boolean isHome = false;
    private Player player;
    private List<Player> recentlyPlayedWith;

    @Autowired
    public ProfileBean(PlayerService playerService, SessionInfoBean sessionInfoBean){
        this.playerService = playerService;
        this.recentlyPlayedWith = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the {@link Player} and loads the recently played with {@link Player}s
     * @param player
     */
    public void setPlayer(Player player) {
        this.player = playerService.getPlayerById(player.getId());
        if(this.player == null && isHome){
            this.player = playerService.getPlayerByUsername(sessionInfoBean.getCurrentUserName());
            this.isHome = false;
        }
        if(this.player == null){
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/players/all.xhtml?faces-redirect=true");
                return;
            } catch (IOException e){
                log.warn("Failed to redirect");
                return;
            }
        }
        List<String> userNames = this.player.getPlayedWithLast();
        this.recentlyPlayedWith = userNames != null && userNames.size() > 0 ? userNames.stream()
                .map(u -> playerService.getPlayerByUsername(u))
                .filter(p -> p != null)
                .collect(Collectors.toList()) : new ArrayList<>();
        // update the recently played with size, if one of the players was deleted
        if(userNames != null && userNames.size() != recentlyPlayedWith.size()){
            this.player._setPlayedWithLast(recentlyPlayedWith);
            this.player = playerService.savePlayer(this.player);
        }
    }

    public List<Player> getRecentlyPlayedWith() {
        return recentlyPlayedWith;
    }

    public void setRecentlyPlayedWith(List<Player> recentlyPlayedWith) {
        this.recentlyPlayedWith = recentlyPlayedWith;
    }

    public void setHome(boolean home) {
        isHome = home;
    }
}
