package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.*;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@Scope("view")



/**
 * FIXME: This version currently works with valid datasets (the ones created by the PS groups) but will be replaced by a more sophisticated version (CSV column mapping) in the future
 * TLDR: It works for valid data, but can't say what happens with invalid data. Will be replaced.
 */

public class CSVImportBean implements Serializable {


    private static final Logger logger = LoggerFactory.getLogger(ChangeAvatarBean.class);

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QSOverviewBean QSOverviewBean;

    @Autowired
    private MessageBean messageBean;

    private List<String> questionVariables = new ArrayList<>(Arrays.asList("getWrongAnswerString_1","getWrongAnswerString_2","getWrongAnswerString_3","getWrongAnswerString_4","getWrongAnswerString_5"));

    private Path temp;

    private String nameCSV;
    private String descriptionCSV;

    private QuestionSet questionSet;
    private Question question;

    private StorageService storageService;
    private PlayerService playerService;
    private ManagerService managerService;

    @Autowired
    public CSVImportBean(StorageService storageService,
                         PlayerService playerService,
                         ManagerService managerService,
                         @Value("${storage.uploads.temporary}") String temp) {
        assert storageService != null;
        assert playerService != null;
        assert managerService != null;
        this.storageService = storageService;
        this.playerService = playerService;
        this.managerService = managerService;
        this.temp = Paths.get(temp);
    }

    private Path filename = null;
    private File file;
    private Manager manager;


    @PostConstruct
    public void init() {
        nameCSV = null;
        descriptionCSV = null;
    }

    public void handleFileUpload(){
        if(file != null){
            try(InputStream is = new FileInputStream(file)){
                // If there is no upload in the time between processing this upload & the next upload of the user,
                // the file attribute could be used directly
                filename = Files.createTempFile(temp, "qs", ".csv");
                Files.copy(is, filename, StandardCopyOption.REPLACE_EXISTING);
                is.close();
                Files.deleteIfExists(file.toPath());
            } catch (IOException e){
                logger.error("Failed to store uploaded .csv file");
                filename = null;
            }
        }
    }

    public void abort() {
        if (filename != null) {
            try{
                Files.deleteIfExists(filename);
            } catch (IOException e){
                logger.error("deleteIfExists - error");
            }

            filename = null;
        }
    }

    public void processCSV() {
        logger.info("processCSV called");
        arrayToDatabase(addQuestionsFromCSV());

    }    /**
     * Uses a inputStream of a csv and converts to a String-Array
     *
     * @param inputStream
     * @return List of List of String ([[question1][correctAnswer][wrongAnswer][wrongAnswer][wrongAnswer]][[question2]...]
     */

    private List<List<String>> addQuestionsFromCSV() {
        logger.info("addQuestionsFromCSV invoked");
        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(filename.toFile())))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Files.deleteIfExists(filename);
            } catch (IOException e){
                logger.error("could not delete");
            }
        }
        return records;
    }

    private void arrayToDatabase(List<List<String>> data) {
        logger.info("arrayToDatabase invoked");

        questionSet = new QuestionSet();
        initQuestionSet(); //new QuestionSet, set difficulty, author, name, description, and connect to HashSet of individual questions
        questionSetService.saveQuestionSet(questionSet);

        Set<Question> questions = new HashSet<Question>();

        for (int wholeQuestion = 0; wholeQuestion < data.size(); wholeQuestion++) {
            Question question = new Question();
            question.setType(QuestionType.text); //csv import only allows questions of type text

            question.setQuestionString(data.get(wholeQuestion).get(0)); // First element of each Question is the question (required)
            question.setRightAnswerString(data.get(wholeQuestion).get(1)); // Second element of each Question is the correct answer (required)
            question.setWrongAnswerString_1(data.get(wholeQuestion).get(2)); // Third element of each Question is the first wrong answer (required)
            if(data.get(wholeQuestion).size()>3) {
                question.setWrongAnswerString_2(data.get(wholeQuestion).get(3)); // Fourth element of each Question is the second wrong answer (optional)
                if (data.get(wholeQuestion).size() > 4) {
                    question.setWrongAnswerString_3(data.get(wholeQuestion).get(4)); // Fifth element of each Question is the third wrong answer (optional)
                    if (data.get(wholeQuestion).size() > 5) {
                        question.setWrongAnswerString_4(data.get(wholeQuestion).get(5)); // Sixth element of each Question is the fourth wrong answer (optional)
                        if (data.get(wholeQuestion).size() > 6) {
                            question.setWrongAnswerString_5(data.get(wholeQuestion).get(6)); // Seventh element of each Question is the fifth wrong answer (optional)
                        }
                    }
                }
            }
            // after each iteration (= one question/line in the csv-file) assign it to the QuestionSet, add to internal list (for displaying it later) and save to DB
            question.setQuestionSet(questionSet);
            questions.add(question);
            questionService.saveQuestion(question);
        }

        // assign all questions (number of lines in the csv) to the QuestionSet
        questionSet.setQuestions(questions);

        // add them to the internal list used in the ui:repeat to show it without time-consuming SELECTOR from the DB
        QSOverviewBean.addQuestionSetForDisplay(questionSet);

        // clear for new import
        nameCSV = null;
        descriptionCSV = null;

        // update to show the new Set
         messageBean.updateComponent("formOverview-QSets:overview-QSets");

        String message = String.format("Successfully imported CSV");
        messageBean.showGlobalInformation(message);
        messageBean.updateComponent("messages");
    }


    public void initQuestionSet() {
        questionSet.setDifficulty(QuestionSetDifficulty.easy);
        questionSet.setAuthor(manager);
        questionSet.setName(nameCSV);
        questionSet.setDescription(descriptionCSV);
        questionSet.setQuestions(new HashSet<>());
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        logger.info("Set manager with ID:" + manager.getId());
        this.manager = manager;
    }

    public String getNameCSV() {
        return nameCSV;
    }

    public void setNameCSV(String nameCSV) {
        this.nameCSV = nameCSV;
    }

    public String getDescriptionCSV() {
        return descriptionCSV;
    }

    public void setDescriptionCSV(String descriptionCSV) {
        this.descriptionCSV = descriptionCSV;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
