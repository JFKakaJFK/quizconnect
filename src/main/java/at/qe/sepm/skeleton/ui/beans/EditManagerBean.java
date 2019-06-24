package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean for editing a {@link at.qe.sepm.skeleton.model.Manager}.
 */
@Controller
@Scope("view")
public class EditManagerBean implements Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private PasswordBean passwordBean;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private ValidationBean validationBean;

    @Autowired
    private MessageBean messageBean;

    private User user;
    private String email;
    private String password;
    private String repeatPassword;


    @PostConstruct
    private void init() {
        user = userService.loadUser(sessionInfoBean.getCurrentUser().getUsername());
    }
    
    /**
     * Changes the password of the Manager if the current password is valid and clears all fields.
     */
    public void changePassword() {
        if (user != null && validationBean.isValidPassword(password, repeatPassword)) {
            user.setPassword(passwordBean.encodePassword(password));
            this.user = userService.saveUser(user);
            messageBean.alertInformation("Success", "Saved new password");
            messageBean.updateComponent("messages");
        } else {
            messageBean.alertError("Error", "Couldn't save new password - please try again");
            messageBean.updateComponent("messages");
        }
        password = null;
        repeatPassword = null;
    }
    
    /**
     * Changes the email of the Manager if the current email is valid.
     */
    public void changeEmail() {
        if (user != null && validationBean.isValidEmail(email)) {
            user.getManager().setEmail(email);
            managerService.saveManager(user.getManager());
            logger.info("Manager with ID " + user.getManager().getId() + " changed e-mail to " + user.getManager().getEmail());
            messageBean.alertInformation("Success", "Saved new email address");
            messageBean.updateComponent("messages");
        } else {
            messageBean.alertError("Error", "Couldn't save new email address - please try again");
            messageBean.updateComponent("messages");
        }
    }

    public String getEmail() {
        if (email == null) {
            email = user.getManager().getEmail();
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
