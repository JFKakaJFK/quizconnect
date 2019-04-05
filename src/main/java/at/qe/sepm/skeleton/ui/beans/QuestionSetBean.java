package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.model.UserRole;
import at.qe.sepm.skeleton.services.QuestionService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean to help to create a new QuestionSet
 *
 * @author Johannes Spies
 */

@Component
@Scope("session")
public class QuestionSetBean {
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
        question = new Question();
        questionSet = new QuestionSet();
        questions = new HashSet<Question>();
        currentUser = sessionInfoBean.getCurrentUser();
    }

    public void clearQuestion() {
        question = new Question();

    }

    public void saveNewQuestion() {
        questions.add(question);
        question.setQuestionSet(questionSet);
        questionService.saveQuestion(question);
        logger.info("Created a new question with ID: " + question.getId());
        clearQuestion();
    }

    public void saveNewQuestionSet() {
        if (currentUser.getRole() == UserRole.MANAGER) {
            questionSet.setAuthor(currentUser.getManager());
            questionSet.setQuestions(questions);
            questionSetService.saveQuestionSet(questionSet);
            logger.info("Created a new QuestionSet with ID: " + questionSet.getId() + "by manager:" + questionSet.getAuthor());
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
