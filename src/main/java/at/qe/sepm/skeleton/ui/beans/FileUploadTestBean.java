package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.services.StorageService;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Bean for testing file storage functionality.
 */
@ManagedBean
public class FileUploadTestBean {

    @Autowired
    private StorageService storageService;

    private String type;
    private String filename = null;

    /**
     * Catches a fileupload and stores file
     *
     * @param event
     */
    public void handleAnswerUpload(FileUploadEvent event){

        type = event.getFile().getContentType();

        try {
            filename = storageService.storeAnswer(event.getFile(), "kevin");
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

        type = event.getFile().getContentType();

        try {
            filename = storageService.storeAvatar(event.getFile(), "admin");
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
                FacesMessage.SEVERITY_INFO, "Success", "Datei vom Typ '" + type + "' erfolgreich gespeichert")
        );
    }

    /**
     * Deletes stored file
     *
     * @throws IOException
     */
    public void abort() throws IOException {
        if(filename != null){
            storageService.deleteFile(filename);
        }
    }
}