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
    private PasswordBean passwordBean;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private ValidationBean validationBean;

    @Autowired
    private MessageBean messageBean;

    private User user;
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
            logger.info(user.getUsername() + " changed password");
            messageBean.alertInformation("Success", "Saved new password");
            messageBean.updateComponent("messages");
        } else {
            messageBean.alertError("Error", "Couldn't save new password - please try again");
            messageBean.updateComponent("messages");
        }
        password = null;
        repeatPassword = null;
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
