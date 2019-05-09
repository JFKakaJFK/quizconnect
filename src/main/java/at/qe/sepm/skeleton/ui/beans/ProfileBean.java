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

/**
 * Bean for {@link Player} profiles
 */
@Controller
@Scope("session")
public class ProfileBean implements Serializable {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private ManagerService managerService;
    private PlayerService playerService;
    private UserService userService;
    private QuestionSetPerformanceService performanceService;
    private StorageService storageService;
    private SessionInfoBean sessionInfoBean;

    private Player player;

    @Autowired
    public ProfileBean(ManagerService managerService,
                       PlayerService playerService,
                       UserService userService,
                       QuestionSetPerformanceService performanceService,
                       StorageService storageService,
                       SessionInfoBean sessionInfoBean){
        this.playerService = playerService;
        this.userService = userService;
        this.performanceService = performanceService;
        this.storageService = storageService;
        this.sessionInfoBean = sessionInfoBean;
        this.managerService = managerService;
    }

    // Deletion option is only on profile since deletion should be thought through, and therefore maybe not too easy to find & more performance (lots of db calls)
    public boolean isDeletable(){
        return sessionInfoBean.getCurrentUser().equals(managerService.getManagerOfPlayer(player).getUser());
    }

    public void setDeletable(boolean deletable){}

    public int getId(){
        return 0;
    }

    public void setId(int id) {
        this.player = playerService.getPlayerById(id);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        if(playerService.getPlayerById(player.getId()) == null){
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/players/all.xhtml?faces-redirect=true");
            } catch (IOException e){
                log.warn("Failed to redirect");
            }
        }
        this.player = player;
    }
}
