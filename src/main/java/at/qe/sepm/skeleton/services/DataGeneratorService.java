package at.qe.sepm.skeleton.services;


import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.utils.AuthenticationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Service for generating test data
 *
 * @author Simon Triendl
 */

@Component
@Scope("application")
public class DataGeneratorService {

    public static final boolean enabled = false;

    private final int countMultiplier = 1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    @Autowired
    ManagerService managerService;

    @Autowired
    PlayerService playerService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionSetService questionSetService;

    @Autowired
    QuestionSetPerformanceService questionSetPerformanceService;

    /**
     * Method to control all following generator-functions
     * Do use getRandomString setters have to be adjusted according to the MaxLength in the JavaDoc
     */
    public void generateData(){
        if(!enabled){
            return;
        }

        Random random = new Random();

        AuthenticationUtil.configureAuthentication("MANAGER");

        generatePlayer(random.nextInt(20 * countMultiplier) + (30 * countMultiplier));

        generateManager(random.nextInt(5 * countMultiplier) + (10 * countMultiplier));

        generateQuestion(random.nextInt( 50 * countMultiplier) + (60 * countMultiplier));

        generateQuestionSet(random.nextInt(20 * countMultiplier) + (20 * countMultiplier));

        generateQuestionSetAndQuestions(random.nextInt(20 * countMultiplier) + (10 *countMultiplier));

        generateQuestionSetPerformance(random.nextInt(10 *countMultiplier) + (10 * countMultiplier));

        AuthenticationUtil.clearAuthentication();
    }

    /**
     * Method that creates a random String
     * @param maxLength
     * @return
     */

    private String getRandomString(int maxLength){
        Random rand = new Random();
        // the maxLength has to be reduced by 4 since because of an edge case in getQuestion
        maxLength -= 4;
        final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890?!:_;";

        final Set<String> identifiers = new HashSet<String>();

        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(maxLength) + 1;
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }

    /**
     * Method that creates, saves to DB and returns a Question
     * MaxLength: Name = 100
     *            Description = 300
     * @param current
     * @return
     */
    private QuestionSet getQuestionSet(int current){
        Manager creator = managerService.getManagerById(101);

        QuestionSet questionSet = new QuestionSet();
        questionSet.setName("GeneratedSet" + current);
        questionSet.setDescription("GeneratedDescription" + current);
        questionSet.setDifficulty((current % 2 == 0) ? QuestionSetDifficulty.easy : QuestionSetDifficulty.hard);
        questionSet.setAuthor(creator);

        return questionSet;
    }

    /**
     * Method that creates, saves to DB and return a QuestionSet
     * MaxLength: any String in Question = 200
     * @param set
     * @param current
     * @return
     */
    private Question getQuestion(int set ,int current){

        Question question = new Question();
        question.setType(QuestionType.text);
        question.setQuestionString("GeneratedTestString " + set + " " + current);
        question.setRightAnswerString("GeneratedRightAnswer " + set + " " + current);
        question.setWrongAnswerString_1("GeneratedWrongAnswer1 " + set + " " + current);
        question.setWrongAnswerString_2("GeneratedWrongAnswer2 " + set + " " + current);
        question.setWrongAnswerString_3("GeneratedWrongAnswer3 " + set + " " + current);
        question.setWrongAnswerString_4("GeneratedWrongAnswer4 " + set + " " + current);
        question.setWrongAnswerString_5("GeneratedWrongAnswer5 " + set + " " + current);

        return question;
    }
    /**
     * Method to generate a User
     * Formally used  at generatePlayer and generateManager
     * Not needed since saveNewPlayer and saveNewManager create a User
     * MaxLength: Username = 100
     * @param numUser
     * @return
     */
    private User generateUser(int numUser){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);

        User user = new User();
        user.setUsername("GeneratedUser" + numUser);
        user.setPassword(passwordEncoder.encode("passwd" + numUser));
        user.setEnabled(true);

        userService.saveUser(user);

        return user;
    }

    /**
     * Method to generate Players (for test purposes only)
     * MaxLength: Username = 100
     * @param count
     */
    private void generatePlayer(int count){
        for (int i = 1; i <= count; i++){

            Manager creator = managerService.getManagerById(101);

            Player player = new Player();
            player.setCreator(creator);

            playerService.saveNewPlayer(player, "GeneratedName" + i, "passwd" + i);
        }

        logger.info(count + " Player have been created");
    }

    /**
     * Method to generate Manager (for test purposes only)
     * MaxLength: Email = 100
     *            Institution = 200
     * @param count
     */
    private void generateManager(int count){
        for (int i = 1; i <= count; i++){

            Manager manager = new Manager();
            manager.setEmail("generatedMail@test.com" + i);
            manager.setInstitution("generatedInst" + i);

            managerService.saveNewManager(manager, "passwd" + i);
        }

        logger.info(count + " Manager have been created");
    }

    /**
     * Method to generate Questions (for test purposes only)
     * @param count
     */
    private void generateQuestion(int count){

        QuestionSet questionSet = getQuestionSet(1);
        questionSetService.saveQuestionSet(questionSet);

        for (int i = 1; i <= count; i++){

            Question question = getQuestion(1, i);
            question.setQuestionSet(questionSet);
            questionService.saveQuestion(question);
        }

        logger.info(count + " Questions have been created");
    }

    /**
     * Method to generate QuestionSets (for test purposes only)
     * @param count
     */
    private void generateQuestionSet(int count){

        for (int i = 1; i <= count; i++){

            QuestionSet questionSet = getQuestionSet(i);
            questionSetService.saveQuestionSet(questionSet);
        }

        logger.info(count + " QuestionSets have been created");
    }

    /**
     * Method to generate QuestionSets with each 30 associated Questions (for test purposes only)
     * @param count
     */
    private void generateQuestionSetAndQuestions(int count){
        for (int i = 1; i <= count; i++){

            QuestionSet questionSet = getQuestionSet(i);
            questionSetService.saveQuestionSet(questionSet);

            for (int y = 1; y <= 30; y++){

                Question question = getQuestion(i, y);
                question.setQuestionSet(questionSet);
                questionService.saveQuestion(question);
            }
        }
        logger.info(count + "QuestionSet and " + count*30 + " Questions have been created");
    }

    /**
     * Method ro generate QuestionSetPerformances and associated Players (for test purposes only)
     * MaxLength: Username = 100
     * @param count
     */
    private void generateQuestionSetPerformance(int count){
        for (int i = 1; i <= count; i++){
            Manager creator = managerService.getManagerById(101);
            Random random = new Random();

            QuestionSet questionSet = getQuestionSet(i);
            questionSetService.saveQuestionSet(questionSet);

            Player player = new Player();
            player.setCreator(creator);
            playerService.saveNewPlayer(player, "GeneratedNameForQSP" + i, "passwd" + i);


            questionSetPerformanceService.updatePlayerStats(questionSet, player, random.nextInt(50) + 300, random.nextInt(100) + 200);
        }
        logger.info(count + " QuestionSetPerformances and " + count + " Players have been created");
    }
}
