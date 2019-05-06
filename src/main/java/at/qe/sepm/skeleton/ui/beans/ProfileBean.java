package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
//@Scope("session")
@Scope("view")
public class ProfileBean implements Serializable {

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

    // TODO should not need player as input, since this.player should be same... Player player
    public void deletePlayer(){ // TODO fix, player always null
        System.out.println("called delete");
        if(player == null || !isDeletable()){
            System.out.println(player);
            System.out.println(isDeletable());
            return;
        }
        User user;
        if(player != null){
            user = player.getUser();
            /* TODO test if cascade on entity is enough
            for(QuestionSetPerformance qsp: performanceService.getQuestionSetPerformancesOfPlayer(player)){
                performanceService.deleteQuestionSetPerfomance(qsp);
            }
            */
            if(player.getAvatarPath() != null){
                storageService.deleteAvatar(player.getAvatarPath());
            }
            // TODO remove player from managers players?
            playerService.deletePlayer(player);
            userService.deleteUser(user);
            this.player = null;
        }
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/players/all.xhtml?faces-redirect=true");
        } catch (IOException e){
            // TODO
        }
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

    public void setPlayer(Player player) {}
}
