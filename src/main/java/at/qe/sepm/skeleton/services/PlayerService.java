package at.qe.sepm.skeleton.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.repositories.PlayerRepository;

/**
 * Service for accessing and manipulating {@link Player} entities.
 *
 * @author Lorenz_Smidt
 */
@Component
@Scope("application")
public class PlayerService
{
	@Autowired
	private PlayerRepository playerRepository;
	
	@Autowired
	UserService userService;
	
	/**
	 * @return A Collection of all {@link Player}s in the database.
	 */
	public Collection<Player> getAllPlayers()
	{
		return playerRepository.findAll();
	}
	
	/**
	 * @param manager
	 * 		Manager to get the created Players of.
	 * @return A list of all {@link Player}s created by a {@link Manager}.
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public List<Player> getPlayersOfManager(Manager manager)
	{
		return playerRepository.findByCreator(manager);
	}
	
	/**
	 * @param id
	 * 		Id of the Player to be found.
	 * @return The Player with id or null if none was found.
	 */
	public Player getPlayerById(int id)
	{
		return playerRepository.findOne(id);
	}
	
	/**
	 * @param username
	 * 		Username of the Player ot be found.
	 * @return The Player with username or null if none was found.
	 */
	@PreAuthorize("hasAuthority('MANAGER') or hasAuthority('PLAYER')")
	public Player getPlayerByUsername(String username)
	{
		return playerRepository.findByUserUsername(username);
	}
	
	/**
	 * Saves the {@link Player} to the database. Throws an IllegalArgumentException if any consistency checks fail.
	 *
	 * @param player
	 * 		Player to be saved.
	 * @return new Player reference. Use for all further operations.
	 * @throws IllegalArgumentException
	 * 		If the player is new, the associated user is null, or if any sanity checks fail.
	 */
	public Player savePlayer(Player player) throws IllegalArgumentException
	{
		if (player.isNew())
			throw new IllegalArgumentException("Player to be saved cannot be new! Use 'saveNewPlayer' to save a new Player!");
		else if (player.getUser() == null)
			throw new IllegalArgumentException("Player user cannot be null!");
		
		playerSanityChecks(player);
		
		return playerRepository.save(player);
	}
	
	/**
	 * Saves a new {@link Player} to the database. Automatically creates a new {@link User} for the player using username and password. Password is expected to be already
	 * encrypted. Performs consistency
	 * checks which may throw IllegalArgumentExceptions. For re-saving / updating {@link Player}s use the 'savePlayer' function.
	 *
	 * @param player
	 * 		New Player to be saved.
	 * @param username
	 * 		Username of the player to be saved.
	 * @param password
	 * 		Encoded password of the player to be saved.
	 * @return new Player reference. Use for all further operations.
	 * @throws IllegalArgumentException
	 * 		If the player is not new, the username is null, the password is null, or if any sanity checks fail.
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public Player saveNewPlayer(Player player, String username, String password) throws IllegalArgumentException
	{
		if (!player.isNew())
			throw new IllegalArgumentException("Player to be saved must be new!");
		else if (username == null)
			throw new IllegalArgumentException("Player username cannot be null!");
		else if (password == null)
			throw new IllegalArgumentException("Player password cannot be null!");
		
		playerSanityChecks(player);
		
		User newUser = new User();
		newUser.setUsername(username);
		newUser.setPassword(password);
		newUser.setEnabled(true);
		User savedUser = userService.saveUser(newUser);
		
		player.setUser(savedUser); // set player user
		player.setPlayedWithLast(null);
		player.setqSetPlayCounts(new HashMap<>());
		player.setLastScores(new HashMap<>());
		player.setHighScore(0);
		Player savedPlayer = playerRepository.save(player); // save player to DB
		
		savedUser.setPlayer(savedPlayer); // automatically sets role
		userService.saveUser(savedUser); // update user in DB
		
		return savedPlayer;
	}
	
	/**
	 * Performs sanity checks on the player. Throws an IllegalArgumentException if any checks fail, terminates otherwise.
	 *
	 * @param player
	 * 		Player to be checked.
	 * @throws IllegalArgumentException
	 * 		If the creator is null.
	 */
	private void playerSanityChecks(Player player) throws IllegalArgumentException
	{
		if (player.getCreator() == null)
			throw new IllegalArgumentException("Player creator cannot be null!");
	}
	
	/**
	 * Deletes the {@link Player} from the database.
	 *
	 * @param player
	 * 		Player to be deleted.
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public void deletePlayer(Player player)
	{
		playerRepository.delete(player);
	}
	
}