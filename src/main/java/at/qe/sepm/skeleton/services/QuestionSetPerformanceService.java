package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetPerformance;
import at.qe.sepm.skeleton.repositories.QuestionSetPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * Service for accessing and manipulating {@link QuestionSetPerformance} entities.
 *
 * @author Johannes Koch
 *
 */
@Component
@Scope("application")
public class QuestionSetPerformanceService {

    @Autowired
    QuestionSetPerformanceRepository questionSetPerformanceRepository;

    /**
     * Returns the {@link QuestionSetPerformance} of a {@link Player} and a {@link QuestionSet}
     *
     * @param player
     * @param questionSet
     * @return
     */
    @PreAuthorize("hasAnyAuthority('MANAGER', 'PLAYER')")
    public QuestionSetPerformance getQuestionSetPerformanceByPlayerAndQuestionSet(Player player, QuestionSet questionSet){
        return questionSetPerformanceRepository.findByPlayerAndQuestionSet(player, questionSet);
    }

    /**
     * Saves a {@link QuestionSetPerformance} in the db
     *
     * @param questionSetPerformance
     * @return
     */
    @PreAuthorize("hasAuthority('PLAYER')")
    public QuestionSetPerformance saveQuestionSetPerformance(QuestionSetPerformance questionSetPerformance) throws IllegalArgumentException
    {
        // context validation
        if(questionSetPerformance == null){
            throw new IllegalArgumentException("QuestionSetPerformance can not be null");
        }
        if(questionSetPerformance.getPlayer() ==  null || questionSetPerformance.getPlayer().isNew()){
            throw new IllegalArgumentException("QuestionSetPerformance must have an associated Player");
        }
        if(questionSetPerformance.getQuestionSet() == null || questionSetPerformance.getQuestionSet().isNew()){
            throw new IllegalArgumentException("QuestionSetPerformance must have an associated QuestionSet");
        }

        if(questionSetPerformance.isNew()){
            // TODO @Lorenz fragen wegen direkt return oder auch player & set speichern(cascade?)
            return questionSetPerformanceRepository.save(questionSetPerformance);
        } else {
            return questionSetPerformanceRepository.save(questionSetPerformance);
        }
    }

    /**
     * Deletes a {@link QuestionSetPerformance}
     *
     * @param questionSetPerformance
     */
    // TODO lorenz fragen obs das ueberhaupt braucht/ mit questionSet deletion kaskadiert
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    public void deleteQuestionSetPerfomance(QuestionSetPerformance questionSetPerformance){
        questionSetPerformanceRepository.delete(questionSetPerformance);
    }
}
