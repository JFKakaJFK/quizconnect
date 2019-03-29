package at.qe.sepm.skeleton.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionType;

/**
 * Repository for managing {@link Question} entities.
 * 
 * @author Lorenz_Smidt
 *
 */
public interface QuestionRepository extends AbstractRepository<Question, Integer>
{
	List<Question> findByType(QuestionType type);
	
	/**
	 * Method for finding questions with similar answers (experimental, not yet tested and somewhat unlikely to work)
	 * 
	 * @param text
	 * @return
	 */
	@Query("SELECT q FROM Question q WHERE q.rightAnswerString LIKE %?1% OR q.wrongAnswerString_1 LIKE %?1% OR q.wrongAnswerString_2 LIKE %?1% OR q.wrongAnswerString_3 LIKE %?1% OR q.wrongAnswerString_4 LIKE %?1% OR q.wrongAnswerString_5 LIKE %?1% ")
	List<Question> findAllByAnswersContaining(String text);
	
	List<Question> findByQuestionStringContaining(String text);
}