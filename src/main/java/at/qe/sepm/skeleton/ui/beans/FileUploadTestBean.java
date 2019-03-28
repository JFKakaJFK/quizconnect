package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.services.StorageService;
import at.qe.sepm.skeleton.ui.controllers.ImageController;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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


    private StorageService storageService;
    private String filename = null;

    @Autowired
    public FileUploadTestBean(StorageService storageService){
        this.storageService = storageService;
    }

    // TODO
    String manager = "carlos";

    /**
     * Catches a fileupload and stores file
     *
     * @param event
     */
    public void handleAnswerUpload(FileUploadEvent event){

        try {
            UploadedFile upload = event.getFile();
            filename = storageService.storeAnswer(upload.getInputstream(), upload.getFileName(), manager);
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
            filename = storageService.storeAvatar(upload.getInputstream(), upload.getFileName(), manager);
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
            return "http://localhost:8080/" + "thumbnails/none/default.png";
        }
        Path path = Paths.get(filename);
        //path.toAbsolutePath().toString();
        return "http://localhost:8080/" + "thumbnails/" + manager + "/" + path.getFileName();
    }
}