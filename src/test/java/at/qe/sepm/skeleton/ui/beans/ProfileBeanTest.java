package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ProfileBeanTest {

    @Autowired
    private ProfileBean profileBean;

    @Autowired
    private PlayerService playerService;

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSetPlayer(){
        Player testPlayer = playerService.getPlayerById(201);
        profileBean.setPlayer(testPlayer);
        Assert.assertNotNull(profileBean.getPlayer());
    }

    @Test
    public void testGetAndSetter(){
        Assert.assertEquals(profileBean.getId(), 0);
        profileBean.setId(201);
        Player testPlayer = playerService.getPlayerById(201);
        Assert.assertEquals(profileBean.getPlayer(), testPlayer);
        List<Player> playerList = new ArrayList<>();
        profileBean.setRecentlyPlayedWith(playerList);
        Assert.assertTrue(profileBean.getRecentlyPlayedWith().isEmpty());

    }
}