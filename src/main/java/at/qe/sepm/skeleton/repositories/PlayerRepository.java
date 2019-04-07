package at.qe.sepm.skeleton.repositories;

import at.qe.sepm.skeleton.model.Player;

/**
 * Repository for managing {@link Player} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface PlayerRepository extends AbstractRepository<Player, Integer>
{
	Player findByUserUsername(String username);
}