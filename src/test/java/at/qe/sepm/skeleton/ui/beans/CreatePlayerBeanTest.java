package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.tests.WebContextTestExecutionListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@TestExecutionListeners( { WebContextTestExecutionListener.class})
public class CreatePlayerBeanTest {

    @Autowired
    private CreatePlayerBean createPlayerBean;

    @Test
    public void testGetUsername(){
        createPlayerBean.setUsername("UserToTest");
        Assert.assertEquals(createPlayerBean.getUsername(), "UserToTest");
    }

}