package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import java.io.*;
import java.nio.file.Files;

// TODO jdoc

@Controller
@Scope("view")
public class ChangeAvatarBean implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ChangeAvatarBean.class);

    private StorageService storageService;
    private PlayerService playerService;
    private ManagerService managerService;
    private MessageBean messageBean;
    private ProfileBean profileBean;

    private String filename = null;
    private File file;
    private Player player;

    @Autowired
    public ChangeAvatarBean(StorageService storageService, PlayerService playerService, ManagerService managerService, MessageBean messageBean, ProfileBean profileBean){
        assert storageService != null;
        assert playerService != null;
        assert managerService != null;
        assert messageBean != null;
        assert profileBean != null;
        this.storageService = storageService;
        this.playerService = playerService;
        this.managerService = managerService;
        this.messageBean = messageBean;
        this.profileBean = profileBean;
        this.player = profileBean.getPlayer();
    }

    // TODO jdoc
    public void handleFileUpload(){
        if(player == null) return;
        if(file != null){
            log.debug("file for " + player.getUser().getUsername() + "is " + file.getName());
            if(filename != null){
                storageService.deleteAvatar(filename);
            }

            try {
                Manager manager = managerService.getManagerOfPlayer(player);
                filename = storageService.storeAvatar(file, manager.getId().toString());
                if(filename == null){
                    messageBean.alertError("Error", "File could not be stored.");
                    messageBean.updateComponent("messages");
                } else {
                    Files.deleteIfExists(file.toPath());
                }
            } catch (IOException e){
                filename = null;
                log.error("Exception while saving Avatar");
            }
        }
    }

    //TODO: JavaDoc for saveAvatar
    public void saveAvatar(){
        if(player == null) return;
        if(filename == null){
            return;
        }

        String old = player.getAvatarPath();

        player.setAvatarPath(filename);
        this.player = playerService.savePlayer(player);

        if(old != null){
            storageService.deleteAvatar(old);
        }
        filename = null;
        profileBean.setPlayer(player);
    }

    //TODO: JavaDoc for abort
    public void abort(){
        if(filename != null){
            storageService.deleteAvatar(filename);
            filename = null;
        }
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
