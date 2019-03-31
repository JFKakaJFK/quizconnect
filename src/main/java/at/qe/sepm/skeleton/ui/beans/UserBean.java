package at.qe.sepm.skeleton.ui.beans;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.Manager;

import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.services.ManagerService;
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
    private PasswordBean passwordGenerator;

    private User user;
    private Manager manager;

    @PostConstruct
    public void init() {
        user = new User();
        manager = new Manager();
    }


    /**
     * Creates a new {@link User} and sends an email to the given address
     */
    public void createNewManager() {
        manager.setInstitution("signup-test-institution");

        user.setUsername(manager.getEmail());
        user.setManager(manager);
        user.setEnabled(true);
        userService.saveNewManager(user);

        logger.info("Created and saved a new user: " + user.toString() + " with privileges: " + user.getRole());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
}
