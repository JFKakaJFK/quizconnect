package at.qe.sepm.skeleton.ui.beans;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * Bean to help to create a new user object
 *
 * @author Johannes Spies
 */
@Component
@Scope("session")
public class UserBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private PasswordBean passwordGenerator;

    private User user;

    @PostConstruct
    public void init() {
        user = new User();
    }


    /**
     * Creates a new {@link User} and sends an email to the given address
     */
    public void createNewManager() {
        user.setRole(UserRole.MANAGER);
        user.setEnabled(true);
        userService.saveUser(user);
        logger.info("Created and saved a new user");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
