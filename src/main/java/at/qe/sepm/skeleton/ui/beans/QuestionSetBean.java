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
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
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

    private void initQuestion() {
        question = new Question();
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
}
