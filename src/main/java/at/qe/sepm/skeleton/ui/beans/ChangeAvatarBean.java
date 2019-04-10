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
    private Player player;

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
        } catch (IOException e){
            filename = null;
        }
    }

    public void saveAvatar(){
        if(filename == null){
            return;
        }

        String old = player.getAvatarPath();

        player.setAvatarPath(filename);
        playerService.savePlayer(player);

        if(old != null){
            storageService.deleteAvatar(old);
        }
        filename = null;
    }

    public void abort(){
        if(filename != null){
            storageService.deleteAvatar(filename);
            filename = null;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean getDisabled(){
        return filename == null;
    }

    public void setDisabled(boolean bool){}

    public String getFilename() {
        if(filename == null && player != null){
            return player.getAvatarPath();
        }
        return filename;
    }

    public void setFilename(String filename) {}
}
