package at.qe.sepm.skeleton.repositories;

import java.util.List;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetPerformance;

/**
 * Repository for managing {@link QuestionSetPerformance} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
@Deprecated
public interface QuestionSetPerformanceRepository extends AbstractRepository<QuestionSetPerformance, Integer>
{
	QuestionSetPerformance findByPlayerAndQuestionSet(Player player, QuestionSet questionSet);
	
	// replaces Player.qSetPerformances lazy loading
	List<QuestionSetPerformance> findByPlayer(Player player);
}