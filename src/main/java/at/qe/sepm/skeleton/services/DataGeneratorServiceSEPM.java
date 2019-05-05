package at.qe.sepm.skeleton.services;


import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.utils.AuthenticationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Service for generating test data
 *
 * @author Simon Triendl
 */

@Component
@Scope("application")
public class DataGeneratorServiceSEPM {

    public static final boolean enabled = true;

    private final int countMultiplier = 1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    ManagerService managerService;

    @Autowired
    PlayerService playerService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionSetService questionSetService;

    @Autowired
    QuestionSetPerformanceService questionSetPerformanceService;


    public void generateData(){
        if(!enabled){
            return;
        }

        Random random = new Random();

        AuthenticationUtil.configureAuthentication("MANAGER");

        generatePlayer(random.nextInt(20 * countMultiplier) + (30 * countMultiplier));

        generateManager(random.nextInt(5 * countMultiplier) + (10 * countMultiplier));

        AuthenticationUtil.clearAuthentication();

    }

    /**
     * Method to generate a User
     * Formally used  at generatePlayer and generateManager
     * Not needed since saveNewPlayer and saveNewManager create a User
     * @param numUser
     * @return
     */
    private User generateUser(int numUser){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);

        User user = new User();
        user.setUsername("GeneratedUser" + numUser);
        user.setPassword(passwordEncoder.encode("passwd" + numUser));
        user.setEnabled(true);

        userService.saveUser(user);

        return user;
    }


    private void generatePlayer(int count){
        for (int i = 1; i <= count; i++){

            Manager creator = managerService.getManagerById(101);

            Player player = new Player();
            player.setCreator(creator);

            playerService.saveNewPlayer(player, "GeneratedName" + i, "passwd" + i);
        }

        logger.info(count + " Player have been created");
    }

    private void generateManager(int count){
        for (int i = 0; i <= count; i++){

            Manager manager = new Manager();
            manager.setEmail("generatedMail@test.com" + i);
            manager.setInstitution("generatedInst" + i);

            managerService.saveNewManager(manager, "passwd" + i);
        }

        logger.info(count + " Manager have been created");
    }
}
