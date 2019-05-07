package at.qe.sepm.skeleton.services;

import at.qe.sepm.skeleton.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private UserService userService;
    private ManagerService managerService;
    private Path temp;

    @Autowired
    public UploadService(UserService userService,
                         ManagerService managerService,
                         @Value("${storage.uploads.temporary}") String temp){
        assert userService != null;
        assert managerService != null;
        assert temp != null && !temp.equals("");

        this.userService = userService;
        this.managerService = managerService;
        this.temp = Paths.get(temp);
    }

    /**
     * Returns an uploaded {@link File} by {@link User}
     *
     * Always call {@link UploadService#clearUploads(User)} after the file has been consumed, since the upload directory is only temporary.
     *
     * @param user
     * @return
     */
    public File getUpload(User user){
        if(user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        int managerId = user.getManager() == null ? managerService.getManagerOfPlayer(user.getPlayer()).getId() : user.getManager().getId();

        File parentDir = temp.resolve(String.valueOf(managerId)).toFile();
        if(parentDir.exists()){
            File[] files = parentDir.listFiles(new UserFilter(user));
            if(files.length > 0){
                return files[0];
            }
        }
        return null;
    }

    /**
     * Clears all uploads by a {@link User}
     *
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
            if(files.length > 0){
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

        public UserFilter(User user){
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
