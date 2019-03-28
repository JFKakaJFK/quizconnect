package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.services.StorageService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Bean for testing file storage functionality.
 */
@ManagedBean
public class FileUploadTestBean {

    @Autowired
    private StorageService storageService;

    private String filename = null;

    /**
     * Catches a fileupload and stores file
     *
     * @param event
     */
    public void handleAnswerUpload(FileUploadEvent event){

        try {
            UploadedFile upload = event.getFile();
            filename = storageService.storeAnswer(upload.getInputstream(), upload.getFileName(), "kevin");
        } catch (IOException e){
            filename = null;
        }
        System.out.println(filename);
    }

    /**
     * Catches a fileupload and stores file
     *
     * @param event
     */
    public void handleAvatarUpload(FileUploadEvent event){

        try {
            UploadedFile upload = event.getFile();
            filename = storageService.storeAvatar(upload.getInputstream(), upload.getFileName(), "admin");
            //filename = storageService.storeAvatar(event.getFile(), "admin");
        } catch (IOException e){
            filename = null;
        }

        System.out.println(filename);
    }

    /**
     * BOILERPLATE
     */
    public void save(){
        if(filename == null){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error", "Sie müssen zuerst eine Datei auswählen!")
            );
            return;
        }

        // ...

        filename = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Success", "Datei erfolgreich gespeichert")
        );
    }

    /**
     * Deletes stored file
     *
     * @throws IOException
     */
    public void abort() throws IOException {
        if(filename != null){
            //storageService.deleteAvatar(filename);
            //storageService.deleteAnswer(filename);
        }
    }

    public String getFilename() {
        if(filename == null){
            return "";
        }
        //Path path = Paths.get(filename);
        //path.toAbsolutePath().toString();
        return filename;
    }
}