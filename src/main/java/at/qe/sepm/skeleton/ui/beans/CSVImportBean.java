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
public class CSVImportBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ChangeAvatarBean.class);

    @Autowired
    private MessageBean messageBean;

    @Autowired
    private QSOverviewBean QSOverviewBean;

    private Path temp;

    private String nameCSV;
    private String descriptionCSV;

    @Autowired
    public CSVImportBean(CSVImportService csvImportService,
                         @Value("${storage.uploads.temporary}") String temp) {
        assert csvImportService != null;

        this.temp = Paths.get(temp);
        this.csvImportService = csvImportService;
    }

    private Path filename = null;
    private File file;
    private Manager manager;
    private CSVImportService csvImportService;


    @PostConstruct
    public void init() {
        nameCSV = null;
        descriptionCSV = null;
    }

    public void handleFileUpload(){
        if(file != null){
            try(InputStream is = new FileInputStream(file)){
                // If there is no upload in the time between processing this upload & the _next upload of the user,
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
        QuestionSet questionSet;
        questionSet = csvImportService.importQuestionSetFromCSV(filename.toFile(), manager, nameCSV, descriptionCSV);

        QSOverviewBean.addQuestionSetForDisplay(questionSet);

        //messageBean.updateComponent("formOverview-QSets:overview-QSets");

        //String message = String.format("Successfully imported CSV");
        //messageBean.showGlobalInformation(message);
        //messageBean.updateComponent("messages");

        descriptionCSV = null;
        nameCSV = null;
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
