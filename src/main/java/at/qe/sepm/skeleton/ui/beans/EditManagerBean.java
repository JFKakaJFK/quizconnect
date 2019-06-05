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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Scope("view")
public class EditManagerBean {

    /**
     * Changes a {@link Player}s password if the new password is valid
     */

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private PasswordBean passwordBean;

    @Autowired
    private SessionInfoBean sessionInfoBean;


    private User user;
    private String email;
    private String password;
    private String repeatPassword;


    @PostConstruct
    private void init() {
        user = sessionInfoBean.getCurrentUser();
    }

    public void changePassword() {
        if (user != null && isPasswordValid()) {
            user.setPassword(passwordBean.encodePassword(password));
            userService.saveUser(user);
        }

        user = null;
        password = null;
        repeatPassword = null;
    }

    public void changeEmail() {
        if (user != null && isEmailValid()) {
            user.getManager().setEmail(email);
            managerService.saveManager(user.getManager());
            logger.info("Manager with ID " + user.getManager().getId() + " changed e-mail to " + user.getManager().getEmail());
        }
    }


    public boolean isEmailValid() {
        if (email != null) {

            //OWASP Validation Regex Repository
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";
            Pattern pat = Pattern.compile(emailRegex);
            return pat.matcher(email).matches();
        }
        return false;
    }

    /**
     * Returns true if the new password is valid
     *
     * @return
     */
    public boolean isPasswordValid() {
        return password != null && password.length() >= 3 && repeatPassword != null && repeatPassword.equals(password);
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
