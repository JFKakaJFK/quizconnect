package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.ui.beans.MessageBean;
import at.qe.sepm.skeleton.ui.beans.QSOverviewBean;
import at.qe.sepm.skeleton.utils.AuthenticationUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvBadConverterException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class CSVImportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private QuestionSetService questionSetService;
    private QuestionService questionService;
    private ManagerService managerService;

    private QuestionSet questionSet;

    private Manager manager;

    @Autowired
    public CSVImportService(QuestionSetService questionSetService, QuestionService questionService, ManagerService managerService){
        assert questionSetService != null;
        assert questionService != null;
        assert managerService != null;

        this.questionService = questionService;
        this.questionSetService = questionSetService;
        this.managerService = managerService;
    }

    // TODO Fix it
    public void init(String location) throws NoSuchElementException {
        Path CSVLocation = Paths.get(location);
        manager = getAuthorManagerFromDB();

        File directory = new File(CSVLocation.toUri());
        File [] directoryListing = directory.listFiles();
        if (directoryListing == null) {
            throw new NoSuchElementException("Directory with QuestionSets CSVs cannot be null");
        }
        Arrays.stream(directoryListing).forEach(set -> importQuestionSetFromCSV(set , manager, set.getName(), "This is a QuestionSet about " + stringModifier(set.getName())));
    }

    public void importQuestionSetFromCSV(File file, Manager manager, String name, String description){
        arrayToDatabase(addQuestionsFromCSV(file), manager, name, description);
    }

    //private List<String> questionVariables = new ArrayList<>(Arrays.asList("getWrongAnswerString_1","getWrongAnswerString_2","getWrongAnswerString_3","getWrongAnswerString_4","getWrongAnswerString_5"));


    private List<List<String>> addQuestionsFromCSV(File file) {
        logger.info("addQuestionsFromCSV invoked");
        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file)))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e){
                logger.error("could not delete");
            }
        }
        return records;
    }

    // TODO check for accurate arguments
    private void arrayToDatabase(List<List<String>> data, Manager manager, String name, String description) {
        logger.info("arrayToDatabase invoked");

        questionSet = new QuestionSet();
        initQuestionSet(manager, name, description); //new QuestionSet, set difficulty, author, name, description, and connect to HashSet of individual questions
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

        // add them to the internal list used in the ui:repeat to show it without time-consuming load from the DB
        // QSOverviewBean.addQuestionSetForDisplay(questionSet);

        // clear for new import
        //nameCSV = null;
        //descriptionCSV = null;

        // update to show the new Set
        /*
        messageBean.updateComponent("formOverview-QSets:overview-QSets");

        String message = String.format("Successfully imported CSV");
        messageBean.showGlobalInformation(message);
        messageBean.updateComponent("messages");

         */
    }

    /**
     * Initializes QuestionSet and defines Difficulty by checking nameCSV ending number
     * Currently does not work with Upload --> TODO
     * @param nameCSV
     * @param descriptionCSV
     */
    private void initQuestionSet(Manager manager, String nameCSV, String descriptionCSV) {
        if (nameCSV.contains("1")){
            questionSet.setDifficulty(QuestionSetDifficulty.easy);
        }
        else if (nameCSV.contains("2")){
            questionSet.setDifficulty(QuestionSetDifficulty.hard);
        }
        else{
            questionSet.setDifficulty(QuestionSetDifficulty.easy);
        }

        if (nameCSV.contains(".csv")){
            questionSet.setName(stringModifier(nameCSV));
        }
        else{
            questionSet.setName(nameCSV);
        }
        questionSet.setAuthor(manager);
        questionSet.setDescription(descriptionCSV);
        questionSet.setQuestions(new HashSet<>());
    }

    private Manager getAuthorManagerFromDB() {
        AuthenticationUtil.configureAuthentication("MANAGER");
        return managerService.getManagerById(101);
    }

    /**
     * This way of changing the name of QSet is ugly and has to replaced
     * @param string
     * @return
     */
    private String stringModifier(String string){
        string = string.replaceFirst(string.substring(0,1), string.substring(0, 1).toUpperCase());
        string = string.substring(0, string.length()-6);
        return string;
    }

}
