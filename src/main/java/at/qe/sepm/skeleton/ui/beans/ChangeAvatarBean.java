package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.PlayerService;
import at.qe.sepm.skeleton.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.faces.context.FacesContext;
import java.io.*;

@Controller
public class ChangeAvatarBean implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ChangeAvatarBean.class);

    private StorageService storageService;
    private PlayerService playerService;
    private ManagerService managerService;

    @Autowired
    public ChangeAvatarBean(StorageService storageService, PlayerService playerService, ManagerService managerService){
        assert storageService != null;
        assert playerService != null;
        assert managerService != null;
        this.storageService = storageService;
        this.playerService = playerService;
        this.managerService = managerService;
    }

    private String filename = null;
    private Player player;


    /**
     * Catches a fileupload and stores file
     *
     * @param file
     * @return
     */
    @RequestMapping(value = "/upload/avatars", method = RequestMethod.POST)
    public ResponseEntity handleFileUpload(@RequestParam("file") MultipartFile file) {
        if(file == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if(filename != null){
            storageService.deleteAvatar(filename);
        }
        try(InputStream is = file.getInputStream()) {
            Manager manager = managerService.getManagerOfPlayer(player);
            filename = storageService.storeAvatar(is, file.getOriginalFilename(), String.valueOf(manager.getId()));
        } catch (IOException e){
            filename = null;
            log.error("Exception while saving Avatar");
            return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
        } finally {
            // TODO update Modal DOM here
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(":formId:body");
            FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(":formId:footer");
        }

        return new ResponseEntity(HttpStatus.OK);
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
