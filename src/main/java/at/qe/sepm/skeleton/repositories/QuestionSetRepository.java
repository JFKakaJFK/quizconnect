package at.qe.sepm.skeleton.repositories;

import java.util.List;

import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;

/**
 * Repository for managing {@link QuestionSet} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface QuestionSetRepository extends AbstractRepository<QuestionSet, Integer>
{
	List<QuestionSet> findByDifficulty(QuestionSetDifficulty difficulty);
	
	List<QuestionSet> findByNameContaining(String name);
}