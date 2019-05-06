package at.qe.sepm.skeleton.repositories;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;

/**
 * Repository for managing {@link Manager} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface ManagerRepository extends AbstractRepository<Manager, Integer>
{
	Manager findByCreatedPlayers(Player player);

	Manager findByCreatedQuestionSets(QuestionSet questionSet);
}