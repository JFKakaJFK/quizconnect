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

/**
 * Bean which deletes a single {@link Player} of a {@link at.qe.sepm.skeleton.model.Manager}
 */
@Controller
@Scope("request")
public class DeletePlayerBean {

    private StorageService storageService;
    private UserService userService;
    private PlayerService playerService;
    private SessionInfoBean sessionInfoBean;
    private ManagerService managerService;

    @Autowired
    public DeletePlayerBean(StorageService storageService, UserService userService, PlayerService playerService, SessionInfoBean sessionInfoBean, ManagerService managerService){
        this.storageService = storageService;
        this.userService = userService;
        this.playerService = playerService;
        this.sessionInfoBean = sessionInfoBean;
        this.managerService = managerService;
    }

    /**
     * Deletes the {@link Player} if the authenticated user is authorized to do so
     *
     * @param player
     */
    public void deletePlayer(Player player){
        if(player == null || player.isNew() || !sessionInfoBean.getCurrentUser().getUsername().equals(managerService.getManagerOfPlayer(player).getUser().getUsername())){
            return;
        }
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
        }
    }
}
