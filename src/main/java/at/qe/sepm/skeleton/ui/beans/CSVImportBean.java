package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.services.*;
import at.qe.sepm.skeleton.ui.controllers.QCModalController;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
//import org.primefaces.event.FileUploadEvent;
//import org.primefaces.model.UploadedFile;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
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
    private Manager manager;


    @PostConstruct
    public void init() {
        //nameCSV = "";
        //descriptionCSV = "";
        //questions = new HashSet<>();
    }
    /**
     * Catches a fileupload and stores file
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload/csv", method = RequestMethod.POST)
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) {
        logger.info("filename: " + file.getOriginalFilename());
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
        logger.info("add Questions from CSV");
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

    //FIXME: This is so bad it actually hurts a bit
    private void arrayToDatabase(List<List<String>> data) {
        logger.info("called A2DB");
        questionSet = new QuestionSet();
        initQuestionSet(); //new QuestionSet, set difficulty, author, name, description, and connect to HashSet of individual questions
        questionSetService.saveQuestionSet(questionSet);

        Set<Question> questions = new HashSet<>();

        for (int wholeQuestion = 0; wholeQuestion < data.size(); wholeQuestion++) {
            //initQuestion(); //new Question-Object set to text

            Question question = new Question();
            question.setType(QuestionType.text);

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

            question.setQuestionSet(questionSet);
            questions.add(question);
            questionService.saveQuestion(question);
        }

        questionSet.setQuestions(questions);

        QSOverviewBean.addQuestionSetForDisplay(questionSet);
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


    /* Obsolete?
    public void initQuestion() {
        question = new Question();
        question.setType(QuestionType.text);
    }*/

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        logger.info("Set manager with ID:" + manager.getId());
        this.manager = manager;
    }

    public Path getFilename() {
        return filename;
    }

    public void setFilename(Path filename) {
        this.filename = filename;
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
}
