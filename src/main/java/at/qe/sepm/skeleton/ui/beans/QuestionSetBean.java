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
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Bean to help to create a new QuestionSet
 *
 * @author Johannes Spies
 */

@Component
@Scope("view")
public class QuestionSetBean implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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



    private Set<Question> savedQuestionSets;

    @PostConstruct
    public void init() {
        questionSet = new QuestionSet();
        questionSet.setDifficulty(QuestionSetDifficulty.easy);
        questions = new HashSet<Question>();
        currentUser = sessionInfoBean.getCurrentUser();
    }

    public void clearQuestion() {
        logger.info("clear question invoked");
        question = new Question();
        question.setType(QuestionType.text);

    }

    public void saveNewQuestion() {
        logger.info("Save new question invoked");
        questions.add(question);
        question.setQuestionSet(questionSet);
        logger.info("questionSet=" + question.getQuestionSet() + "; type=" + question.getType() + ", questionString='" + question.getQuestionString() + ", rightAnswerString='" + question.getRightAnswerString());
        questionService.saveQuestion(question);
        logger.info("Created a new question with ID: " + question.getId());
    }

    public void saveNewQuestionSet() {
        if (currentUser.getRole() == UserRole.MANAGER) {
            questionSet.setAuthor(currentUser.getManager());
            questionSet.setQuestions(questions);
            questionSetService.saveQuestionSet(questionSet);
            logger.info("Created a new QuestionSet with ID: " + questionSet.getId() + " by manager:" + questionSet.getAuthor());

            // creates a new question initialized to easy
            clearQuestion();
        }
    }

    public void exitCreateQuestionSet() {
        saveNewQuestion();
        logger.info("Added a total of " + questions.size() + " questions to QuestionSet with name: " + questionSet.getName());

        try {
            FacesContext.getCurrentInstance().
                    getExternalContext().redirect("/secured/home.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
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

    public Set<Question> getSavedQuestionSets() {
        return savedQuestionSets;
    }

    public List<String> getTypes() {
        return types;
    }

    public List<String> getDifficulty() {
        return difficulty;
    }
}
