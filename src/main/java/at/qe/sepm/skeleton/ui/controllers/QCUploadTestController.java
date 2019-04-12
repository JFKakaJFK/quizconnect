package at.qe.sepm.skeleton.ui.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class QCUploadTestController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void handleUpload(@RequestParam("parameterName")MultipartFile file){
        System.out.println("UPLOAD - START");
        System.out.println(file.getOriginalFilename());
        System.out.println("END");

        // TODO: IMPORTANT: do file validation here
    }
}
