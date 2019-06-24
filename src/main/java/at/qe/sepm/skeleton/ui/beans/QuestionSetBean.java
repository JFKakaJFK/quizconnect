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
@Scope("session")

public class QuestionSetBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private MessageBean messageBean;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private ValidationBean validationBean;

    private List<String> types = Arrays.asList("text", "picture");
    private List<String> difficulty = Arrays.asList("easy", "hard");

    private User currentUser;
    private QuestionSet questionSet;
    private Set<Question> questions;
    private Question question;
    private Question questionForDetails;
    private Question questionToDelete;

    private StorageService storageService;
    private ManagerService managerService;
    private PlayerService playerService;

    private Boolean questionSetSaved;
    private List<Question> questionsDisplay;
    private boolean bEditQuestion = false;
    private boolean bEditSet = false;

    @PostConstruct
    public void init() {
        currentUser = sessionInfoBean.getCurrentUser();
        questionToDelete = null;
    }

    public void onPageLoad() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (bEditSet == false) {
                initQuestions();
                initQuestion();
                initQuestionSet();
            }
        }

    }

    @Autowired
    public QuestionSetBean(StorageService storageService, PlayerService playerService, ManagerService managerService) {
        assert storageService != null;
        assert playerService != null;
        assert managerService != null;
        this.storageService = storageService;
        this.playerService = playerService;
        this.managerService = managerService;
    }

    private String filename = null;
    private File file;

    public void handleFileUpload(String property, Question question, String questionSetId) {
        if (file != null) {
            logger.debug("file for someone");
            if (filename != null) {
                storageService.deleteAnswer(filename);
            }
            try {
                Manager manager = currentUser.getManager();
                logger.info("Manager has ID: " + manager.getId());
                filename = storageService.storeAnswer(file, manager.getId().toString(), questionSetId);
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                filename = null;
                logger.error("Exception while saving Question");
            }
        }
        logger.info("Filename:" + filename);
        saveQuestionPicture(property, question);
    }

    public void saveQuestionPicture(String property, Question question) {
        if (filename == null) {
            return;
        }
        switch (property) {
            case "questionString":
                question.setQuestionString(filename);
                break;
            case "rightAnswerString":
                if (question.getRightAnswerString()!= null && !question.getRightAnswerString().isEmpty()) {
                    storageService.deleteAnswer(question.getRightAnswerString());
                }
                question.setRightAnswerString(filename);
                break;
            case "wrongAnswerString_1":
                if (question.getWrongAnswerString_1()!= null && !question.getWrongAnswerString_1().isEmpty()) {
                    storageService.deleteAnswer(question.getWrongAnswerString_1());
                }
                question.setWrongAnswerString_1(filename);
                break;
            case "wrongAnswerString_2":
                if (question.getWrongAnswerString_2()!= null && !question.getWrongAnswerString_2().isEmpty()) {
                    storageService.deleteAnswer(question.getWrongAnswerString_2());
                }
                question.setWrongAnswerString_2(filename);
                break;
            case "wrongAnswerString_3":
                if (question.getWrongAnswerString_3()!= null && !question.getWrongAnswerString_3().isEmpty()) {
                    storageService.deleteAnswer(question.getWrongAnswerString_3());
                }
                question.setWrongAnswerString_3(filename);
                break;
            case "wrongAnswerString_4":
                if (question.getWrongAnswerString_4()!= null && !question.getWrongAnswerString_4().isEmpty()) {
                    storageService.deleteAnswer(question.getWrongAnswerString_4());
                }
                question.setWrongAnswerString_4(filename);
                break;
            case "wrongAnswerString_5":
                if (question.getWrongAnswerString_5()!= null && !question.getWrongAnswerString_5().isEmpty()) {
                    storageService.deleteAnswer(question.getWrongAnswerString_5());
                }
                question.setWrongAnswerString_5(filename);
                break;
            default:
                logger.info("No valid case matched");
        }
        filename = null;
    }

    /**
     * Called on "Cancel" of Question-Modal. Re-uses the listener which also deletes pictures in case a user decides to change the type to text.
     */
    public void abort() {
        logger.info("Type was set to: " + question.getType());
        typeChangeListener();
    }

    /**
     * Used as a default listener for the fileUpload
     */
    public void none() {

    }

    /**
     * Creates a new instance of {@link Question} and sets the default {@link QuestionType} to text (pre-selection for JSF selectOneRadio)
     */
    public void initQuestion() {
        question = new Question();

        //pre-selection for radio buttons
        question.setType(QuestionType.text);
    }

    /**
     * Creates a new {@link HashSet<Question>} for the {@link Question}s of a {@link QuestionSet} and a {@link ArrayList<Question>} for the JSF ui:repeat
     */
    private void initQuestions() {
        questions = new HashSet<Question>();
        questionsDisplay = new ArrayList<Question>();
    }

    /**
     * Creates a new {@link QuestionSet}
     * Per default a questionSet is not saved (used for showing different buttons)
     * Sets the default {@link QuestionSetDifficulty} to easy (pre-selection for the toggle-button)
     */
    private void initQuestionSet() {
        questionSet = new QuestionSet();
        questionSetSaved = false;
        questionSet.setDifficulty(QuestionSetDifficulty.easy);
    }

    /**
     * Removes a question from both Collections (ArrayList and HashSet) and from the database
     */
    public void deleteQuestion() {
        questions.remove(questionToDelete);
        questionsDisplay.remove(questionToDelete);
        questionService.deleteQuestion(questionToDelete);
        messageBean.alertInformation("Success", "Deleted question");
        /*
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("deleteQuestionForm"); // todo wtf??
        messageBean.updateComponent("messages");
        */
    }

    /**
     * bEditQuestion indicates, whether the question is a new one or just edited (true = edited, false = new)
     * Trims the excess whitespaces of all attributes of a {@link Question}
     * Adds the {@link Question} to both Collections (HashSet for assignment to the {@link QuestionSet} and ArrayList for display)
     * Assigns the {@link Question} to the according {@link QuestionSet}
     * Saves the {@link Question} to the database
     */
    public void saveNewQuestion() {
        if (isValidQuestion(question)) {
            removeSpacesAndSet(question);

            if (bEditQuestion == false) {
                questions.add(question);
                questionsDisplay.add(question);
                question.setQuestionSet(questionSet);
                logger.info("Added question to Database");
            }
            questionService.saveQuestion(question);
            initQuestion();
            if (!bEditQuestion) {
                messageBean.alertInformation("Success", "Saved new Question");
                messageBean.updateComponent("messages");
            } else {
                messageBean.alertInformation("Success", "Saved edited Question");
                messageBean.updateComponent("messages");
            }
            bEditQuestion = false;

        } else {
            logger.error("Invalid question!");
            messageBean.alertError("Error", "Couldn't save Question - please try again");
            messageBean.updateComponent("messages");
        }
    }

    /**
     * Saves a newly created {@link QuestionSet}
     * Sets the author to the currently logged in manager and assigns a Collection of {@link Question} to it for to-be-added items
     * Saves the {@link QuestionSet} to the database
     */
    public void saveNewQuestionSet() {
        questionSet.setAuthor(currentUser.getManager());
        questionSet.setQuestions(questions);
        questionSetService.saveQuestionSet(questionSet);
        questionSetSaved = true;
        logger.info("Created a new QuestionSet with ID: " + questionSet.getId() + " by manager:" + questionSet.getAuthor());
        initQuestion();
        messageBean.alertInformation("Success", "Saved new Questionset");
        messageBean.updateComponent("messages");

    }

    /**
     * Saves a edited {@link QuestionSet} (name, description, difficulty) to the database
     */
    public void saveEditedQuestionset() {
        questionSetSaved = true;
        questionSetService.saveQuestionSet(questionSet);
        logger.info("Edited QuestionSet with ID: " + questionSet.getId());
        initQuestion();
        messageBean.alertInformation("Success", "Saved edited Questionset");
        messageBean.updateComponent("messages");
    }

    /**
     * Shows if a {@link QuestionSet} contains valid information (to disable/enable the save button)
     *
     * @return <code>true</code> as soon as at least 3 characters are entered
     */
    public boolean validQuestionSet() {
        if (questionSet.getName() != null && validationBean.isValidText(questionSet.getName(), 100) && questionSet.getName().trim().length() > 2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Toggles a button in the UI used to select the {@link QuestionSetDifficulty} of a {@link QuestionSet}
     */
    public void toggleDifficulty() {
        if (questionSet.getDifficulty() == QuestionSetDifficulty.easy) {
            questionSet.setDifficulty(QuestionSetDifficulty.hard);
        } else {
            questionSet.setDifficulty(QuestionSetDifficulty.easy);
        }
    }

    /**
     * Removes trailing and leading spaces of all attributes of a {@link Question}
     * As QuestionString, RightAnswerString and WrongAnswerStrings are required and checked by {@link #isValidQuestion(Question)} they can get saved instantly
     * WrongAnswerStrings 2-5 are optional
     * @param question
     */
    private void removeSpacesAndSet(Question question) {
        question.setQuestionString(question.getQuestionString().trim());
        question.setRightAnswerString(question.getRightAnswerString().trim());
        question.setWrongAnswerString_1(question.getWrongAnswerString_1().trim());

        if (question.getWrongAnswerString_2() != null && validationBean.isValidText(question.getWrongAnswerString_2().trim())) {
            question.setWrongAnswerString_2(question.getWrongAnswerString_2().trim());
        }

        if (question.getWrongAnswerString_3() != null && validationBean.isValidText(question.getWrongAnswerString_3().trim())) {
            question.setWrongAnswerString_3(question.getWrongAnswerString_3().trim());
        }

        if (question.getWrongAnswerString_4() != null && validationBean.isValidText(question.getWrongAnswerString_4().trim())) {
            question.setWrongAnswerString_4(question.getWrongAnswerString_4().trim());
        }

        if (question.getWrongAnswerString_5() != null && validationBean.isValidText(question.getWrongAnswerString_5().trim())) {
            question.setWrongAnswerString_5(question.getWrongAnswerString_5().trim());
        }
    }

    /**
     * Validating the required attributes of a {@link Question}
     * @param question
     * @return <code>true</code> if neither the String is null, contains invalid characters, nor is longer than 200 chars
     */
    private boolean isValidQuestion(Question question) {

        boolean bValidPicture = true;
        boolean bValidText = true;

        if (question.getQuestionString() == null || !validationBean.isValidText(question.getQuestionString().trim(), 200)) {
            messageBean.alertError("Error", "Question is invalid. Please remove special chars except '.,;-_€@$äÄöÖüÜ!?#&='");
            messageBean.updateComponent("messages");
            bValidText = false;
            bValidPicture = false;
        }

        if (question.getType() == QuestionType.picture) {
            if (question.getRightAnswerString() == null || !validationBean.isValidPictureString(question.getRightAnswerString(), 200)) {
                messageBean.alertError("Error", "Picture for correct answer is invalid. Please try again!");
                messageBean.updateComponent("messages");
                bValidPicture = false;
            }

            if (question.getWrongAnswerString_1() == null || !validationBean.isValidPictureString(question.getWrongAnswerString_1(), 200)) {
                messageBean.alertError("Error", "Picture for wrong answer #1 is invalid. Please try again!");
                messageBean.updateComponent("messages");
                bValidPicture = false;
            }

            // also check other (optional) inputs if they are correct, of course just if they are provided by the user

            if (question.getWrongAnswerString_2() != null && !question.getWrongAnswerString_2().isEmpty() && !validationBean.isValidPictureString(question.getWrongAnswerString_2(), 200)) {
                messageBean.alertError("Error", "Picture for wrong answer #2 is invalid. Please try again!");
                messageBean.updateComponent("messages");
                bValidPicture = false;
            }

            if (question.getWrongAnswerString_3() != null && !question.getWrongAnswerString_3().isEmpty() && !validationBean.isValidPictureString(question.getWrongAnswerString_3(), 200)) {
                messageBean.alertError("Error", "Picture for wrong answer #3 is invalid. Please try again!");
                messageBean.updateComponent("messages");
                bValidPicture = false;
            }

            if (question.getWrongAnswerString_4() != null && !question.getWrongAnswerString_4().isEmpty() && !validationBean.isValidPictureString(question.getWrongAnswerString_4(), 200)) {
                messageBean.alertError("Error", "Picture for wrong answer #4 is invalid. Please try again!");
                messageBean.updateComponent("messages");
                bValidPicture = false;
            }

            if (question.getWrongAnswerString_5() != null && !question.getWrongAnswerString_5().isEmpty() && !validationBean.isValidPictureString(question.getWrongAnswerString_5(), 200)) {
                messageBean.alertError("Error", "Picture for wrong answer #5 is invalid. Please try again!");
                messageBean.updateComponent("messages");
                bValidPicture = false;
            }

            return bValidPicture;
        }

        if (question.getRightAnswerString() == null || !validationBean.isValidText(question.getRightAnswerString().trim(), 200)) {
            messageBean.alertError("Error", "Correct answer is invalid. Please remove special chars except '.,;-_€@$äÄöÖüÜ!?#&='");
            messageBean.updateComponent("messages");
            bValidText = false;
        }

        if (question.getWrongAnswerString_1() == null || !validationBean.isValidText(question.getWrongAnswerString_1().trim(), 200)) {
            messageBean.alertError("Error", "Wrong answer #1 is invalid. Please remove special chars except '.,;-_€@$äÄöÖüÜ!?#&='");
            messageBean.updateComponent("messages");
            bValidText = false;
        }

        // also check other (optional) inputs if they are correct, of course just if they are provided by the user
        if (question.getWrongAnswerString_2() != null && !question.getWrongAnswerString_2().isEmpty() && !validationBean.isValidText(question.getWrongAnswerString_2().trim(), 200)) {
            messageBean.alertError("Error", "Wrong answer #2 is invalid. Please remove special chars except '.,;-_€@$äÄöÖüÜ!?#&='");
            messageBean.updateComponent("messages");
            bValidText = false;
        }

        if (question.getWrongAnswerString_3() != null && !question.getWrongAnswerString_3().isEmpty() && !validationBean.isValidText(question.getWrongAnswerString_3().trim(), 200)) {
            messageBean.alertError("Error", "Wrong answer #3 is invalid. Please remove special chars except '.,;-_€@$äÄöÖüÜ!?#&='");
            messageBean.updateComponent("messages");
            bValidText = false;
        }

        if (question.getWrongAnswerString_4() != null && !question.getWrongAnswerString_4().isEmpty() && !validationBean.isValidText(question.getWrongAnswerString_4().trim(), 200)) {
            messageBean.alertError("Error", "Wrong answer #4 is invalid. Please remove special chars except '.,;-_€@$äÄöÖüÜ!?#&='");
            messageBean.updateComponent("messages");
            bValidText = false;
        }

        if (question.getWrongAnswerString_5() != null && !question.getWrongAnswerString_5().isEmpty() && !validationBean.isValidText(question.getWrongAnswerString_5().trim(), 200)) {
            messageBean.alertError("Error", "Wrong answer #5 is invalid. Please remove special chars except '.,;-_€@$äÄöÖüÜ!?#&='");
            messageBean.updateComponent("messages");
            bValidText = false;
        }

       return bValidText;
    }


    public void setEditQuestionset(int id) {
        bEditSet = true;
        questionSetSaved = false;
        this.questionSet = questionSetService.getQuestionSetById(id);
        this.questions = questionSet.getQuestions();
        this.questionsDisplay = new ArrayList<>(questions);
    }

    public void typeChangeListener() {
        if (question.getType() == QuestionType.picture) {
            if (question.getRightAnswerString()!=null && !question.getRightAnswerString().isEmpty()) {
                storageService.deleteAnswer(question.getRightAnswerString());
                question.setRightAnswerString("");
            }

            if (question.getWrongAnswerString_1()!=null && !question.getWrongAnswerString_1().isEmpty()) {
                storageService.deleteAnswer(question.getWrongAnswerString_1());
                question.setWrongAnswerString_1("");
            }

            if (question.getWrongAnswerString_2()!=null && !question.getWrongAnswerString_2().isEmpty()) {
                storageService.deleteAnswer(question.getWrongAnswerString_2());
                question.setWrongAnswerString_2("");
            }

            if (question.getWrongAnswerString_3()!=null && !question.getWrongAnswerString_3().isEmpty()) {
                storageService.deleteAnswer(question.getWrongAnswerString_3());
                question.setWrongAnswerString_3("");
            }

            if (question.getWrongAnswerString_4()!=null && !question.getWrongAnswerString_4().isEmpty()) {
                storageService.deleteAnswer(question.getWrongAnswerString_4());
                question.setWrongAnswerString_4("");
            }

            if (question.getWrongAnswerString_5()!=null && !question.getWrongAnswerString_5().isEmpty()) {
                storageService.deleteAnswer(question.getWrongAnswerString_5());
                question.setWrongAnswerString_5("");
            }

        }
    }

    public void setEditQuestion(Question selectedQuestion) {
        bEditQuestion = true;
        question = selectedQuestion;
    }

    public Boolean getQuestionSetSaved() {
        return questionSetSaved;
    }

    public Question getQuestion() {
        return question;
    }

    public Question getQuestionToDelete() {
        return questionToDelete;
    }

    public Question getQuestionForDetails() {
        return questionForDetails;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setQuestionToDelete(Question questionToDelete) {
        this.questionToDelete = questionToDelete;
    }

    public void setQuestionForDetails(Question questionForDetails) {
        this.questionForDetails = questionForDetails;
    }

    public QuestionSet getQuestionSet() {
        return questionSet;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public List<Question> getQuestionsDisplay() {
        return questionsDisplay;
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

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public void setQuestionsDisplay(List<Question> questionsDisplay) {
        this.questionsDisplay = questionsDisplay;
    }

    public void setbEditSet(boolean bEditSet) {
        this.bEditSet = bEditSet;
    }

    public boolean isbEditSet() {
        return bEditSet;
    }

    public boolean isbEditQuestion() {
        return bEditQuestion;
    }

    public void setbEditQuestion(boolean bEditQuestion) {
        this.bEditQuestion = bEditQuestion;
    }
}
