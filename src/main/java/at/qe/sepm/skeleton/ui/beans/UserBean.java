package at.qe.sepm.skeleton.ui.beans;
import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.services.ManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Bean to help to create a new user object
 *
 * @author Johannes Spies
 */
@Component
@Scope("request")
public class UserBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ManagerService managerService;

    @Autowired
    private PasswordBean passwordBean;

    private String password;
    private Manager manager;

    @PostConstruct
    public void init() {
        manager = new Manager();
    }

    /**
     * Creates a new {@link Manager} (and sends an email to the given address)
     */
    public void createNewManager() {
        managerService.saveNewManager(manager, passwordBean.encodePassword(password));
        logger.info("Created and saved a new manager: " + manager.toString());
        redirectRegistration();
    }

    //maybe better possibility for redirect?
    /*@RequestMapping(value = "/signup.xhtml", method = RequestMethod.POST)
    public String handle() {
        // Save account ...
        return "redirect:login.xhtml";
    }
    */

    //TODO: JavaDoc for redirectRegistration
    public void redirectRegistration() {
        try {
            FacesContext.getCurrentInstance().
                    getExternalContext().redirect("/login.xhtml?registration=success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
}
