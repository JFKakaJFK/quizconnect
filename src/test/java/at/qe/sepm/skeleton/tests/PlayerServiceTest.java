package at.qe.sepm.skeleton.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.UserService;

/**
 * Tests for the PlayerService.
 * 
 * @author Lorenz_Smidt
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class PlayerServiceTest
{
	@Autowired
	PlayerService playerService;
	
	@Autowired
	UserService userService;
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testLoadAllPlayers()
	{
		Collection<Player> players = playerService.getAllPlayers();
		assertEquals("Wrong number of players loaded!", 6, players.size());
		
		for (Player player : players)
		{
			assertNotNull("Player creator is null!", player.getCreator());
			assertNotNull("Player user is null!", player.getUser());
		}
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testGetPlayerById()
	{
		Player player = playerService.getPlayerById(202);
		assertNotNull("Player not loaded!", player);
		assertEquals("Wrong Player loaded!", new Integer(202), player.getId());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testGetPlayerByUsername()
	{
		Player player = playerService.getPlayerByUsername("user4");
		assertNotNull("Player not loaded!", player);
		assertEquals("Wrong Player loaded!", new Integer(202), player.getId());
	}

	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testGetPlayersOfManager()
	{
		User m = userService.loadUser("user1");

		List<Player> players = playerService.getPlayersOfManager(m.getManager());
		assertNotNull("Players not loaded!", players);
		assertEquals("Wrong number of Players loaded", 3, players.size());

	}
	
	@Test
	@WithMockUser(username = "user3", authorities = { "PLAYER" })
	public void testGetLastPlayedWith1()
	{
		User user = userService.loadUser("user3");
		Player player = user.getPlayer();
		assertNotNull("Player not loaded!", player);
		
		List<String> players = player.getPlayedWithLast();
		assertNotNull("List is null!", players);
		assertEquals("Wrong number of last played with player names!", 3, players.size());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testGetLastPlayedWith2()
	{
		Player player = playerService.getPlayerById(202);
		
		List<String> players = player.getPlayedWithLast();
		assertNotNull("List is null!", players);
		assertEquals("Wrong number of last played with player names!", 2, players.size());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testGetLastPlayedWith3()
	{
		Player player = playerService.getPlayerById(203);
		
		List<String> players = player.getPlayedWithLast();
		assertNotNull("List is null!", players);
		assertEquals("Wrong number of last played with player names!", 0, players.size());
	}
	
	@DirtiesContext
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testAddPlayedWith()
	{
		Player player = playerService.getPlayerById(205);
		Player player2 = playerService.getPlayerById(201);
		player.addToPlayedWithLast(player2);
		
		playerService.savePlayer(player);
		
		Player player3 = playerService.getPlayerById(205);
		assertNotNull("List is null!", player3.getPlayedWithLast());
		assertEquals("Wrong number of last players saved!", 1, player3.getPlayedWithLast().size());
		assertEquals("Wrong player name saved!", "user3", player3.getPlayedWithLast().get(0));
	}
	
	@DirtiesContext
	@Test
	@WithMockUser(username = "user3", authorities = { "PLAYER" })
	public void testUpdatePlayer()
	{
		User user = userService.loadUser("user3");
		assertNotNull("User not loaded!", user);
		Player player = user.getPlayer();
		assertNotNull("Player not found!", player);
		
		player.setAvatarPath("newPath");
		
		playerService.savePlayer(player);
		
		User user2 = userService.loadUser("user3");
		Player player2 = user2.getPlayer();
		assertEquals("User still has old Player!", "newPath", player2.getAvatarPath());
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user3", authorities = { "PLAYER" })
	public void testUpdatePlayerNoCreator()
	{
		User user = userService.loadUser("user3");
		assertNotNull("User not loaded!", user);
		Player player = user.getPlayer();
		assertNotNull("Player not found!", player);
		
		player.setCreator(null);
		
		playerService.savePlayer(player);
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user2", authorities = { "MANAGER" })
	public void testUpdatePlayerNoUser()
	{
		Player player = playerService.getPlayerById(202);
		assertNotNull("Player not loaded!", player);
		
		player.setUser(null);
		
		playerService.savePlayer(player);
	}
	
	@DirtiesContext
	@Test
	@WithMockUser(username = "user2", authorities = { "MANAGER" })
	public void testCreatePlayer()
	{
		Manager manager = userService.loadUser("user2").getManager();
		assertNotNull("Manager not loaded!", manager);
		
		Player player = new Player();
		player.setCreator(manager);
		player.setAvatarPath("newAvatar OwO");
		
		Player savedPlayer = playerService.saveNewPlayer(player, "newUsername", "pw");
		assertNotNull("Saved player is null!", savedPlayer);
		
		assertEquals("Saved player wrong creator!", manager, savedPlayer.getCreator());
		assertEquals("Saved player wrong avatar!", "newAvatar OwO", savedPlayer.getAvatarPath());
		
		User user = savedPlayer.getUser();
		assertNotNull("Saved player user is null!", user);
		
		assertEquals("User wrong role!", UserRole.PLAYER, user.getRole());
		assertEquals("User wrong username!", "newUsername", user.getUsername());
		assertEquals("User wrong password!", "pw", user.getPassword());
	}
	
	@DirtiesContext
	@Test(expected = org.springframework.security.access.AccessDeniedException.class)
	@WithMockUser(username = "user3", authorities = { "PLAYER" })
	public void testUnauthorizedCreatePlayer()
	{
		Player player = new Player();
		playerService.saveNewPlayer(player, "username", "pw");
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user2", authorities = { "MANAGER" })
	public void testCreatePlayerNoCreator()
	{
		Player player = new Player();
		player.setAvatarPath("newAvatar OwO");
		
		playerService.saveNewPlayer(player, "username", "pw");
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user2", authorities = { "MANAGER" })
	public void testCreatePlayerNoUsername()
	{
		Manager manager = userService.loadUser("user2").getManager();
		assertNotNull("Manager not loaded!", manager);
		
		Player player = new Player();
		player.setCreator(manager);
		player.setAvatarPath("newAvatar OwO");
		
		playerService.saveNewPlayer(player, null, "pw");
	}
	
	@DirtiesContext
	@Test(expected = IllegalArgumentException.class)
	@WithMockUser(username = "user2", authorities = { "MANAGER" })
	public void testCreatePlayerNoPassword()
	{
		Manager manager = userService.loadUser("user2").getManager();
		assertNotNull("Manager not loaded!", manager);
		
		Player player = new Player();
		player.setCreator(manager);
		player.setAvatarPath("newAvatar OwO");
		
		playerService.saveNewPlayer(player, "username", null);
	}
	
	@DirtiesContext
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testDeletePlayer()
	{
		Player player = playerService.getPlayerById(201);
		assertNotNull("Player not loaded!", player);
		
		playerService.deletePlayer(player);
		
		Player player2 = playerService.getPlayerById(201);
		assertNull("Player not deleted!", player2);
		
		Collection<Player> players = playerService.getAllPlayers();
		assertEquals("Wrong number of players in DB!", 5, players.size());
	}
}
