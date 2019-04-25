package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class QCUploadTestController {

   @Autowired
   private StorageService storageService;

   File file;

   public void handleFileUpload() throws IOException {
       if(file != null){
           System.out.println(file.getName());
           // do something w/ file here
           // at least save somewhere else (or call clear method later)
           storageService.storeAvatar(new FileInputStream(file), file.getName(), "managerId");
           Files.deleteIfExists(file.toPath()); // Should always be deleted after usage
       } else {
           System.out.println("error at file upload");
       }
   }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
