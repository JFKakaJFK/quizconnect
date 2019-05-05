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


import java.net.Authenticator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

        generateUser(random.nextInt(20 * countMultiplier) + (30 * countMultiplier));

        AuthenticationUtil.clearAuthentication();

    }

    private void generateUser(int count){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.PLAYER);
        for (int i = 1; i <= count; i++){

            Manager manager = managerService.getManagerById(101);

            User user = new User();
            user.setUsername("GeneratedUser" + i);
            user.setPassword(passwordEncoder.encode("passwd" + i));
            user.setEnabled(true);
            user.setManager(manager);
            userService.saveUser(user);

            //if (i == (int) (count * 0.1)) {
             //   roles.add(UserRole.MANAGER);
            //}
        }

        logger.info("> generated " + count + " users");
    }
}
