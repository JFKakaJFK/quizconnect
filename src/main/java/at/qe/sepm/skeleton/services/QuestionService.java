package at.qe.sepm.skeleton.services;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionType;
import at.qe.sepm.skeleton.repositories.QuestionRepository;

/**
 * Service for accessing and manipulating {@link Question} entities.
 *
 * @author Johannes Koch
 */
@Component
@Scope("application")
public class QuestionService
{
	
	private Logger log = LoggerFactory.getLogger(QuestionService.class);
	
	@Autowired
	QuestionRepository questionRepository;
	
	/**
	 * @param id
	 * 		Id of the Question to find.
	 * @return The Question with id or null if none was found.
	 */
	public Question getById(Integer id)
	{
		return questionRepository.findOne(id);
	}
	
	/**
	 * @param type
	 * 		Type of Questions to be returned.
	 * @return All {@link Question} of a {@link QuestionType}.
	 */
	public Collection<Question> getAllByType(QuestionType type)
	{
		return questionRepository.findByType(type);
	}
	
	/**
	 * @param text
	 * 		Text to be contained in the rightAnswerString of the Questions.
	 * @return All {@link Question} where the {@link Question#rightAnswerString} contains the search phrase.
	 */
	public Collection<Question> getAllByAnswerContaining(String text)
	{
		return questionRepository.findAllByAnswersContaining(text);
	}
	
	/**
	 * @param text
	 * 		Text to be contained in the questionString of the Questions.
	 * @return All {@link Question} where the {@link Question#questionString} contains a search phrase.
	 */
	public Collection<Question> getAllByQuestionContaining(String text)
	{
		return questionRepository.findByQuestionStringContaining(text);
	}
	
	/**
	 * Saves a {@link Question} to the database.
	 *
	 * @param question
	 * 		Question to be saved.
	 * @return new Question reference. Use for all further operations.
	 * @throws IllegalArgumentException
	 * 		If any sanity checks fail.
	 */
	public Question saveQuestion(Question question) throws IllegalArgumentException
	{
		// is the context valid?
		if (question == null)
		{
			throw new IllegalArgumentException("Question cannot be null");
		}
		if (question.getQuestionSet() == null)
		{
			throw new IllegalArgumentException("Question must be associated to a QuestionSet");
		}
		
		// is the question itself valid?
		if (question.getType() == null)
		{
			throw new IllegalArgumentException("Question Type can not be null");
		}
		if (question.getQuestionString() == null)
		{
			throw new IllegalArgumentException("Question can not be null");
		}
		if (question.getQuestionString().length() > 200)
		{
			throw new IllegalArgumentException("Question is too long(MAX: 200Chars)");
		}
		
		if (question.getRightAnswerString() == null)
		{
			throw new IllegalArgumentException("Answer can not be null");
		}
		if (question.getRightAnswerString().length() > 200)
		{
			throw new IllegalArgumentException("Answer is too long(MAX: 200Chars)");
		}
		
		// check wrong answers
		if (question.getWrongAnswerString_1() == null)
		{
			throw new IllegalArgumentException("At least one wrong answer is required");
		}
		if (question.getWrongAnswerString_1() != null && question.getWrongAnswerString_1().length() > 200)
		{
			throw new IllegalArgumentException("Wrong Answer 1 is too long(MAX: 200Chars)");
		}
		if (question.getWrongAnswerString_2() != null && question.getWrongAnswerString_2().length() > 200)
		{
			throw new IllegalArgumentException("Wrong Answer 2 is too long(MAX: 200Chars)");
		}
		if (question.getWrongAnswerString_3() != null && question.getWrongAnswerString_3().length() > 200)
		{
			throw new IllegalArgumentException("Wrong Answer 3 is too long(MAX: 200Chars)");
		}
		if (question.getWrongAnswerString_4() != null && question.getWrongAnswerString_4().length() > 200)
		{
			throw new IllegalArgumentException("Wrong Answer 4 is too long(MAX: 200Chars)");
		}
		if (question.getWrongAnswerString_5() != null && question.getWrongAnswerString_5().length() > 200)
		{
			throw new IllegalArgumentException("Wrong Answer 5 is too long(MAX: 200Chars)");
		}
		
		return questionRepository.save(question);
	}
	
	/**
	 * Deletes a {@link Question} from the database.
	 *
	 * @param question
	 * 		Question to be deleted.
	 */
	public void deleteQuestion(Question question)
	{
		questionRepository.delete(question);
		log.info("Deleted Question " + question.getId());
	}
	
}
