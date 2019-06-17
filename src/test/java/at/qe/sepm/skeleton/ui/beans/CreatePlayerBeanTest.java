package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.repositories.UserRepository;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.UserService;
import at.qe.sepm.skeleton.tests.WebContextTestExecutionListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CreatePlayerBeanTest {


    @Mock
    private CreatePlayerBean createPlayerBean;

    @Mock
    private SessionInfoBean sessionInfoBean;

    @Mock
    private ManagerService managerService;


    @Test
    public void testGetUsername(){
        createPlayerBean.setUsername("UserToTest");
        Assert.assertNull(createPlayerBean.getUsername());
    }
}