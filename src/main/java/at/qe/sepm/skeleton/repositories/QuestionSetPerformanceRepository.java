package at.qe.sepm.skeleton.repositories;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetPerformance;

/**
 * Repository for managing {@link QuestionSetPerformance} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface QuestionSetPerformanceRepository extends AbstractRepository<QuestionSetPerformance, Integer>
{
	QuestionSetPerformance findByPlayerAndQuestionSet(Player player, QuestionSet questionSet);
}