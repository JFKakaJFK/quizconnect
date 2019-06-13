package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.QuestionSetService;
import at.qe.sepm.skeleton.services.StorageService;
import at.qe.sepm.skeleton.utils.ScrollPaginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bean to manage QuestionSets
 *
 * @author Johannes Spies
 */

@Controller
@Scope("view")
public class QSOverviewBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private StorageService storageService;

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private MessageBean messageBean;


    private List<QuestionSet> questionSets;
    private List<QuestionSet> questionSetsByManager;
    private boolean onlyByManager;
    private Manager manager;

    private QuestionSet questionsetToDelete;
    private QuestionSet questionsetForDetails;
    private String searchPhrase = "";
    private ScrollPaginator<QuestionSet> paginator;

    /**
     * Init is invoked once after the bean is initialized
     * Loads all {@link QuestionSet}s into an internal list to reduce calls to the database
     * Also loads all sets created by this user to an internal list to check if a set was made by this manager (ui:repeat) without accessing the DB each time
     */

    @PostConstruct
    public void init() {
        questionsetToDelete = null;

        this.manager = sessionInfoBean.getCurrentUser().getManager();

        //store all questionSets to an ArrayList
        this.questionSets = new ArrayList<>(questionSetService.getAllQuestionSets());

        this.paginator = new ScrollPaginator<>(questionSets, 20); // about the number of players fitting into one viewport

        //store all questionSets by currently logged-in manager to allow fast access to check if a Set belongs to a manager (enable edit/delete)
        this.questionSetsByManager = new ArrayList<>(questionSetService.getQuestionSetsOfManager(manager));
    }

    public void addQuestionSetForDisplay(QuestionSet toAdd) {
        questionSets.add(toAdd);
        questionSetsByManager.add(toAdd); //add to questionSetsByManager to correctly show edit/delete button right after the import (without having to reload the page)
        logger.info("Added QuestionSet to DisplayList");
    }

    public void deleteQuestionSet() {
        logger.info("deleting QuestionSet with name: " + questionsetToDelete.getName());

        storageService.deleteAllAnswersOfQuestionSet(sessionInfoBean.getCurrentUser().getId(), questionsetToDelete.getId().toString());
        logger.info("deleted all answers of QuestionSet");

        //delete from DB
        questionSetService.deleteQuestionSet(questionSetService.getQuestionSetById(questionsetToDelete.getId()));
        logger.info("deleted from database");

        //delete from displayed ArrayList
        questionSets.remove(questionsetToDelete);
        logger.info("deleted from displayed list");

        // removes the set from the list of sets by manager too, so isByManager doesn't need to check against already deleted sets (contains = O(n))
        questionSetsByManager.remove(questionsetToDelete);

        questionsetToDelete = null;

        filterAndUpdateSets();

        //show success message
        messageBean.alertInformation("Success", "Deleted QuestionSet");
        messageBean.updateComponent("messages");
    }

    public void handleSearch(AjaxBehaviorEvent event){
        filterAndUpdateSets();
    }

    /**
     * Filters the {@link Player}s using {@link this#searchPhrase} and updates {@link this#paginator}.
     *
     * Any mutation of {@link this#allPlayers} or {@link this#allByManager} must call this method to update the {@link ScrollPaginator}.
     */
    private void filterAndUpdateSets(){
        paginator.updateList((onlyByManager && true ? questionSetsByManager : questionSets).stream().parallel()
                .filter(questionSet -> questionSet.getName().toLowerCase().contains(searchPhrase.toLowerCase()))
                .collect(Collectors.toList()));
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

    public QuestionSet getQuestionsetToDelete() {
        return questionsetToDelete;
    }

    public void setQuestionsetToDelete(QuestionSet questionsetToDelete) {
        logger.info("set " + questionsetToDelete.getName());
        this.questionsetToDelete = questionsetToDelete;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public ScrollPaginator<QuestionSet> getPaginator() {
        return paginator;
    }

    public void setPaginator(ScrollPaginator<QuestionSet> paginator) {
        this.paginator = paginator;
    }

    public boolean isOnlyByManager() {
        return onlyByManager;
    }

    public void setOnlyByManager(boolean onlyByManager) {
        this.onlyByManager = onlyByManager;
        filterAndUpdateSets();
    }

    public QuestionSet getQuestionsetForDetails() {
        return questionsetForDetails;
    }

    public void setQuestionsetForDetails(QuestionSet questionsetForDetails) {
        this.questionsetForDetails = questionsetForDetails;
    }
}


