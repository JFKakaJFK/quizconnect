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
    private QuestionSetService questionSetService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CSVImportBean CSVImport;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    private User currentUser;
    private List<QuestionSet> questionSets;

    private Part uploadedFile;

    @PostConstruct
    public void init() {
        currentUser = sessionInfoBean.getCurrentUser();
    }

    public List<QuestionSet> getQuestionSets(){
        if(questionSets == null) {
            this.questionSets = new ArrayList<>(questionSetService.getAllContaining("Test"));
            //this.questionSets = questionSetService.getQuestionSetsOfManager(currentUser.getManager());
        }
        return questionSets;
    }

    public void deleteQuestionSet() {
        //TODO: delete functionality
    }

    public void saveChanges() {
        //TODO: edit functionality
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


