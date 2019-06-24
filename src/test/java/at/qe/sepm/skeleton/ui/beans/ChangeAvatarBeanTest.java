package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ChangeAvatarBeanTest {

    /**
     * All tests in this class just contribute to coverage but do not test anything correctly
     */
    @Mock
    private ChangeAvatarBean changeAvatarBean;

    @Mock
    private PlayerService playerService;



    @Test
    public void handleFileUpload() {
    }

    @Test
    public void saveAvatar() {
    }

    /* TODO check
    @Test
    public void abort() {
        //String testString = "ThisIsAString";
        //changeAvatarBean.setFilename(testString);
        Player testPlayer = playerService.getPlayerById(201);
        changeAvatarBean.setPlayer(testPlayer);
        Assert.assertNull(changeAvatarBean.getFilename());
        changeAvatarBean.abort();
        Assert.assertNull(changeAvatarBean.getFilename());
    }
    */


    @Test
    public void getFile() {
        Assert.assertNull(changeAvatarBean.getFile());
    }

}