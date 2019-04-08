package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.ui.beans.SessionInfoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;

@Controller
@Scope("request")
public class LoginRedirectController {

    @Autowired
    private SessionInfoBean sessionInfoBean;

    public void redirect() throws IOException {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        if(sessionInfoBean.isLoggedIn()){
            User user = sessionInfoBean.getCurrentUser();

            if(user.getPlayer() != null){
                ec.redirect("/player/home.xhtml");
            } else if(user.getManager() != null){
                ec.redirect("/secured/home.xhtml");
            } else {
                ec.redirect("/logout");
            }
        }
    }
}
