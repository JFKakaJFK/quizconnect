package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.QuestionService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
@Component
@Scope("session")

public class QSOverviewBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageBean messageBean;

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CSVImportBean CSVImport;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    private User currentUser;
    private Manager currentManager;
    private List<QuestionSet> questionSets;

    private Part uploadedFile;

    //TODO JavaDoc for init
    @PostConstruct
    public void init() {
        this.questionSets = new ArrayList<>(questionSetService.getAllQuestionSets());
        currentUser = sessionInfoBean.getCurrentUser();
        currentManager = sessionInfoBean.getCurrentUser().getManager();
    }

    public List<QuestionSet> getQuestionSets(){
        return questionSets;
    }

    // TODO JavaDoc for deleteQuestionSet
    public void deleteQuestionSet(QuestionSet questionSet) {
        logger.info("deleting QuestionSet with name: " + questionSet.getName());
        questionSetService.deleteQuestionSet(questionSet);
        logger.info("deleted from database");
        questionSets.remove(questionSet);
        logger.info("deleted from internal set");
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("formOverview-QSets:overview-QSets");
        String message = String.format("Successfully deleted %s", questionSet.getName());
        messageBean.showInformation("overview-QSets", message);
    }

    public void saveChanges() {
        //TODO: edit functionality & JavaDoc
    }

    //TODO: JavaDoc for getCurrentUser
    public User getCurrentUser() {
        logger.info("user with name: " + currentUser.getUsername());
        return currentUser;
    }

    //TODO: JavaDoc for getCurrentManager
    public Manager getCurrentManager() {
        logger.info("manager with id: " + currentManager.getId());
        return currentManager;
    }

    /*
    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        logger.info("WTF IS SET");
        this.uploadedFile = uploadedFile;
    }



    public void processFile(){
        logger.info("WTF IS SAVE");
        logger.info(String.valueOf(uploadedFile));
        try (InputStream input = uploadedFile.getInputStream()) {
            List<List<String>> csvdata = CSVImport.addQuestionsFromCSV(input);
            logger.info(String.valueOf(csvdata));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
   */

}


