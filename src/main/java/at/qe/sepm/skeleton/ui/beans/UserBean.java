package at.qe.sepm.skeleton.ui.beans;
import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.services.MailSenderService;
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
    private MailSenderService mailSenderService;


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

        /* Email sender - disabled until release */

        /*mailSenderService.sendUserMail(manager.getEmail(),
                "Quizconnect: Account",
                "Hey hey heeeeeeeeeey… Hey hey heeeeeeeeeey… Hey hey heeeeeeeeeey.\n\n" +
                        "What's-a what's-a what's-a what's-a what's-a what's-a what's-a what's-a what's UP, QUIZCONNEEEEEEEEEEEECT!\n\n" +
                        "Hey hey hey everybody, My name is Johannes and we are coming from Innsbruck, Tirol!\n\n" +
                        "Let me tell you guys that I am SO EXCITED, I am SO HAPPY I am really so thrilled to be right now!\n\n" +
                        "Sharing this amazing, glorious, SUPER and EXCITING moment of my life with all of you guys—and let me tell you that we are really changing the WORLD as we know it. The WORLD is not anymore the way it used to be, mm mm MM, NO NO NOH!\n\n" +
                        "QuizcoNNEEEEEEEEEEEEEEEEECT! WOOOoo... quizconnEEEEEEEEEEEEEEEEEEEEECT!\n\n" +
                        "We are coming and we are coming in waves. We are starting and to actually go all over the world. We all built in the entire world. Let me tell you guys that I started 137 days ago, with only 25 thousand six hundred and ten dollars—and right now I am reaching one hundred and forty THOUSAND dollars!\n\n" +
                        "WOAH WOAH WOAH WOAH WOAH WOAH WOAH WHAT'S UP!\n\n" +
                        "So guys, I wanna tell you something: Faith and belief is the one thing we will need to be able to change the world. And right now, I believe, that in this room, we have the seed, that’s gonna germinate, and that is going to EXPLODE, into an AMAZING opportunity for us to change this entire world!\n\n" +
                        "I am so proud, I am so honored, I am so EXCITED to be here right now—and, hey, let me tell you something: that each and every one of YOU, has the opportunity to become, like those amazing people that we know here FROM VIETNAM! HEY HEY, MY PEOPLE HERE FROM VIETNAM, making so much money that they can probably have a real hard time counting it!\n\n\n" +
                        "AHAHAHAHAHAh!\n\n\n" +
                        "So GUYS, let me tell you: I LOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOVE QUIZ CON EEEEEEECT\n\n\n" +
                        "http://www.quizconnect.rocks");
        */
        redirectRegistration();
    }

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
