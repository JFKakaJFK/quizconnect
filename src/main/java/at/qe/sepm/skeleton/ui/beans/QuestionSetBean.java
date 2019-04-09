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
import java.util.HashSet;
import java.util.Set;

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

    private User currentUser;
    private QuestionSet questionSet;
    private Set<Question> questions;
    private Question question;

    @PostConstruct
    public void init() {
        questionSet = new QuestionSet();
        questionSet.setDifficulty(QuestionSetDifficulty.easy);
        questions = new HashSet<Question>();
        currentUser = sessionInfoBean.getCurrentUser();
    }

    public void clearQuestion() {
        question = new Question();
        question.setType(QuestionType.text);

    }

    public void saveNewQuestion() {
        questions.add(question);
        question.setQuestionSet(questionSet);
        logger.info("questionSet=" + question.getQuestionSet() + "; type=" + question.getType() + ", questionString='" + question.getQuestionString() + ", rightAnswerString='" + question.getRightAnswerString());
        questionService.saveQuestion(question);
        logger.info("Created a new question with ID: " + question.getId());
        clearQuestion();
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
                    getExternalContext().redirect("/");
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
}
