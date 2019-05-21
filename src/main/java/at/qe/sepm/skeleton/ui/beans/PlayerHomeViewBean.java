package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

/**
 * Bean for Home View. The player always is the current {@link User}.
 */
@Controller
@Scope("view")
public class PlayerHomeViewBean implements Serializable {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private Player player;

    @Autowired
    public PlayerHomeViewBean(SessionInfoBean sessionInfoBean){
        User u = sessionInfoBean.getCurrentUser();
        if(u == null || u.getPlayer() == null){
            log.error("Spring security failed: an unauthorized attempt was made to access this page.");
            return;
        }
        this.player = sessionInfoBean.getCurrentUser().getPlayer();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
