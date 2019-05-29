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

    public void init(String location) throws NoSuchElementException {
        Path CSVLocation = Paths.get(location);
        manager = getAuthorManagerFromDB();

        File directory = new File(CSVLocation.toUri());
        File [] directoryListing = directory.listFiles();
        if (directoryListing == null) {
            throw new NoSuchElementException("Directory with QuestionSets CSVs cannot be null");
        }
        for (File set : directoryListing) {
            importQuestionSetFromCSV(set, manager, set.getName(), "This is a QuestionSet about " + stringModifier(set.getName()));
        }
    }

    public QuestionSet importQuestionSetFromCSV(File file, Manager manager, String name, String description){
        return saveQuestionSet(addQuestionsFromCSV(file), manager, name, description);
    }


    private List<Question> addQuestionsFromCSV(File file) {
        logger.info("addQuestionsFromCSV invoked");
        List<Question> questions = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(file)))) {
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                try{
                    questions.add(parseAndValidateQuestion(Arrays.asList(values)));
                } catch (IllegalArgumentException e){
                    logger.error("CSV Reader failed");
                }
            }
            return questions;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(file.toPath().toString().contains("temp")) {
                    Files.deleteIfExists(file.toPath());
                }
            } catch (IOException e){
                logger.error("could not delete");
            }
        }
        return questions;
    }

    private QuestionSet saveQuestionSet(List<Question> questionList, Manager manager, String name, String description) {
        logger.info("arrayToDatabase invoked");

        questionSet = new QuestionSet();
        initQuestionSet(manager, name, description); //new QuestionSet, set difficulty, author, name, description, and connect to HashSet of individual questions
        questionSetService.saveQuestionSet(questionSet);

        Set<Question> questions = new HashSet<>(); // needed since before save, hashcode and equals don't differentiate between entities (no set usable)
        for (Question question: questionList) {
            question.setType(QuestionType.text); //csv import only allows questions of type text
            question.setQuestionSet(questionSet);
            Question q = questionService.saveQuestion(question);
            questions.add(q);
        }

        // assign all questions (number of lines in the csv) to the QuestionSet
        questionSet.setQuestions(questions);
        return questionSetService.saveQuestionSet(questionSet);
    }

    private Question parseAndValidateQuestion(List<String> tokens){
        if(tokens.size() < 3){
            throw new IllegalArgumentException("Too few tokens in line");
        }
        Question q = new Question();
        q.setQuestionString(tokens.get(0).trim());
        if(nullOrEmpty(q.getQuestionString())){
            throw new IllegalArgumentException("QuestionString invalid");
        }
        q.setRightAnswerString(tokens.get(1).trim());
        if(nullOrEmpty(q.getQuestionString())){
            throw new IllegalArgumentException("RightAnswerString invalid");
        }
        q.setWrongAnswerString_1(tokens.get(2).trim());
        if(nullOrEmpty(q.getQuestionString())){
            throw new IllegalArgumentException("WrongAnswerString invalid");
        }
        if(tokens.size() > 3){
            q.setWrongAnswerString_2(tokens.get(3).trim());
            if(q.getWrongAnswerString_2().equals("")){
                q.setWrongAnswerString_2(null);
            }
        }
        if(tokens.size() > 4){
            q.setWrongAnswerString_3(tokens.get(4).trim());
            if(q.getWrongAnswerString_3().equals("")){
                q.setWrongAnswerString_3(null);
            }
        }
        if(tokens.size() > 5){
            q.setWrongAnswerString_4(tokens.get(5).trim());
            if(q.getWrongAnswerString_4().equals("")){
                q.setWrongAnswerString_4(null);
            }
        }
        if(tokens.size() > 6){
            q.setWrongAnswerString_5(tokens.get(6).trim());
            if(q.getWrongAnswerString_5().equals("")){
                q.setWrongAnswerString_5(null);
            }
        }
        return q;
    }

    private boolean nullOrEmpty(String s){
        return s == null || s.equals("");
    }

    /**
     * Initializes QuestionSet and defines Difficulty by checking nameCSV ending number
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
