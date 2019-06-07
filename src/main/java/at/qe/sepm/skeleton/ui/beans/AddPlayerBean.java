package at.qe.sepm.skeleton.ui.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.UserService;

import java.io.Serializable;

@Controller
@Scope("view")
public class AddPlayerBean implements Serializable {

    private PlayerService playerService;
    private UserService userService;
    private AllPlayersBean allPlayersBean;
    private PasswordBean passwordBean;
    private ValidationBean validationBean;
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
                         ValidationBean validationBean){
        assert sessionInfoBean.getCurrentUser().getManager() != null;
        assert userService != null;
        assert playerService != null;
        assert allPlayersBean != null;
        assert passwordBean != null;
        assert validationBean != null;
        this.manager = sessionInfoBean.getCurrentUser().getManager();
        this.userService = userService;
        this.playerService = playerService;
        this.allPlayersBean = allPlayersBean;
        this.passwordBean = passwordBean;
        this.validationBean = validationBean;
    }

    public void addUser(){
        if(!validateInput()){
            System.out.println("HAHAHA INVALID INPUT");
            return;
        }
        Player p = new Player();
        p.setAvatarPath(null); // the default avatar is loaded automatically
        p.setCreator(manager);
		playerService.saveNewPlayer(p, username, passwordBean.encodePassword(password));
        clear();
        allPlayersBean.addPlayer(p);
    }

    public boolean validateInput(){
        if(username == null || userService.loadUser(username) != null || username.length() < 3 || !validationBean.isValidText(username, 100)){
            // TODO message username already taken
            return false;
        }

        if(password == null || !validationBean.isValidPassword(password, repeatPassword)){
            return false;
        }
        return true;
    }

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
