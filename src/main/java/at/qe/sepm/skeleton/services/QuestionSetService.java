package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.repositories.QuestionSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for accessing and manipulating {@link Question} entities.
 *
 * @author Johannes Koch
 * @return
 */
@Component
@Scope("application")
public class QuestionSetService {

    private Logger log = LoggerFactory.getLogger(QuestionSetPerformanceService.class);

    @Autowired
    QuestionSetRepository questionSetRepositoryRepository;

    @Autowired
    QuestionService questionService;

    /**
     * Returns the QuestionSet of a Question
     * @param question
     * @return
     */
    public QuestionSet getQuestionSetOfQuestion(Question question) {
        return questionSetRepositoryRepository.findByQuestions(question);
    }

    /**
     * Returns all {@link QuestionSet}s of a {@link Manager}
     *
     * @param manager
     * @return
     */
    public List<QuestionSet> getQuestionSetsOfManager(Manager manager){
        return questionSetRepositoryRepository.findByAuthor(manager);
    }

    /**
     * Returns all {@link QuestionSet} with a certain {@link QuestionSetDifficulty}
     *
     * @param difficulty
     */
    public Collection<QuestionSet> getAllByDifficulty(QuestionSetDifficulty difficulty){
        return questionSetRepositoryRepository.findByDifficulty(difficulty);
    }

    /**
     * Finds all {@link QuestionSet}s where the name contains a search phrase
     *
     * @param name search phrase
     * @return
     */
    public Collection<QuestionSet> getAllContaining(String name){
        return questionSetRepositoryRepository.findByNameContaining(name);
    }

    /**
     * Saves the {@link QuestionSet} to the db
     *
     * @param questionSet
     * @return
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasAuthority('MANAGER')")
    public QuestionSet saveQuestionSet(QuestionSet questionSet) throws IllegalArgumentException{
        if(questionSet == null){
            throw new IllegalArgumentException("QuestionSet cannot be null!");
        }
        if(questionSet.getAuthor() == null){
            throw new IllegalArgumentException("QuestionSet must have a manager");
        }
        if(questionSet.getName() == null){
            throw new IllegalArgumentException("QuestionSet name cannot be null");
        }
        if(questionSet.getName().length() > 100){
            throw new IllegalArgumentException("QuestionSet name is too long(MAX: 100Chars)");
        }
        if(questionSet.getDescription() == null){
            throw new IllegalArgumentException("QuestionSet description cannot be null");
        }
        if(questionSet.getDescription().length() > 300){
            throw new IllegalArgumentException("QuestionSet description is too long(MAX: 300Chars)");
        }
        if(questionSet.getDifficulty() == null){
            throw new IllegalArgumentException("QuestionSet must have difficulty");
        }

        if(questionSet.getQuestions() == null){
            questionSet.setQuestions(new HashSet<>());
        }

        if(questionSet.isNew()){
            Set<Question> questions = new HashSet<>(questionSet.getQuestions());
            Set<Question> savedQuestions = new HashSet<>();
            questionSet.setQuestions(null); // reset questions to avoid db inconsistency
            QuestionSet savedQuestionSet = questionSetRepositoryRepository.save(questionSet);

            for (Question q: questions) {
                q.setQuestionSet(savedQuestionSet);
                savedQuestions.add(questionService.saveQuestion(q));
            }

            savedQuestionSet.setQuestions(savedQuestions);
            return questionSetRepositoryRepository.save(savedQuestionSet);
        } else {
            return questionSetRepositoryRepository.save(questionSet);
        }
    }

    /**
     * Deletes a {@link QuestionSet} from the db
     *
     * @param questionSet
     */
    //@PreAuthorize("principal.username eq questionSet.author.user.username")
    public void deleteQuestionSet(QuestionSet questionSet){
        Set<Question> questions = new HashSet<>(questionSet.getQuestions());
        for(Question q: questions){
            // TODO if type is file, delete all files
            if(q.getType() == QuestionType.picture){
                //...
            }
        }
        questionSetRepositoryRepository.delete(questionSet);
        log.info("Deleted QuestionSet " + questionSet.getId());
    }

}
