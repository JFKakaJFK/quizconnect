package at.qe.sepm.skeleton.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Player;
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
	
	/**
	 * Returns a Collection of all {@link Player}s.
	 * 
	 * @return
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public Collection<Player> getAllPlayers()
	{
		return playerRepository.findAll();
	}
	
	/**
	 * Returns a {@link Player} based on its id.
	 * 
	 * @param id
	 * @return
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public Player getPlayerById(int id)
	{
		return playerRepository.findOne(id);
	}
	
	/**
	 * Saves the {@link Player} to the database. Throws an IllegalArgumentException if any consistency checks fail.
	 * 
	 * @param player
	 * @throws IllegalArgumentException
	 */
	public void savePlayer(Player player) throws IllegalArgumentException
	{
		if (player.getUser() == null)
			throw new IllegalArgumentException("Player user cannot be null!");
		else if (player.getCreator() == null)
			throw new IllegalArgumentException("Player creator cannot be null!");
		
		playerRepository.save(player);
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