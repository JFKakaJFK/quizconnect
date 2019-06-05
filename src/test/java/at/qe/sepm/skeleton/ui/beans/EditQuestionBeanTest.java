package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Question;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.services.QuestionService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class EditQuestionBeanTest {

    @Autowired
    private EditQuestionBean editQuestionBean;

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private QuestionService questionService;

    @Test
    public void testEditQuestion(){
        QuestionSet testSet = questionSetService.getQuestionSetById(401);
        editQuestionBean.setQuestionSet(testSet);
        editQuestionBean.setNewQuestion();
        Assert.assertNotNull(editQuestionBean.getSelectedQuestion());
        Assert.assertFalse(editQuestionBean.edit);
    }

    @Test
    public void testDeleteAndSaveQuestion(){
        QuestionSet testSet = questionSetService.getQuestionSetById(300);
        editQuestionBean.setQuestionSet(testSet);
        Question testQuestion = testSet.getQuestions().iterator().next();
        List <Question> questionList = new ArrayList<Question>();
        editQuestionBean.setSelectedQuestion(testQuestion);
        editQuestionBean.setQuestions(questionList);
        editQuestionBean.save();
        Assert.assertTrue(editQuestionBean.edit);
        Assert.assertTrue(questionService.getAllByQuestionContaining(testQuestion.getQuestionString()).contains(testQuestion));
        editQuestionBean.deleteQuestion(testQuestion);
        Assert.assertFalse(questionService.getAllByQuestionContaining(testQuestion.getQuestionString()).contains(testQuestion));
        editQuestionBean.setEdit(false);
        editQuestionBean.save();
        Assert.assertTrue(editQuestionBean.getQuestions().contains(testQuestion));
    }

    @Test (expected = NullPointerException.class)
    public void testSetAndRedirect(){
        editQuestionBean.setAndRedirect(300);
    }

    @Test
    public void testGetQuestionSet(){
        Assert.assertNull(editQuestionBean.getQuestionSet());
    }

    @Test
    public void testIsEdit(){
        Assert.assertNotNull(editQuestionBean.isEdit());
    }

}