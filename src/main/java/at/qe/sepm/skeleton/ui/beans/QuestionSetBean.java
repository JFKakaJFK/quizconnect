package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

/**
 * Bean to help to create a new QuestionSet
 *
 * @author Johannes Spies
 */

@Controller
@Scope("view")

public class QuestionSetBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageBean messageBean;

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    private List<String> types = Arrays.asList("text", "picture");
    private List<String> difficulty = Arrays.asList("easy", "hard");

    private User currentUser;
    private QuestionSet questionSet;
    private Set<Question> questions;
    private Question question;

    private StorageService storageService;
    private ManagerService managerService;
    private PlayerService playerService;

    @PostConstruct
    public void init() {
        currentUser = sessionInfoBean.getCurrentUser();
        // creates internal set ("questions") of questions created in this process which is later assigned to the questionSet
        initQuestions();
        // create an empty question on startup
        initQuestion();
        // create a new QuestionSet
        initQuestionSet();
    }

    @Autowired
    public QuestionSetBean(StorageService storageService, PlayerService playerService, ManagerService managerService){
        assert storageService != null;
        assert playerService != null;
        assert managerService != null;
        this.storageService = storageService;
        this.playerService = playerService;
        this.managerService = managerService;
    }

    private String filename = null;
    private File file;

    public void handleFileUpload(String property, Question question, String questionSetId){
        if(file != null){
            logger.debug("file for someone");
            if(filename != null){
                storageService.deleteAnswer(filename);
            }
            try {
                Manager manager = currentUser.getManager();
                filename = storageService.storeAnswer(file, manager.getId().toString(), questionSetId);
                Files.deleteIfExists(file.toPath());
            } catch (IOException e){
                filename = null;
                logger.error("Exception while saving Question");
            }
        }
        logger.info("Filename:" + filename);
        saveQuestionPicture(property, question);
    }

    public void saveQuestionPicture(String property, Question question) {
        logger.info("---> saveQuestionPicture called! <---");
        if(filename == null){
            return;
        }
        switch(property) {
            case "questionString":
                question.setQuestionString(filename);
                break;
            case "rightAnswerString":
                question.setRightAnswerString(filename);
                break;
            case "wrongAnswerString_1":
                question.setWrongAnswerString_1(filename);
                break;
            case "wrongAnswerString_2":
                question.setWrongAnswerString_2(filename);
                break;
            case "wrongAnswerString_3":
                question.setWrongAnswerString_3(filename);
                break;
            case "wrongAnswerString_4":
                question.setWrongAnswerString_4(filename);
                break;
            case "wrongAnswerString_5":
                question.setWrongAnswerString_5(filename);
                break;
            default:
                logger.info("well, something in the switch-case went wrong");
        }
        filename = null;
    }

    //TODO: JavaDoc for abort
    public void abort(){
        if(filename != null){
            storageService.deleteAvatar(filename);
            filename = null;
        }
    }

    public void none() {

    }

    private void initQuestion() {
        question = new Question();

        //pre-selection for radio buttons
        question.setType(QuestionType.text);
    }

    private void initQuestions() {
        questions = new HashSet<Question>();
    }

    private void initQuestionSet() {
        questionSet = new QuestionSet();
        questionSet.setDifficulty(QuestionSetDifficulty.easy);
    }

    /* Called on modal button "add another question - YES" */
    public void saveNewQuestion() {
            logger.info("saveNewQuestion called");
            removeSpaces(question);
            questions.add(question);
            question.setQuestionSet(questionSet);
            questionService.saveQuestion(question);
            logger.info("Added question to Database - ID: " + question.getId());
            initQuestion();
    }

    /* Called on button "Step 2: Add new questions"
    * Currently a questionSet is saved even if the manager decides to navigate to another page before adding a question (-> Empty questionSet is allowed)
    * */

    public void saveNewQuestionSet() {
        logger.info("saveNewQuestionSet invoked");
        if (currentUser.getRole() == UserRole.MANAGER) {
            questionSet.setAuthor(currentUser.getManager());
            questionSet.setQuestions(questions);
            questionSetService.saveQuestionSet(questionSet);
            logger.info("Created a new QuestionSet with ID: " + questionSet.getId() + " by manager:" + questionSet.getAuthor());
        }
    }

    /* Called on modal button "add another question - NO" Saves the currently entered question and saves the whole QuestionSet */
    public void exitCreateQuestionSet() {
        saveNewQuestion();

        // redirect to overview
        try {
            FacesContext.getCurrentInstance().
                    getExternalContext().redirect("/secured/QSOverview.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes trailing and leading spaces.
     *
     * @param question
     * @return
     */
    private void removeSpaces (Question question) {
        question.setQuestionString(question.getQuestionString().trim());
        question.setRightAnswerString(question.getRightAnswerString().trim());
        question.setWrongAnswerString_1(question.getWrongAnswerString_1().trim());

        if (question.getWrongAnswerString_2()!=null) {
            question.setWrongAnswerString_2(question.getWrongAnswerString_2().trim());
        }

        if (question.getWrongAnswerString_3()!=null) {
            question.setWrongAnswerString_3(question.getWrongAnswerString_3().trim());
        }

        if (question.getWrongAnswerString_4()!=null) {
            question.setWrongAnswerString_4(question.getWrongAnswerString_4().trim());
        }

        if (question.getWrongAnswerString_5()!=null) {
            question.setWrongAnswerString_5(question.getWrongAnswerString_5().trim());
        }
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public List<String> getTypes() {
        return types;
    }

    public List<String> getDifficulty() {
        return difficulty;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
