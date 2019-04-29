package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@Controller
@Scope("view")
public class CreatePlayerBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PasswordBean passwordBean;

    private Player player;
    private String username;
    private String password;

    @PostConstruct
    public void init(){
        player = new Player();
        player.setCreator(sessionInfoBean.getCurrentUser().getManager());
    }

    // TODO: error messages & JavaDoc
    public void createNewPlayer(){
        if(player.getCreator() == null){
            return;
        }
        if(username == null || password == null){
            return;
        }
        if(username.length() > 100){
            return;
        }

        playerService.saveNewPlayer(player, username, passwordBean.encodePassword(password));
        log.info("Created new Player " + player.toString());
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
}
