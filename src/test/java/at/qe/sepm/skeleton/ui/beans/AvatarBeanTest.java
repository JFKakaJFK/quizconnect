package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AvatarBeanTest {

    @Autowired
    private AvatarBean avatarBean;

    @Autowired
    private PlayerService playerService;



    @Test
    public void getAvatar() {
        Assert.assertNotNull(avatarBean.getAvatar(null, "user1"));
        Assert.assertNotNull(avatarBean.getAvatar("thisIsNoPath", "user1"));

        Player testPlayer = playerService.getPlayerById(201);

        Assert.assertNotNull(avatarBean.getAvatar(testPlayer.getAvatarPath(), "user3"));
    }

    @Test
    public void getAvatar1() {
        Assert.assertEquals(avatarBean.getAvatar(null), "");

        Player testPlayer = playerService.getPlayerById(201);

        Assert.assertNotNull(avatarBean.getAvatar(testPlayer));

        testPlayer.setAvatarPath(null);

        Assert.assertNotNull(avatarBean.getAvatar(testPlayer));
    }
}