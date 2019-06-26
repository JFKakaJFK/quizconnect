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

/**
 * Bean that connects frontend with CSVImportService
 *
 * @author Johannes Koch, Johannes Spies, Simon Triendl
 */
@Controller
@Scope("view")
public class CSVImportBean implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ChangeAvatarBean.class);

    @Autowired
    private MessageBean messageBean;

    @Autowired
    private QSOverviewBean QSOverviewBean;

    @Autowired
    private SessionInfoBean sessionInfoBean;

    private Path temp;

    private String nameCSV;
    private String descriptionCSV;
    private boolean uploadStatus;

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
        manager = sessionInfoBean.getCurrentUser().getManager();
        nameCSV = null;
        descriptionCSV = null;
        uploadStatus = false;
    }

    /**
     * Handles the upload of csv files.
     */
    public void handleFileUpload(){
        if(file != null){
            try(InputStream is = new FileInputStream(file)){
                // If there is no upload in the time between processing this upload & the _next upload of the user,
                // the file attribute could be used directly
                filename = Files.createTempFile(temp, "qs", ".csv");
                Files.copy(is, filename, StandardCopyOption.REPLACE_EXISTING);
                is.close();
                Files.deleteIfExists(file.toPath());
                uploadStatus = true;
            } catch (IOException e){
                logger.error("Failed to store uploaded .csv file");
                filename = null;
            }
        }
    }

    /**
     * On abort, any uploaded files are deleted.
     */
    public void abort() {
        uploadStatus = false;
        descriptionCSV = null;
        nameCSV = null;

        try{
            if (filename != null) Files.deleteIfExists(filename);
        } catch (IOException e){
            logger.error("deleteIfExists - error");
        } finally {
            filename = null;
        }
    }

    /**
     * Saves the new {@link QuestionSet}
     */
    public void processCSV() {
        logger.info("processCSV called");

        QSOverviewBean.addQuestionSetForDisplay(csvImportService.importQuestionSetFromCSV(filename.toFile(), manager, nameCSV, descriptionCSV));

        messageBean.alertInformation("Success", "Successfully imported CSV");
        messageBean.updateComponent("messages");
        abort();
    }

    /**
     * Returns true, if the confirm action should be disabled.
     *
     * @return
     */
    public boolean disableConfirm(){
        return !uploadStatus || filename == null || descriptionCSV == null || descriptionCSV.equals("") || nameCSV == null || nameCSV.equals("");
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
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

    public boolean getUploadStatus() {
        return uploadStatus;
    }
}
