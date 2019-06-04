package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.model.User;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.UploadService;
import at.qe.sepm.skeleton.services.UserService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.security.Principal;
import java.util.List;

/**
 * This {@link Controller} handles all file uploads
 */
@Controller
@Scope("request")
public class UploadAPIController {

    private UserService userService;
    private ManagerService managerService;
    private Path temp;
    private UploadService uploadService;

    @Autowired
    public UploadAPIController(UserService userService,
                               ManagerService managerService,
                               @Value("${storage.uploads.temporary}") String temp,
                               UploadService uploadService){
        assert userService != null;
        assert managerService != null;
        assert temp != null && !temp.equals("");
        assert uploadService != null;

        this.userService = userService;
        this.managerService = managerService;
        this.temp = Paths.get(temp);
        this.uploadService = uploadService;
    }

    /**
     * Handles all uploads and saves them in a way such that {@link at.qe.sepm.skeleton.services.UploadService} can retrieve them.
     *
     * @param files
     *          The uploaded files.
     * @return
     *          Returns a HTTP response indicating if the upload was successful.
     */
    @RequestMapping(value = "/uploads", method = RequestMethod.POST)
    public ResponseEntity handleUpload(@RequestParam("files[]") List<MultipartFile> files, Principal principal){
        if(principal == null){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        String username = principal.getName();
        MultipartFile file = null;
        // get the first valid file
        for(int i = 0; i < files.size(); i++){
            MultipartFile f = files.get(i);
            if(f != null && !f.getOriginalFilename().equals("")){
                file = f;
                break;
            }
        }

        if(file == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        User user = userService.loadUser(username);
        if(user == null){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        int managerId = user.getManager() == null ? managerService.getManagerOfPlayer(user.getPlayer()).getId() : user.getManager().getId();

        uploadService.clearUploads(user);
        // save file under /{upload-dir}/{manager id}/{user name}.{ext}
        // if file already exists, overwrite current upload file
        try(InputStream is = file.getInputStream()) {
            Path path = temp.resolve(String.valueOf(managerId)).resolve(username + "." + FilenameUtils.getExtension(file.getOriginalFilename()));
            Files.createDirectories(path);
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(HttpStatus.OK);
    }
}
