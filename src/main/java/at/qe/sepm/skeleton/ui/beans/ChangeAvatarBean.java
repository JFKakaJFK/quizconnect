package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.StorageService;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.Serializable;

@Controller
@Scope("view")
public class ChangeAvatarBean implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ChangeAvatarBean.class);

    private StorageService storageService;
    private PlayerService playerService;

    @Autowired
    public ChangeAvatarBean(StorageService storageService, PlayerService playerService){
        assert storageService != null;
        assert playerService != null;
        this.storageService = storageService;
        this.playerService = playerService;
    }

    private String filename = null;
    private String status = "";
    private Player player;
    private boolean disabled = true;

    /**
     * Catches a fileupload and stores file
     *
     * @param event
     */
    public void handleAvatarUpload(FileUploadEvent event){

        if(filename != null){
            storageService.deleteAvatar(filename);
        }
        try {
            UploadedFile upload = event.getFile();
            filename = storageService.storeAvatar(upload.getInputstream(), upload.getFileName(), "1232"); // TODO player.getCreator().getId().toString());
            status = "Upload successful";
        } catch (IOException e){
            filename = null;
            status = "Upload failed";
        }
    }

    public void saveAvatar(){
        if(filename == null){
            status = "No file";
            return;
        }
        status = "Avatar changed successfully";

        player.setAvatarPath(filename);
        playerService.savePlayer(player);
        filename = null;
    }

    public void abort(){
        if(filename != null){
            storageService.deleteAvatar(filename);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        status = "";
    }

    public boolean getDisabled(){
        return disabled;
    }

    public void setDisabled(boolean bool){
        this.disabled = bool;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
