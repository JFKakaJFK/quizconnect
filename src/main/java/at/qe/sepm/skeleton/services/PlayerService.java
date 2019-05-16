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
 *
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
	 * Returns a Collection of all {@link Player}s.
	 * 
	 * @return
	 */
	//@PreAuthorize("hasAuthority('MANAGER')")
	public Collection<Player> getAllPlayers()
	{
		return playerRepository.findAll();
	}

	/**
	 * Returns all {@link Player}s of the {@link Manager}
	 *
	 * @param manager
	 * @return
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public List<Player> getPlayersOfManager(Manager manager){
		return playerRepository.findByCreator(manager);
	}
	
	/**
	 * Returns a {@link Player} based on its id.
	 * 
	 * @param id
	 * @return
	 */
	//@PreAuthorize("hasAuthority('MANAGER')")
	public Player getPlayerById(int id)
	{
		return playerRepository.findOne(id);
	}
	
	/**
	 * Returns a {@link Player} by its username.
	 * 
	 * @param username
	 * @return
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
	 * @throws IllegalArgumentException
	 * @return new Player reference. Use for all further operations.
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
	 * Saves a new {@link Player} to the database. Automatically creates a new {@link User} for the player using username and password. Password is expected to be already encrypted. Performs consistency
	 * checks which may throw IllegalArgumentExceptions. For re-saving / updating {@link Player}s use the 'savePlayer' function.
	 * 
	 * @param player
	 * @param username
	 * @param password
	 * @throws IllegalArgumentException
	 * @return new Player reference. Use for all further operations.
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
		player.setqSetPlayCounts(new HashMap<Integer, Integer>());
		player.setLastScores(new HashMap<Long, Integer>());
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
	 * @throws IllegalArgumentException
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
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public void deletePlayer(Player player)
	{
		playerRepository.delete(player);
	}

}