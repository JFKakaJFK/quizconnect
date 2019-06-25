package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests PlayerDetailController
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PlayerDetailControllerTest {

    @Mock
    private PlayerDetailController playerDetailController;

    @Mock
    private PlayerService playerService;

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void deletePlayer() {
        Player testPlayer = playerService.getPlayerById(201);
        playerDetailController.setPlayer(testPlayer);
        playerDetailController.deletePlayer();
        Assert.assertNull(playerService.getPlayerById(201));
    }
}