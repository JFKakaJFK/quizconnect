package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.*;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
//import org.primefaces.event.FileUploadEvent;
//import org.primefaces.model.UploadedFile;

import javax.annotation.ManagedBean;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
public class CSVImportBean implements Serializable {


    private static final Logger logger = LoggerFactory.getLogger(ChangeAvatarBean.class);

    @Autowired
    private QuestionSetService questionSetService;

    @Autowired
    private QuestionService questionService;

    private Path temp;

    private QuestionSet questionSet;
    private Set<Question> questions;
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
    private Manager manager;


    /**
     * Catches a fileupload and stores file
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload/csv", method = RequestMethod.POST)
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        try(InputStream is = file.getInputStream()){
            filename = Files.createTempFile(temp, "qs", ".csv");
            Files.copy(is, filename, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            logger.error("Such sad");
            filename = null;
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public void abort() {
        if (filename != null) {
            try{
                Files.deleteIfExists(filename);
            } catch (IOException e){
                logger.error("Much error such sad");
            }

            filename = null;
        }
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public boolean getDisabled() {
        return filename == null;
    }

    public void setDisabled(boolean bool) {
    }


    public void processCSV() {
        arrayToDatabase(addQuestionsFromCSV());

    }    /**
     * Uses a inputStream of a csv and converts to a String-Array
     *
     * @param inputStream
     * @return List of List of String ([[question1][correctAnswer][wrongAnswer][wrongAnswer][wrongAnswer]][[question2]...]
     */

    private List<List<String>> addQuestionsFromCSV() {
        logger.info("addQuestionsFromCSV called");
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
                logger.error("lol");
            }
        }
        return records;
    }

    private void arrayToDatabase(List<List<String>> data) {
        questions = new HashSet<Question>();
        questionSet = new QuestionSet();
        questionSet.setDifficulty(QuestionSetDifficulty.easy);
        questionSet.setAuthor(manager);
        questionSet.setQuestions(questions);
        questionSetService.saveQuestionSet(questionSet);
        logger.info("Created a new QuestionSet with ID: " + questionSet.getId());

        for (int wholeQuestion = 0; wholeQuestion < data.size(); wholeQuestion++) {
            question = new Question();
            question.setType(QuestionType.text);
            question.setQuestionString(data.get(wholeQuestion).get(0));
            question.setRightAnswerString(data.get(wholeQuestion).get(1));
            //for (int wrongAnswers = 2; wrongAnswers < data.get(wholeQuestion).size(); wrongAnswers++) {
            question.setWrongAnswerString_1(data.get(wholeQuestion).get(2));
            question.setWrongAnswerString_2(data.get(wholeQuestion).get(3));
            question.setWrongAnswerString_3(data.get(wholeQuestion).get(4));
            questions.add(question);
            question.setQuestionSet(questionSet);
            questionService.saveQuestion(question);
            //}
        }
    }


}
