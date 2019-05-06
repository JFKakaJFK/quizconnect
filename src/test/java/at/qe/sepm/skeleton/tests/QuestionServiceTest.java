package at.qe.sepm.skeleton.tests;

        import at.qe.sepm.skeleton.model.*;
        import at.qe.sepm.skeleton.repositories.QuestionRepository;
        import at.qe.sepm.skeleton.services.ManagerService;
        import at.qe.sepm.skeleton.services.QuestionService;
        import at.qe.sepm.skeleton.services.QuestionSetService;
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
 * Tests for the {@link QuestionSetService}
 *
 * @author Simon Triendl
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class QuestionServiceTest {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionSetService questionSetService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    private ManagerService managerService;

    private Manager manager;
    private QuestionSet questionSet;

    @Before
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void setUp(){
        manager = managerService.getManagerById(101);

        QuestionSet questionSet = new QuestionSet();

        questionSet.setAuthor(manager);
        questionSet.setDescription("DasIstEineBeschreibung");
        questionSet.setName("DasTestSet");
        questionSet.setDifficulty(QuestionSetDifficulty.easy);

        this.questionSet = questionSetService.saveQuestionSet(questionSet);
    }

    private QuestionSet validQuestionSet() {
        QuestionSet questionSet = new QuestionSet();

        questionSet.setAuthor(manager);
        questionSet.setDescription("DasIstEineBeschreibung");
        questionSet.setName("DasTestSet");
        questionSet.setDifficulty(QuestionSetDifficulty.easy);

        Question question = new Question();
        question.setRightAnswerString("Correct");
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

    private Question validQuestion() {
        Question question = new Question();
        question.setQuestionSet(questionSet);
        question.setRightAnswerString("Correct");
        question.setType(QuestionType.text);
        question.setQuestionString("Is this a test?");
        question.setWrongAnswerString_1("Yes!");
        question.setWrongAnswerString_2("NO!");
        question.setWrongAnswerString_3("Maybe!");
        question.setWrongAnswerString_4("I dont know!");
        question.setWrongAnswerString_5("Did not see that coming?!");

        return question;
    }

    private String getTooLongString(){
        ArrayList name = new ArrayList();
        for (int i = 0; i < 101; i++){
            name.add("Too");
        }
        return name.toString();
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionQuestionNull() {
        Question testQuestion = null;

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionQuestionSetNull() {
        Question testQuestion = validQuestion();
        QuestionSet testSet = null;
        testQuestion.setQuestionSet(testSet);

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionQuestionSetNew() {
        Question testQuestion = validQuestion();

        QuestionSet testSet = validQuestionSet();
        testQuestion.setQuestionSet(testSet);

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionQuestionTypeNull() {
        Question testQuestion = validQuestion();
        testQuestion.setType(null);

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionQuestionStringNull() {
        Question testQuestion = validQuestion();
        testQuestion.setQuestionString(null);

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionQuestionStringTooLong() {
        Question testQuestion = validQuestion();
        testQuestion.setQuestionString(getTooLongString());

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionAnswerStringNull() {
        Question testQuestion = validQuestion();
        testQuestion.setRightAnswerString(null);

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionAnswerStringTooLong() {
        Question testQuestion = validQuestion();
        testQuestion.setRightAnswerString(getTooLongString());

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_1Null() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_1(null);

        questionService.saveQuestion(testQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_1TooLong() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_1(getTooLongString());

        questionService.saveQuestion(testQuestion);
    }

    /*
    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_2Null() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_2(null);

        questionService.saveQuestion(testQuestion);
    }

     */

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_2TooLong() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_2(getTooLongString());

        questionService.saveQuestion(testQuestion);
    }

    /*
    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_3Null() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_3(null);

        questionService.saveQuestion(testQuestion);
    }

     */

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_3TooLong() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_3(getTooLongString());

        questionService.saveQuestion(testQuestion);
    }

    /*
    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_4Null() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_4(null);

        questionService.saveQuestion(testQuestion);
    }

     */

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_4TooLong() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_4(getTooLongString());

        questionService.saveQuestion(testQuestion);
    }

    /*
    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_5Null() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_5(null);

        questionService.saveQuestion(testQuestion);
    }
     */

    @Test (expected = IllegalArgumentException.class)
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testSaveQuestionWrongAnswerString_5TooLong() {
        Question testQuestion = validQuestion();
        testQuestion.setWrongAnswerString_5(getTooLongString());

        questionService.saveQuestion(testQuestion);
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetAllByAnswerContaining() {
        String correctString = "CorrectString";
        String incorrectString = "NotRight";

        Question testQuestion1 = validQuestion();
        Question testQuestion2 = validQuestion();
        Question testQuestion3 = validQuestion();

        testQuestion2.setRightAnswerString(correctString);
        testQuestion3.setRightAnswerString(correctString);
        testQuestion1.setRightAnswerString(incorrectString);

        Collection<Question> testCollection = questionService.getAllByAnswerContaining(correctString);


        Assert.assertEquals("Returning right Questions", testCollection.contains(testQuestion2), testCollection.contains(testQuestion3));
        Assert.assertFalse("Wrong Question is not returned", testCollection.contains(testQuestion1));
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetAllByQuestionContaining() {
        String correctString = "CorrectString";
        String incorrectString = "NotRight";

        Question testQuestion1 = validQuestion();
        Question testQuestion2 = validQuestion();
        Question testQuestion3 = validQuestion();

        testQuestion2.setQuestionString(correctString);
        testQuestion3.setQuestionString(correctString);
        testQuestion1.setQuestionString(incorrectString);

        Collection<Question> testCollection = questionService.getAllByQuestionContaining(correctString);

        Assert.assertEquals("Returning right Questions", testCollection.contains(testQuestion2), testCollection.contains(testQuestion3));
        Assert.assertFalse("Wrong Question is not returned", testCollection.contains(testQuestion1));
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testGetAllByType() {
        Question testQuestion1 = validQuestion();
        Question testQuestion2 = validQuestion();
        Question testQuestion3 = validQuestion();

        testQuestion2.setType(QuestionType.text);
        testQuestion3.setType(QuestionType.picture);
        testQuestion1.setType(QuestionType.picture);

        Collection<Question> testCollection = questionService.getAllByType(QuestionType.picture);

        Assert.assertEquals("Returning right Questions", testCollection.contains(testQuestion2), testCollection.contains(testQuestion3));
        Assert.assertFalse("Wrong Question is not returned", testCollection.contains(testQuestion1));
    }

    @Test
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testDeleteQuestion() {
        Question testQuestion = validQuestion();
        Question saved = questionService.saveQuestion(testQuestion);
        questionService.deleteQuestion(testQuestion);

        Assert.assertNull("Question was deleted", questionRepository.findOne(saved.getId()));
    }
}

