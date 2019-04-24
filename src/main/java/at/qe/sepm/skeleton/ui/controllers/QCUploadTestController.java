package at.qe.sepm.skeleton.ui.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Controller
public class QCUploadTestController {

   File file;

   public void handleFileUpload() throws IOException {
       if(file != null){
           System.out.println(file.getName());
           // do something w/ file here
           // at least save somewhere else (or call clear method later)
           Files.deleteIfExists(file.toPath());
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
