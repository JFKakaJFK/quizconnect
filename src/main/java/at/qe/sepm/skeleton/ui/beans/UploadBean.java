package at.qe.sepm.skeleton.ui.beans;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.*;

// @MultipartConfig
@Controller
public class UploadBean {

    private String type = "no file";

    // @PostMapping("/avatar")
    @RequestMapping(value = "/avatar", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        System.out.println(file.getName());
        System.out.println(file.getContentType());
        System.out.println(file.getSize());
        this.type = file.getContentType();

        File f = new File("uploads-test/test.svg");
        try(InputStream is = file.getInputStream(); OutputStream os = new FileOutputStream(f)){
            IOUtils.copy(is, os);
        } catch (IOException e){
            System.out.println("ERROR");
        }
        // TODO wrapper json class/whatever https://stackoverflow.com/questions/30895286/spring-mvc-how-to-return-simple-string-as-json-in-rest-controller
        return "{\"success\":\"huge\"}";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
