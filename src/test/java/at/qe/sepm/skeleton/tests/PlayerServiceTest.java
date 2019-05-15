package at.qe.sepm.skeleton.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.QuestionSetService;
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
	
	@Autowired
	QuestionSetService questionSetService;
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testLoadAllPlayers()
	{
		Collection<Player> players = playerService.getAllPlayers();
		assertEquals("Wrong number of players loaded!", 19, players.size());
		
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
		assertEquals("Wrong number of Players loaded", 9, players.size());
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
		List<Player> ps = new ArrayList<>();
		ps.add(player2);
		player.setPlayedWithLast(ps);
		
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
		assertEquals("Wrong number of players in DB!", 18, players.size());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testAddTotalScore()
	{
		Player player = playerService.getPlayerById(201);
		assertNotNull("Player not loaded!", player);
		
		long score1 = player.getTotalScore();
		assertEquals("Loaded score is wrong!", 9096, score1);
		
		player.addToTotalScore(1274);
		long score2 = player.getTotalScore();
		assertEquals("Updated score is wrong!", 10370, score2);
		
		Player updatedPlayer = playerService.savePlayer(player);
		assertNotNull("Updated Player not loaded!", updatedPlayer);
		assertEquals("Saved Score is wrong!", 10370, updatedPlayer.getTotalScore());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testSetHighScore()
	{
		Player player = playerService.getPlayerById(201);
		assertNotNull("Player not loaded!", player);
		
		int hs1 = player.getHighScore();
		assertEquals("Loaded highscore is wrong!", 720, hs1);
		
		player.setHighScore(832);
		int hs2 = player.getHighScore();
		assertEquals("Updated highscore is wrong!", 832, hs2);
		
		Player updatedPlayer = playerService.savePlayer(player);
		assertNotNull("Updated Player not loaded!", updatedPlayer);
		assertEquals("Saved highscore is wrong!", 832, updatedPlayer.getHighScore());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testAddCorrectAnswers()
	{
		Player player = playerService.getPlayerById(201);
		assertNotNull("Player not loaded!", player);
		
		int ca1 = player.getCorrectAnswersCount();
		assertEquals("Loaded correct answers is wrong!", 18, ca1);
		
		player.AddCorrectAnswers(11);
		int ca2 = player.getCorrectAnswersCount();
		assertEquals("Updated correct answers is wrong!", 29, ca2);
		
		Player updatedPlayer = playerService.savePlayer(player);
		assertNotNull("Updated Player not loaded!", updatedPlayer);
		assertEquals("Saved correct answers is wrong!", 29, updatedPlayer.getCorrectAnswersCount());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testAddTotalAnswers()
	{
		Player player = playerService.getPlayerById(201);
		assertNotNull("Player not loaded!", player);
		
		int ta1 = player.getTotalAnswers();
		assertEquals("Loaded total answers is wrong!", 24, ta1);
		
		player.addTotalAnswers(23);
		int ta2 = player.getTotalAnswers();
		assertEquals("Updated total answers is wrong!", 47, ta2);
		
		Player updatedPlayer = playerService.savePlayer(player);
		assertNotNull("Updated Player not loaded!", updatedPlayer);
		assertEquals("Saved total answers is wrong!", 47, updatedPlayer.getTotalAnswers());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testAddPlayTime()
	{
		Player player = playerService.getPlayerById(201);
		assertNotNull("Player not loaded!", player);
		
		long pt1 = player.getPlayTime();
		assertEquals("Loaded playtime is wrong!", 743563, pt1);
		
		player.addPlayTime(23698);
		long pt2 = player.getPlayTime();
		assertEquals("Updated playtime is wrong!", 767261, pt2);
		
		Player updatedPlayer = playerService.savePlayer(player);
		assertNotNull("Updated Player not loaded!", updatedPlayer);
		assertEquals("Saved playtime is wrong!", 767261, updatedPlayer.getPlayTime());
	}
	
	@Test
	@WithMockUser(username = "user1", authorities = { "MANAGER" })
	public void testAddQSetPlays()
	{
		Player player = playerService.getPlayerById(201);
		assertNotNull("Player not loaded!", player);
		assertEquals("Map already contains entries!", 0, player.getqSetPlayCounts().size());
		
		List<QuestionSet> qsets = new ArrayList<>();
		qsets.add(questionSetService.getQuestionSetById(300));
		qsets.add(questionSetService.getQuestionSetById(301));
		
		player.addPlayToQSets(qsets);
		assertEquals("Map doesn't contain entries for newly added QuestionSets!", 2, player.getqSetPlayCounts().size());
		
		Player savedPlayer = playerService.savePlayer(player);
		assertEquals("Map doesn't contain entries for newly added QuestionSets on savedPlayer!", 2, savedPlayer.getqSetPlayCounts().size());
		
		Map<Integer, Integer> map = savedPlayer.getqSetPlayCounts();
		assertEquals("Map missing entry for QSet 300", true, map.containsKey(300));
		assertEquals("Map missing entry for QSet 301", true, map.containsKey(301));
		
		assertEquals("Map has wrong number for QSet 300", new Integer(1), map.get(300));
		assertEquals("Map has wrong number for QSet 301", new Integer(1), map.get(301));
	}
}
