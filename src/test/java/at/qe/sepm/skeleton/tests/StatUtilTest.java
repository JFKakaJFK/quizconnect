package at.qe.sepm.skeleton.tests;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.utils.StatUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests for StatUtil
 *
 * @author Simon Triendl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class StatUtilTest {

    @Autowired
    public StatUtil statUtil;

    @Autowired
    public PlayerService playerService;
    @Test
    public void testRoundPercentage(){
        float testFloat1 = 0.996f;
        float testFloat2 = 0.736f;
        Assert.assertEquals(statUtil.roundPercent(testFloat1), "99%");
        Assert.assertEquals(statUtil.roundPercent(testFloat2), "74%");
        //Assert.assertEquals(statUtil.roundPercent(null), "None");
    }

    @Test
    public void testRound(){
        float testFloat2 = 0.736f;
        Assert.assertEquals(statUtil.round(100 * testFloat2, 0), "74");
        Assert.assertEquals(statUtil.round(100 * testFloat2, 5), "73,6");
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetGamesPlayed(){
        Assert.assertEquals(statUtil.getGamesPlayed(null), 0);
        Player testPlayer = playerService.getPlayerById(203);
        Assert.assertEquals(statUtil.getGamesPlayed(testPlayer), 3);
    }

    @Test
    public void testMsToString(){
        Assert.assertEquals(statUtil.msToString(0), "0");
        Assert.assertEquals(statUtil.msToString(500), "< 1min");
        Assert.assertEquals(statUtil.msToString(240000), "4min");
        Assert.assertEquals(statUtil.msToString(3600000), "1hrs 0min");
    }
}
