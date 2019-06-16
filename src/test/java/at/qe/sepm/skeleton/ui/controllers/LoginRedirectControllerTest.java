package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.ui.beans.SessionInfoBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginRedirectControllerTest {

    @Autowired
    private LoginRedirectController loginRedirectController;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private ManagerService managerService;


    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void redirect() throws IOException {
        User testUser = managerService.getManagerById(101).getUser();
        sessionInfoBean.setCurrentUser(testUser);
        loginRedirectController.redirect();

    }
}