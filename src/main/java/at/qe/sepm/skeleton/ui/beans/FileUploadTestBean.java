package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.services.StorageService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * Bean for testing file storage functionality.
 */
@ManagedBean
public class FileUploadTestBean {

    private StorageService storageService;
    private String avatar = null;
    private String answer = null;
    private String avatarEndpoint;
    private String answerEndpoint;

    // TODO: find out if endpoint url is not needed, previously is somehow was necessary
    @Autowired
    public FileUploadTestBean(
            StorageService storageService,
            @Value("${storage.api.endpoint}") String endpoint,
            @Value("${storage.api.avatars}") String avatars,
            @Value("${storage.api.answers}") String answers){
        this.storageService = storageService;
        this.avatarEndpoint = avatars; //endpoint + avatars;
        this.answerEndpoint = answers; //endpoint + answers;
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
            answer = storageService.storeAnswer(upload.getInputstream(), upload.getFileName(), manager);
        } catch (IOException e){
            answer = null;
        }
    }

    /**
     * Catches a fileupload and stores file
     *
     * @param event
     */
    public void handleAvatarUpload(FileUploadEvent event){

        try {
            UploadedFile upload = event.getFile();
            avatar = storageService.storeAvatar(upload.getInputstream(), upload.getFileName(), manager);
        } catch (IOException e){
            avatar = null;
        }
    }

    /**
     * BOILERPLATE
     */
    public void save(){
        if(avatar == null){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "Error", "Sie müssen zuerst eine Datei auswählen!")
            );
            return;
        }

        // save user using service ...

        avatar = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO, "Success", "Datei erfolgreich gespeichert")
        );
    }

    /**
     * BOILERPLATE
     */
    public void abort() {
        // ... call when user aborts action
        if(avatar != null){
            storageService.deleteAvatar(avatar);
        }
        if(answer != null){
            storageService.deleteAnswer(avatar);
        }
    }

    /**
     * returns the source of the currently uploaded image
     *
     * @return
     */
    public String getAvatar() {
        if(avatar == null){
            return avatarEndpoint + "bad/request.png";
        }
        return avatarEndpoint + avatar;
    }

    /**
     * returns the source of the currently uploaded image
     *
     * @return
     */
    public String getAnswer() {
        if(answer == null){
            return answerEndpoint + "bad/request.png";
        }
        return answerEndpoint + answer;
    }
}