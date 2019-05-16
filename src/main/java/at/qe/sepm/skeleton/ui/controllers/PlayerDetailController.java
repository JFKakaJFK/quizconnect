package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.StorageService;
import at.qe.sepm.skeleton.services.UserService;
import at.qe.sepm.skeleton.ui.beans.PasswordBean;
import at.qe.sepm.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
@Scope("view")
public class PlayerDetailController implements Serializable {

    private PasswordBean passwordBean;
    private UserService userService;
    private PlayerService playerService;
    private StorageService storageService;
    private ManagerService managerService;

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
            ManagerService managerService){
        assert passwordBean != null;
        assert userService != null;
        assert managerService != null;
        assert playerService != null;
        assert storageService != null;
        assert sessionInfoBean != null;

        this.passwordBean = passwordBean;
        this.userService = userService;
        this.storageService = storageService;
        this.playerService = playerService;
        this.currentUser = sessionInfoBean.getCurrentUser();
        this.managerService = managerService;
    }

    public void deletePlayer(){
        if(player == null || player.isNew() || !currentUser.getUsername().equals(managerService.getManagerOfPlayer(player).getUser().getUsername())){
            return;
        }

        User user = player.getUser();

        if(player.getAvatarPath() != null){
            storageService.deleteAvatar(player.getAvatarPath());
        }

        playerService.deletePlayer(player);
        userService.deleteUser(user);

        player = null;

        // TODO detect if view is profile, if yes, redirect else remove from allplayersbean
    }

    /**
     * Changes a {@link Player}s password if the new password is valid
     */
    public void changePassword(){
        if(player != null && isPasswordValid()){
            player.getUser().setPassword(passwordBean.encodePassword(password));
            userService.saveUser(player.getUser());
        }
        password = null;
        player = null;
    }

    /**
     * Returns true if the new password is valid
     *
     * @return
     */
    public boolean isPasswordValid(){
        // return password.equals("hunter2"); // TODO validate better, maybe use Validator for all passwords?
        return password != null && password.length() >= 3 && repeatPassword != null && repeatPassword.equals(password);
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
