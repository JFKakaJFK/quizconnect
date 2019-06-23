package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.StorageService;
import at.qe.sepm.skeleton.services.UserService;
import at.qe.sepm.skeleton.ui.beans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * Controller for updating Player details and deleting Players.
 */
@Controller
@Scope("view")
public class PlayerDetailController implements Serializable {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private PasswordBean passwordBean;
    private UserService userService;
    private PlayerService playerService;
    private StorageService storageService;
    private ManagerService managerService;
    private ValidationBean validationBean;
    private MessageBean messageBean;

    private User currentUser;
    private Player player;
    private String password;
    private String repeatPassword;

    @Autowired
    public PlayerDetailController(
            PasswordBean passwordBean,
            UserService userService,
            StorageService storageService,
            PlayerService playerService,
            SessionInfoBean sessionInfoBean,
            ManagerService managerService,
            ValidationBean validationBean,
            MessageBean messageBean){
        assert passwordBean != null;
        assert userService != null;
        assert managerService != null;
        assert playerService != null;
        assert storageService != null;
        assert sessionInfoBean != null;
        assert validationBean != null;
        assert messageBean != null;

        this.passwordBean = passwordBean;
        this.userService = userService;
        this.storageService = storageService;
        this.playerService = playerService;
        this.currentUser = sessionInfoBean.getCurrentUser();
        this.managerService = managerService;
        this.validationBean = validationBean;
        this.messageBean = messageBean;
    }

    /**
     * Deletes a Player if the current user is the creator of the Player.
     */
    public void deletePlayer(){
        deletePlayer(null);
    }

    /**
     * Deletes a Player if the current user is the creator of the Player.
     *
     * @param allPlayersBean
     *         The bean to refresh.
     */
    public void deletePlayer(AllPlayersBean allPlayersBean){
        if(player == null || player.isNew() || !currentUser.getUsername().equals(managerService.getManagerOfPlayer(player).getUser().getUsername())){
            return;
        }

        User user = player.getUser();

        if(player.getAvatarPath() != null){
            storageService.deleteAvatar(player.getAvatarPath());
        }

        playerService.deletePlayer(player);
        userService.deleteUser(user);

        messageBean.alertInformation("Success", "Player deleted.");
        messageBean.updateComponent("messages");

        log.info("Player " + player.getUser().getUsername() + " was successfully deleted");
        // wheter to simply remove the player or whether to refresh from the db is still subject of discussion // TODO
        if(allPlayersBean != null){
            allPlayersBean.removePlayer(player);
            // allPlayersBean.refresh();
        }
        player = null;

        FacesContext fc = FacesContext.getCurrentInstance();
        String viewId = fc.getViewRoot().getViewId();

        if(viewId.equals("/players/profile.xhtml")){
            try {
                fc.getExternalContext().redirect("/players/all.xhtml?faces-redirect=true");
            } catch (IOException e){
                log.warn("Failed to redirect");
            }
        }
    }

    /**
     * Changes a {@link Player}s password if the new password is valid
     */
    public void changePassword(){
        if(player != null && isPasswordValid()){
            player.getUser().setPassword(passwordBean.encodePassword(password));
            userService.saveUser(player.getUser());

            messageBean.alertInformation("Success", "Password was successfully changed.");
            messageBean.updateComponent("messages");
        }
        password = null;
        repeatPassword = null;
        player = null;
    }

    /**
     * Returns true if the new password is valid
     *
     * @return Boolean if the new password is valid.
     */
    public boolean isPasswordValid(){
        return validationBean.isValidPassword(password, repeatPassword);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
