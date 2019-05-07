package at.qe.sepm.skeleton.tests;

import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.repositories.QuestionSetRepository;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.QuestionService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import at.qe.sepm.skeleton.services.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests for the {@link QuestionService}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class QuestionSetServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionSetService questionSetService;

    @Autowired
    QuestionSetRepository questionSetRepository;

    @Autowired
    private ManagerService managerService;

    private Manager manager;

    @Before
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void setUp(){
        manager = managerService.getManagerById(101);
    }

    private QuestionSet validQuestionSet(){
        QuestionSet questionSet = new QuestionSet();

        questionSet.setAuthor(manager);
        questionSet.setDescription("DasIstEineBeschreibung");
        questionSet.setName("DasTestSet");
        //questionSet.setId(120);
        questionSet.setDifficulty(QuestionSetDifficulty.easy);

        Question question = new Question();
        question.setRightAnswerString("Richtig");
        question.setType(QuestionType.text);
        question.setQuestionString("Is this a test?");
        question.setWrongAnswerString_1("Yes!");
        question.setWrongAnswerString_2("NO!");
        question.setWrongAnswerString_3("Maybe!");
        question.setWrongAnswerString_4("I dont know!");
        question.setWrongAnswerString_5("Did not see that coming?!");
        question.setQuestionSet(questionSet);

        Set<Question> questions = new HashSet<>();
        questions.add(question);
        questionSet.setQuestions(questions);

        return questionSet;
    }

    private String getTooLongString(){
        ArrayList name = new ArrayList();
        for (int i = 0; i < 101; i++){
            name.add("Too");
        }
        return name.toString();
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetQuestionSetsOfManager()
    {
        User m = userService.loadUser("user1");

        List<QuestionSet> questionSets = questionSetService.getQuestionSetsOfManager(m.getManager());
        Assert.assertNotNull("QuestionSets not loaded!", questionSets);
        Assert.assertTrue("Wrong number of Players loaded",questionSets.size() > 0);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetQuestionById()
    {
        QuestionSet questionSet = questionSetService.getQuestionSetById(300);
        Assert.assertNotNull("QuestionSets not loaded!", questionSet);
        Assert.assertEquals("Wrong set loaded",300, (int) questionSet.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetNullSet() {
        QuestionSet testSet = null;
        questionSetService.saveQuestionSet(testSet);
    }

    @Test(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetNullAuthor() {
        QuestionSet testSet = validQuestionSet();
        testSet.setAuthor(null);
        questionSetService.saveQuestionSet(testSet);
    }

    @Test(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetNullName() {
        QuestionSet testSet = validQuestionSet();
        testSet.setName(null);
        questionSetService.saveQuestionSet(testSet);
    }

    @Test(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetTooLongName() {
        QuestionSet testSet = validQuestionSet();
        testSet.setName(getTooLongString());
        questionSetService.saveQuestionSet(testSet);
    }

    @Test(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetDescriptionNull() {
        QuestionSet testSet = validQuestionSet();
        testSet.setDescription(null);
        questionSetService.saveQuestionSet(testSet);
    }

    @Test(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetDescriptionTooLong() {
        QuestionSet testSet = validQuestionSet();
        testSet.setName(getTooLongString());
        questionSetService.saveQuestionSet(testSet);
    }

    @Test(expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetDifficultyNull() {
        QuestionSet testSet = validQuestionSet();
        testSet.setDifficulty(null);
        questionSetService.saveQuestionSet(testSet);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetSaveSet() {
        QuestionSet testSet = validQuestionSet();
        QuestionSet saved = questionSetService.saveQuestionSet(testSet);

        Assert.assertNotNull("QuestionSet is saved to DB", saved.getId());
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionSetOverwriteSet() {
        String changingString = "ThatChanged";
        QuestionSet testSet = validQuestionSet();
        questionSetService.saveQuestionSet(testSet);
        testSet.setDescription(changingString);
        QuestionSet saved = questionSetService.saveQuestionSet(testSet);

        Assert.assertEquals("QuestionSet is overwritten in DB", changingString,saved.getDescription());
    }


    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testDeleteQuestionSet() {
        QuestionSet testSet = validQuestionSet();
        QuestionSet saved = questionSetService.saveQuestionSet(testSet);
        questionSetService.deleteQuestionSet(testSet);

        Assert.assertNull("QuestionSet is deleted from DB", questionSetRepository.findOne(saved.getId()));
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetAllContaining() {
        String testString = "RightName";
        QuestionSet testSet1 = validQuestionSet();
        QuestionSet testSet2 = validQuestionSet();
        QuestionSet testSet3 = validQuestionSet();
        testSet2.setName(testString);
        testSet3.setName(testString);

        questionSetService.saveQuestionSet(testSet1);
        questionSetService.saveQuestionSet(testSet2);
        questionSetService.saveQuestionSet(testSet3);

        Collection<QuestionSet> testCollection = questionSetService.getAllContaining(testString);

        Assert.assertEquals("Returning right QuestionSets", testCollection.contains(testSet3),testCollection.contains(testSet2));
        Assert.assertFalse("Wrong QuestionSet is not returned", testCollection.contains(testSet1));
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetAllByDifficulty() {
        QuestionSet testSet1 = validQuestionSet();
        QuestionSet testSet2 = validQuestionSet();
        QuestionSet testSet3 = validQuestionSet();
        testSet1.setDifficulty(QuestionSetDifficulty.hard);
        testSet2.setDifficulty(QuestionSetDifficulty.easy);
        testSet3.setDifficulty(QuestionSetDifficulty.easy);

        questionSetService.saveQuestionSet(testSet1);
        questionSetService.saveQuestionSet(testSet2);
        questionSetService.saveQuestionSet(testSet3);

        Collection<QuestionSet> testCollection = questionSetService.getAllByDifficulty(QuestionSetDifficulty.easy);

        Assert.assertEquals("Returning right QuestionSets", testCollection.contains(testSet2), testCollection.contains(testSet3));
        Assert.assertFalse("Wrong QuestionSet is not returned", testCollection.contains(testSet1));

    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetQuestionSetOfQuestion(){
        // Initializing still missing
        Question testQuestion = questionService.getAllByType(QuestionType.text).iterator().next();
        QuestionSet checkSet = questionSetService.getQuestionSetOfQuestion(testQuestion);

        Assert.assertNotNull("QuestionSet is not null", questionSetService.getQuestionSetOfQuestion(testQuestion));
        Assert.assertTrue("Right QuestionSet was loaded from DB", checkSet.equals(questionSetService.getQuestionSetOfQuestion(testQuestion)));
    }

}
