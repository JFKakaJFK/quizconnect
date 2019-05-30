package at.qe.sepm.skeleton.services;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;
import at.qe.sepm.skeleton.model.QuestionType;
import at.qe.sepm.skeleton.repositories.QuestionSetRepository;

/**
 * Service for accessing and manipulating {@link Question} entities.
 *
 * @author Johannes Koch
 */
@Component
@Scope("application")
public class QuestionSetService
{
	
	private Logger log = LoggerFactory.getLogger(QuestionSetService.class);
	
	@Autowired
	QuestionSetRepository questionSetRepositoryRepository;
	
	@Autowired
	QuestionService questionService;
	
	/**
	 * @return All {@link QuestionSet}s in the database.
	 */
	public List<QuestionSet> getAllQuestionSets()
	{
		return questionSetRepositoryRepository.findAll();
	}
	
	/**
	 * @param id
	 * 		Id of the {@link QuestionSet} to be found.
	 * @return The QuestionSet with id.
	 */
	public QuestionSet getQuestionSetById(int id)
	{
		return questionSetRepositoryRepository.findQuestionSetById(id);
	}
	
	
	/**
	 * @param question
	 * 		Question to get the QuestionSet of.
	 * @return The QuestionSet of the question.
	 */
	public QuestionSet getQuestionSetOfQuestion(Question question)
	{
		return questionSetRepositoryRepository.findByQuestions(question);
	}
	
	/**
	 * @param manager
	 * 		Manager to get all created QuestionSets of.
	 * @return All {@link QuestionSet}s of a {@link Manager}.
	 */
	public List<QuestionSet> getQuestionSetsOfManager(Manager manager)
	{
		return questionSetRepositoryRepository.findByAuthor(manager);
	}
	
	/**
	 * @param difficulty
	 * 		Difficulty of the QuestionSets to be returned.
	 * @return All {@link QuestionSet} with a certain {@link QuestionSetDifficulty}
	 */
	public Collection<QuestionSet> getAllByDifficulty(QuestionSetDifficulty difficulty)
	{
		return questionSetRepositoryRepository.findByDifficulty(difficulty);
	}
	
	/**
	 * @param name
	 * 		String to be contained in the name of all QuestionSets returned.
	 * @return All {@link QuestionSet}s where the name contains a search phrase
	 */
	public Collection<QuestionSet> getAllContaining(String name)
	{
		return questionSetRepositoryRepository.findByNameContaining(name);
	}
	
	/**
	 * Saves the {@link QuestionSet} to the database.
	 *
	 * @param questionSet
	 * 		QuestinSet to be saved.
	 * @return the new instance. Use for all further operations.
	 * @throws IllegalArgumentException
	 * 		If any sanity checks fail.
	 */
	@PreAuthorize("hasAuthority('MANAGER')")
	public QuestionSet saveQuestionSet(QuestionSet questionSet) throws IllegalArgumentException
	{
		if (questionSet == null)
		{
			throw new IllegalArgumentException("QuestionSet cannot be null!");
		}
		if (questionSet.getAuthor() == null)
		{
			throw new IllegalArgumentException("QuestionSet must have a manager");
		}
		if (questionSet.getName() == null)
		{
			throw new IllegalArgumentException("QuestionSet name cannot be null");
		}
		if (questionSet.getName().length() > 100)
		{
			throw new IllegalArgumentException("QuestionSet name is too long(MAX: 100Chars)");
		}
		if (questionSet.getDescription() == null)
		{
			throw new IllegalArgumentException("QuestionSet description cannot be null");
		}
		if (questionSet.getDescription().length() > 300)
		{
			throw new IllegalArgumentException("QuestionSet description is too long(MAX: 300Chars)");
		}
		if (questionSet.getDifficulty() == null)
		{
			throw new IllegalArgumentException("QuestionSet must have difficulty");
		}
		
		if (questionSet.getQuestions() == null)
		{
			questionSet.setQuestions(new HashSet<>());
		}
		
		if (questionSet.isNew())
		{
			Set<Question> questions = new HashSet<>(questionSet.getQuestions());
			Set<Question> savedQuestions = new HashSet<>();
			questionSet.setQuestions(null); // reset questions to avoid db inconsistency
			QuestionSet savedQuestionSet = questionSetRepositoryRepository.save(questionSet);
			
			for (Question q : questions)
			{
				q.setQuestionSet(savedQuestionSet);
				savedQuestions.add(questionService.saveQuestion(q));
			}
			
			savedQuestionSet.setQuestions(savedQuestions);
			return questionSetRepositoryRepository.save(savedQuestionSet);
		}
		else
		{
			return questionSetRepositoryRepository.save(questionSet);
		}
	}
	
	/**
	 * Deletes a {@link QuestionSet} from the database.
	 *
	 * @param questionSet
	 * 		QuestionSet to be deleted.
	 */
	public void deleteQuestionSet(QuestionSet questionSet)
	{
		Set<Question> questions = new HashSet<>(questionSet.getQuestions());
		for (Question q : questions)
		{
			// TODO if type is file, delete all files
			if (q.getType() == QuestionType.picture)
			{
				//...
			}
		}
		questionSetRepositoryRepository.delete(questionSet);
		log.info("Deleted QuestionSet " + questionSet.getId());
	}
	
}
