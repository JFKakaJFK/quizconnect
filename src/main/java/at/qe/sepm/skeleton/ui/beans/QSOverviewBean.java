package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.QuestionService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import java.io.InputStream;

/**
 * Bean to manage QuestionSets
 *
 * @author Johannes Spies
 */

@Controller

public class QSOverviewBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //@Autowired
    //private MessageBean messageBean;

    @Autowired
    private QuestionSetService questionSetService;

    private List<QuestionSet> questionSets;


    @PostConstruct
    public void init() {
        this.questionSets = new ArrayList<>(questionSetService.getAllQuestionSets());
    }

    public List<QuestionSet> getQuestionSets(){
        //if (questionSets.size() != questionSetService.getAllQuestionSets().size()) {
        //    this.questionSets = questionSetService.getAllQuestionSets();
        //}
        logger.info("getQuestionSet called");
        return questionSets;
    }

    public void addQuestionSetForDisplay(QuestionSet toAdd) {
        questionSets.add(toAdd);
        logger.info("Added QuestionSet to DisplayList");
    }

    public void setQuestionSets(List<QuestionSet> questionSets) {
        this.questionSets = questionSets;
    }

    public void deleteQuestionSet(QuestionSet questionSet) {
        logger.info("deleting QuestionSet with name: " + questionSet.getName());
        questionSetService.deleteQuestionSet(questionSet);
        logger.info("deleted from database");
        questionSets.remove(questionSet);
        logger.info("deleted from internal set");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("formOverview-QSets:overview-QSets");
        String message = String.format("Successfully deleted %s", questionSet.getName());
        //messageBean.showInformation("overview-QSets", message);
    }

    public void saveChanges() {
        //TODO: edit functionality
    }
}


