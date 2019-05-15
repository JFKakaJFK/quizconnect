package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.ui.beans.SessionInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Scope("request")
public class UploadService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private ManagerService managerService;
    private SessionInfoBean sessionInfoBean;
    private Path temp;

    @Autowired
    public UploadService(ManagerService managerService,
                         SessionInfoBean sessionInfoBean,
                         @Value("${storage.uploads.temporary}") String temp){
        assert managerService != null;
        assert sessionInfoBean != null;
        assert temp != null && !temp.equals("");

        this.managerService = managerService;
        this.temp = Paths.get(temp);
        this.sessionInfoBean = sessionInfoBean;
    }

    /**
     * Returns an uploaded {@link File} by {@link User}
     *
     * Always call {@link UploadService#clearUploads(User)} after the file has been consumed, since the upload directory is only temporary.
     *
     * @return
     */
    public File getUpload(){
        User user = sessionInfoBean.getCurrentUser();
        if(user == null){
            log.warn("unauthenticated upload");
            return null;
        }
        log.debug("user is " + user.getUsername());
        int managerId = user.getManager() == null ? managerService.getManagerOfPlayer(user.getPlayer()).getId() : user.getManager().getId();

        File parentDir = temp.resolve(String.valueOf(managerId)).toFile();
        if(parentDir.exists()){
            File[] files = parentDir.listFiles(new UserFilter(user));
            if(files != null && files.length > 0){
                log.debug("file " + files[0].getName() + " found for user" + user.getUsername());
                return files[0];
            }
        }
        return null;
    }

    /**
     * Clears all uploads by a {@link User}
     *
     * @throws IllegalArgumentException
     * @param user
     */
    public void clearUploads(User user){
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        int managerId = user.getManager() == null ? managerService.getManagerOfPlayer(user.getPlayer()).getId() : user.getManager().getId();

        File parentDir = temp.resolve(String.valueOf(managerId)).toFile();
        if(parentDir.exists()){
            File[] files = parentDir.listFiles(new UserFilter(user));
            if(files != null && files.length > 0){
                for(File f: files){
                    try {
                        Files.deleteIfExists(f.toPath());
                    } catch (IOException e){
                        log.warn("Could not delete file '" + f.getName() + "'");
                    }
                }
            }
        }
    }

    private class UserFilter implements FileFilter {

        private String username;

        private UserFilter(User user){
            if(user == null){
                throw new IllegalArgumentException("User cannot be null");
            }
            this.username = user.getUsername();
        }

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName().toLowerCase();
            return name.startsWith(username + ".");
        }
    }
}
