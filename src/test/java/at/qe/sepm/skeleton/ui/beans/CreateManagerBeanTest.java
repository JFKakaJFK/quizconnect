package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import java.io.IOException;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class CreateManagerBeanTest {

    @Autowired
    private CreateManagerBean createManagerBean;

    @Autowired
    private ManagerService managerService;

    @Before
    public void initialization(){
        createManagerBean.init();
    }
    @Test
    public void init() {
        Assert.assertNotNull(createManagerBean.getManager());
    }

    @Test
    public void createNewManager() {
    }

    @Test (expected = NullPointerException.class)
    public void redirectRegistration() {
        createManagerBean.redirectRegistration();
    }

    @Test
    public void sendRegistrationEmail() {
    }

    @Test
    public void getPassword() {
        String testString = "thisIsATestString";
        createManagerBean.setPassword(testString);
        Assert.assertNotNull(createManagerBean.getPassword());
    }

    @Test
    public void setPassword() {
        String testString = "thisIsATestString";
        createManagerBean.setPassword(testString);
        Assert.assertEquals(createManagerBean.getPassword(), testString);
    }

    @Test
    public void getManager() {
        Assert.assertNotNull(createManagerBean.getManager());
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void setManager() {
        Manager testManager = managerService.getManagerById(102);
        createManagerBean.setManager(testManager);
        Assert.assertEquals(createManagerBean.getManager(), testManager);
    }
}