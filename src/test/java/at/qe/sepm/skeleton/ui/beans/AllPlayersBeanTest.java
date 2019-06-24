package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AllPlayersBeanTest {

    @Autowired
    private AllPlayersBean allPlayersBean;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private PlayerService  playerService;

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void isManager() {
        User user = managerService.getManagerById(101).getUser();
        allPlayersBean.setUser(user);
        Assert.assertTrue(allPlayersBean.isManager());
    }

    @Test
    public void testGetAndSetSearchPhrase(){
        String testPhrase = "ThisIsAPhrase";
        allPlayersBean.setSearchPhrase(null);
        Assert.assertNull(allPlayersBean.getSearchPhrase());
        allPlayersBean.setSearchPhrase(testPhrase);
        Assert.assertEquals(allPlayersBean.getSearchPhrase(), testPhrase);
    }

    @Test
    public void testGetAndSetPaginator(){
        Assert.assertNotNull(allPlayersBean.getPaginator());
        allPlayersBean.setPaginator(null);
        Assert.assertNull(allPlayersBean.getPaginator());

    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testRemoveAndAddPlayer(){
        User testUser = playerService.getPlayerById(201).getUser();
        allPlayersBean.setUser(testUser);
        Player testPlayer = playerService.getPlayerById(202);
        allPlayersBean.addPlayer(testPlayer);
        Assert.assertTrue(allPlayersBean.getAllPlayers().contains(testPlayer));
        allPlayersBean.removePlayer(testPlayer);
        allPlayersBean.removePlayer(testPlayer); //has to be deleted twice since it is standardized in list
        Assert.assertFalse(allPlayersBean.getAllPlayers().contains(testPlayer));

    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetAllByManager(){
        User testUser = playerService.getPlayerById(201).getUser();
        allPlayersBean.setUser(testUser);
        List<Player> testList = allPlayersBean.getAllByManager();
        Assert.assertNull(testList);
        testUser = managerService.getManagerById(101).getUser();
        allPlayersBean.setUser(testUser);
        testList = allPlayersBean.getAllByManager();
        Assert.assertNotNull(testList);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testIsEditable(){
        User testUser = playerService.getPlayerById(201).getUser();
        allPlayersBean.setUser(testUser);
        Assert.assertTrue(allPlayersBean.isEditable(testUser.getPlayer()));
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testIsDeletable(){
        User testUser = playerService.getPlayerById(201).getUser();
        allPlayersBean.setUser(testUser);
        Assert.assertFalse(allPlayersBean.isDeletable(testUser.getPlayer()));
    }


}