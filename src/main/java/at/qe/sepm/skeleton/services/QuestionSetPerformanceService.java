package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetPerformance;
import at.qe.sepm.skeleton.repositories.QuestionSetPerformanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service for accessing and manipulating {@link QuestionSetPerformance} entities.
 *
 * @author Johannes Koch
 *
 */
@Component
@Scope("application")
public class QuestionSetPerformanceService {

    private Logger log = LoggerFactory.getLogger(QuestionSetPerformanceService.class);

    @Autowired
    QuestionSetPerformanceRepository questionSetPerformanceRepository;

    /**
     * Returns all {@link QuestionSetPerformance}s of a {@link Player}
     *
     * @param player
     * @return
     */
    @PreAuthorize("hasAnyAuthority('MANAGER', 'PLAYER')")
    public List<QuestionSetPerformance> getQuestionSetPerformancesOfPlayer(Player player){
        return questionSetPerformanceRepository.findByPlayer(player);
    }

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
     * Updates a {@link Player}s {@link QuestionSetPerformance} for a {@link QuestionSet}. Creates a new
     * {@link QuestionSetPerformance} if necessary.
     *
     * @param questionSet
     * @param player
     * @param total
     * @param right
     * @return
     */
    // TODO: preauthorize
    public QuestionSetPerformance updatePlayerStats(QuestionSet questionSet, Player player, int total, int right){
        QuestionSetPerformance questionSetPerformance = questionSetPerformanceRepository.findByPlayerAndQuestionSet(player, questionSet);

        if(questionSetPerformance == null){
            questionSetPerformance = new QuestionSetPerformance();
            questionSetPerformance.setQuestionSet(questionSet);
            questionSetPerformance.setPlayer(player);
            // other values will be inititalized per default
            log.info("Created new QuestionSetPerformance for Player " + player.getId() + " and QuestionSet " + questionSet.getId());
        } else {
            if(questionSetPerformance.isNew()){
                throw new IllegalArgumentException("Damn you're a magician! You found something in the DB which was NOT stored there...");
            }
            if(questionSetPerformance.getPlayer() ==  null || questionSetPerformance.getPlayer().isNew()){
                throw new IllegalArgumentException("QuestionSetPerformance must have an associated Player");
            }
            if(questionSetPerformance.getQuestionSet() == null || questionSetPerformance.getQuestionSet().isNew()){
                throw new IllegalArgumentException("QuestionSetPerformance must have an associated QuestionSet");
            }
        }

        questionSetPerformance.setPlayCount((questionSetPerformance.getPlayCount() + 1));
        questionSetPerformance.setTotalQuestions((questionSetPerformance.getTotalQuestions() + total));
        questionSetPerformance.setRightAnswers((questionSetPerformance.getRightAnswers() + right));

        return questionSetPerformanceRepository.save(questionSetPerformance);

    }

    /**
     * Saves a {@link QuestionSetPerformance} in the db
     *
     * @param questionSetPerformance
     * @return
     */
    @Deprecated
    @PreAuthorize("hasAuthority('PLAYER')")
    public QuestionSetPerformance saveQuestionSetPerformance(QuestionSetPerformance questionSetPerformance) throws IllegalArgumentException
    {
        // context validation
        if(questionSetPerformance == null){
            throw new IllegalArgumentException("QuestionSetPerformance can not be null");
        }
        if(questionSetPerformance.isNew()){
            throw new IllegalArgumentException("Use updatePlayerStats method");
        }
        if(questionSetPerformance.getPlayer() ==  null || questionSetPerformance.getPlayer().isNew()){
            throw new IllegalArgumentException("QuestionSetPerformance must have an associated Player");
        }
        if(questionSetPerformance.getQuestionSet() == null || questionSetPerformance.getQuestionSet().isNew()){
            throw new IllegalArgumentException("QuestionSetPerformance must have an associated QuestionSet");
        }

        return questionSetPerformanceRepository.save(questionSetPerformance);
    }

    /**
     * Deletes a {@link QuestionSetPerformance}
     *
     * @param questionSetPerformance
     */
    @Deprecated
    @PreAuthorize("hasAnyAuthority('MANAGER')")
    public void deleteQuestionSetPerfomance(QuestionSetPerformance questionSetPerformance){
        questionSetPerformanceRepository.delete(questionSetPerformance);
    }
}
