package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.QuestionSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * Bean to manage QuestionSets
 *
 * @author Johannes Spies
 */

@Controller
@Scope("view")
public class QSOverviewBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //@Autowired
    //private MessageBean messageBean;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private MessageBean messageBean;

    private List<QuestionSet> questionSets;
    private List<QuestionSet> questionSetsByManager;
    private Manager manager;


    /**
     * Init is invoked once after the bean is initialized
     * Loads all {@link QuestionSet} into an internal list to reduce calls to the database
     * Also loads all sets created by this user to an internal list to check if a set was made by this manager (ui:repeat) without accessing the DB each time
     */

    @PostConstruct
    public void init() {
        this.manager = sessionInfoBean.getCurrentUser().getManager();
        this.questionSets = new ArrayList<>(questionSetService.getAllQuestionSets());
        this.questionSetsByManager = questionSetService.getQuestionSetsOfManager(manager);
    }
    
    /**
     * Adds a QuestionSet to the display.
     *
     * @param toAdd
     * 		QuestionSet to add.
     */
    public void addQuestionSetForDisplay(QuestionSet toAdd) {
        questionSets.add(toAdd);
        questionSetsByManager.add(toAdd); //add to questionSetsByManager to correctly show edit/delete button right after the import (without having to reload the page)
        logger.info("Added QuestionSet to DisplayList");
    }
    
    /**
     * Deletes a QuestionSet form the database.
     *
     * @param questionSet
     * 		QuestionSet to be deleted.
     */
    public void deleteQuestionSet(QuestionSet questionSet) {
        logger.info("deleting QuestionSet with name: " + questionSet.getName());
        questionSetService.deleteQuestionSet(questionSetService.getQuestionSetById(questionSet.getId()));
        logger.info("deleted from database");
        questionSets.remove(questionSet);
        logger.info("deleted from internal set");

        // removes the set from the list of sets by manager too, so isByManager doesn't need to check against already deleted sets (contains = O(n))
        questionSetsByManager.remove(questionSet);

        messageBean.updateComponent("formOverview-QSets:overview-QSets");
        String message = String.format("Successfully deleted %s", questionSet.getName());
        messageBean.showGlobalInformation(message);
    }

    public void setQuestionSets(List<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }

    public List<QuestionSet> getQuestionSets(){
        return questionSets;
    }

    public boolean isByManager(QuestionSet questionSet) {
        return questionSetsByManager.contains(questionSet);
    }
}


