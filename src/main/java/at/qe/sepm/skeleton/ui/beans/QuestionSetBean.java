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
@Scope("session")

public class QuestionSetBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    @Autowired
    private QSOverviewBean overviewBean;

    private List<String> types = Arrays.asList("text", "picture");
    private List<String> difficulty = Arrays.asList("easy", "hard");

    private User currentUser;
    private QuestionSet questionSet;
    private Set<Question> questions;
    private Question question;

    @PostConstruct
    public void init() {
        // questions is the internal list of questions created
        questions = new HashSet<Question>();
        // current user for setting the author of the QuestionSet
        currentUser = sessionInfoBean.getCurrentUser();
        // because of the session-scope, this needs to be cleared just in case something remained in the inputs. Shouldn't be necessary!
        clearQuestions();
        clearQuestion();
        clearQuestionSet();


    }

    //TODO: JavaDoc for clearQuestion
    public void clearQuestion() {
        question = new Question();
        question.setType(QuestionType.text);
    }

    public void clearQuestions() {
        questions = new HashSet<Question>();
    }

    public void clearQuestionSet() {
        questionSet = new QuestionSet();
        questionSet.setDifficulty(QuestionSetDifficulty.easy);
    }

    /* Called on modal button "add another question - YES" */
    public void saveNewQuestion() {
        questions.add(question);
        question.setQuestionSet(questionSet);
        questionService.saveQuestion(question);
        logger.info("Added question to Database - ID: " + question.getId());
        clearQuestion();
    }

    /* Called by exitCreateQuestionSet (which is triggered by modal button - NO) */
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
        saveNewQuestionSet();

        // redirect to overview
        try {
            FacesContext.getCurrentInstance().
                    getExternalContext().redirect("/secured/QSOverview.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* add QuestionSet to QSOverviewBean internal list, which gets returned by getter (used in ui:repeat to show all questionSets) */
        overviewBean.addQuestionSetForDisplay(questionSet);

        /* clear all*/
        clearQuestion();
        clearQuestionSet();
        clearQuestions();
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
