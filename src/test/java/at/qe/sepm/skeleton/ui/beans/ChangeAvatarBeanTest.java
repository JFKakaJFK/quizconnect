package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ChangeAvatarBeanTest {

    @Autowired
    private ChangeAvatarBean changeAvatarBean;

    @Autowired
    private PlayerService playerService;



    @Test
    public void handleFileUpload() {
    }

    @Test
    public void saveAvatar() {
    }

    /*
    @Test
    public void abort() {
        //String testString = "ThisIsAString";
        //changeAvatarBean.setFilename(testString);
        Player testPlayer = playerService.getPlayerById(201);
        changeAvatarBean.setPlayer(testPlayer);
        Assert.assertNotNull(changeAvatarBean.getFilename());
        changeAvatarBean.abort();
        Assert.assertNull(changeAvatarBean.getFilename());
    }

     */

    @Test
    public void getPlayer() {
    }

    @Test
    public void setPlayer() {
    }

    @Test
    public void getDisabled() {
    }

    @Test
    public void setDisabled() {
    }

    @Test
    public void getFilename() {
    }

    @Test
    public void setFilename() {
    }

    @Test
    public void getFile() {
    }

    @Test
    public void setFile() {
    }
}