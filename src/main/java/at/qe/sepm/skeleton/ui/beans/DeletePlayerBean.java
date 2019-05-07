package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.StorageService;
import at.qe.sepm.skeleton.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;
import java.io.IOException;

@Controller
@Scope("request")
public class DeletePlayerBean {

    private StorageService storageService;
    private UserService userService;
    private PlayerService playerService;
    private SessionInfoBean sessionInfoBean;
    private ManagerService managerService;
    // private AllPlayersBean allPlayersBean;

    @Autowired
    public DeletePlayerBean(/* AllPlayersBean allPlayersBean, */ StorageService storageService, UserService userService, PlayerService playerService, SessionInfoBean sessionInfoBean, ManagerService managerService){
        this.storageService = storageService;
        this.userService = userService;
        this.playerService = playerService;
        this.sessionInfoBean = sessionInfoBean;
        this.managerService = managerService;
        // this.allPlayersBean = allPlayersBean;
    }

    public void deletePlayer(Player player){
        System.out.println("called delete");
        if(player == null || player.isNew() || !sessionInfoBean.getCurrentUser().getUsername().equals(managerService.getManagerOfPlayer(player).getUser().getUsername())){
            System.out.println(player);
            return;
        }
        System.out.println("Can delete");
        User user;
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
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/players/all.xhtml?faces-redirect=true");
        } catch (IOException e){
            // TODO
        } finally {
            // allPlayersBean.removePlayer(player);
        }
    }
}
