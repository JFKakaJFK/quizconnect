package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.ui.beans.ProfileBean;
import at.qe.sepm.skeleton.ui.beans.SessionInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * This controller redirects logged in users to their home page
 * respective to their authorization.
 */
@Controller
@Scope("request")
public class LoginRedirectController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SessionInfoBean sessionInfoBean;
    private ProfileBean profileBean;
    private PlayerService playerService;

    @Autowired
    public LoginRedirectController(SessionInfoBean sessionInfoBean, ProfileBean profileBean, PlayerService playerService){
        assert sessionInfoBean != null;
        assert profileBean != null;
        assert playerService != null;
        this.sessionInfoBean = sessionInfoBean;
        this.profileBean = profileBean;
        this.playerService = playerService;
    }

    /**
     * Redirects {@link at.qe.sepm.skeleton.model.Player}s to the player homepage and
     * {@link at.qe.sepm.skeleton.model.Manager}s to the manager homepage. If a user is
     * neither, the redirect logs the user out.
     */
    public void redirect() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        if(sessionInfoBean.isLoggedIn()){
            User user = sessionInfoBean.getCurrentUser();

            String url = "/logout";
            if(user.getPlayer() != null){
                url = "/players/profile.xhtml";
                profileBean.setPlayer(playerService.getPlayerByUsername(user.getUsername()));
                profileBean.setHome(true);
            } else if(user.getManager() != null){
                url = "/secured/home.xhtml";
            }

            try {
                ec.redirect(url);
            } catch (IOException e){
                log.warn("Failed to redirect");
            }
        }
    }
}
