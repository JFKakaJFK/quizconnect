package at.qe.sepm.skeleton.ui.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.UserService;

import java.io.Serializable;

/**
 * Bean for the create user interface.
 */
@Controller
@Scope("view")
public class AddPlayerBean implements Serializable {

    private PlayerService playerService;
    private UserService userService;
    private AllPlayersBean allPlayersBean;
    private PasswordBean passwordBean;
    private ValidationBean validationBean;
    private MessageBean messageBean;
    private Manager manager;

    private String username = "";
    private String password = "";
    private String repeatPassword = "";

    @Autowired
    public AddPlayerBean(PlayerService playerService,
                         SessionInfoBean sessionInfoBean,
                         UserService userService,
                         AllPlayersBean allPlayersBean,
                         PasswordBean passwordBean,
                         ValidationBean validationBean,
                         MessageBean messageBean){
        assert sessionInfoBean.getCurrentUser().getManager() != null;
        assert userService != null;
        assert playerService != null;
        assert allPlayersBean != null;
        assert passwordBean != null;
        assert validationBean != null;
        assert messageBean != null;
        this.manager = sessionInfoBean.getCurrentUser().getManager();
        this.userService = userService;
        this.playerService = playerService;
        this.allPlayersBean = allPlayersBean;
        this.passwordBean = passwordBean;
        this.validationBean = validationBean;
        this.messageBean = messageBean;
    }
    
    /**
     * Adds a user with the specified username and password to the database if both are valid.
     */
    public void addUser(){
        if(!validateInput(true)){ return; }
        Player p = new Player();
        p.setAvatarPath(null); // the default avatar is loaded automatically
        p.setCreator(manager);
        p = playerService.saveNewPlayer(p, username, passwordBean.encodePassword(password));
        clear();
        allPlayersBean.addPlayer(p);
        messageBean.alertInformation("Success", "Successfully created new player.");
        messageBean.updateComponent("messages");
    }

    /**
     * @return True if the current username and password are valid, false otherwise.
     */
    public boolean validateInput(boolean showMessages){
        if(username == null || userService.loadUser(username) != null){
            if(showMessages){
                messageBean.alertError("Error", "Username is already taken.");
                messageBean.updateComponent("messages");
            }
            return false;
        }

        if(username.length() < 3){
            if(showMessages){
                messageBean.alertError("Error", "Username is too short (MIN. 3 Letters).");
                messageBean.updateComponent("messages");
            }
            return false;
        }

        if(!validationBean.isValidText(username, 100)){
            if(showMessages){
                messageBean.alertError("Error", "Username invalid.");
                messageBean.updateComponent("messages");
            }
            return false;
        }

        if(password == null || !validationBean.isValidPassword(password, repeatPassword)){
            if(showMessages){
                messageBean.alertError("Error", "Password invalid.");
                messageBean.updateComponent("messages");
            }
            return false;
        }
        return true;
    }
    
    /**
     * Clears the current username and password.
     */
    public void clear(){
        this.username = "";
        this.password = "";
        this.repeatPassword = "";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
