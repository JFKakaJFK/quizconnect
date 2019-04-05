package at.qe.sepm.skeleton.repositories;

import java.util.List;

import at.qe.sepm.skeleton.model.Player;

/**
 * Repository for managing {@link Player} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface PlayerRepository extends AbstractRepository<Player, Integer>
{
	List<Player> findByUserUsernameContaining(String fragment);
}