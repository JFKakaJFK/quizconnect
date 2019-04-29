package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.services.QuestionService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Scope("session")
public class EditQuestionBean {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QuestionService questionService;

    private QuestionSet questionSet;
    private List<Question> questions;
    private Question selectedQuestion;

    public void setAndRedirect(QuestionSet set) {
        this.questionSet = set;
        this.questions = new ArrayList<>(questionSet.getQuestions());

        try {
            //String url = String.format("/secured/editQuestionSet.xhtml?id=%d", set.getId());
            FacesContext.getCurrentInstance().
                    getExternalContext().redirect("/secured/editQuestionSet.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //FIXME: Don't just reload the original set from DB
    public void abort() {
        logger.info("called abort");
        questions = new ArrayList<>(questionSet.getQuestions());
    }

    public void save() {
        questionService.saveQuestion(selectedQuestion);
        logger.info("Saved question");
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestionSet(QuestionSet questionSet) {
        this.questionSet = questionSet;
    }

    public Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public void setSelectedQuestion(Question selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
    }
}
