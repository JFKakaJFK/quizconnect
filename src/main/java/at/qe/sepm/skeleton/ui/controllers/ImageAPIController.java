package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.services.StorageService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


/**
 * This controller responds to requests to the avatar & answer endpoints and responds either
 * with the sought image or a default file.
 *
 *
 * @author Johannes Koch
 */
@Controller
@RequestMapping
public class ImageAPIController {

    private static final Logger log = LoggerFactory.getLogger(ImageAPIController.class);
    private StorageService storageService;
    @Value("${storage.avatars.default}")
    private String defaultAvatar;
    @Value("${storage.answers.default}")
    private String defaultAnswer;

    @Autowired
    public ImageAPIController(StorageService storageService){
        this.storageService = storageService;
    }

    /**
     * Catches GET requests for the player avatars and returns corresponding file or default
     *
     * @param response
     * @param manager
     * @param file
     * @param ext
     */
    @RequestMapping(value = "/avatars/{manager}/{file}.{ext}", method = RequestMethod.GET)
    public void getAvatar(HttpServletResponse response, @PathVariable String manager, @PathVariable String file, @PathVariable String ext){
        if(!(ext.toLowerCase().equals("png") || ext.toLowerCase().equals("jpg"))){
            log.error("Request for thumbnail is not well-formed: no image extension");
            sendError(response,400, "Invalid extension. Allowed: (png|jpg)");
            return;
        }

        File img = storageService.loadAvatar(manager + "/" + file + "." + ext).toFile();

        if(!img.exists()){
            log.warn("Could not serve avatars/" + manager + "/" + file + "." + ext + ", serving default");
            img = storageService.loadAvatar(defaultAvatar).toFile();
        }

        sendResponse(response, img, ext);
    }

    /**
     * Catches GET requests for the answer images and returns corresponding file or default
     *
     * @param response
     * @param manager
     * @param file
     * @param ext
     */
    @RequestMapping(value = "/answers/{manager}/{file}.{ext}", method = RequestMethod.GET)
    public void getAnswer(HttpServletResponse response, @PathVariable String manager, @PathVariable String file, @PathVariable String ext){
        if(!(ext.toLowerCase().equals("png") || ext.toLowerCase().equals("jpg"))){
            log.error("Request for thumbnail is not well-formed: no image extension");
            sendError(response,400, "Invalid extension. Allowed: (png|jpg)");
            return;
        }

        File img = storageService.loadAnswer(manager + "/" + file + "." + ext).toFile();

        if(!img.exists()){
            log.warn("Could not serve answers/" + manager + "/" + file + "." + ext + ", serving default");
            img = storageService.loadAnswer(defaultAnswer).toFile();
        }

        sendResponse(response, img, ext);
    }

    /**
     * Sets some http response headers and then sends the file to the requesting user
     *
     * @param response
     * @param file
     * @param type
     */
    private void sendResponse(HttpServletResponse response, File file, String type){
        response.setHeader("cache-control", "max-age=31104000");
        response.setContentType("image/" + type);
        response.setContentLengthLong(file.length());
        response.setDateHeader("Expires", System.currentTimeMillis() + 604800000L); // expires in a week
        // response.setDateHeader("Last-Modified", 0);
        // TODO: set moar headers (maybe get a nice eTag, expires & last modified working)
        try(InputStream is = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
            IOUtils.copy(is, os);
            response.flushBuffer();
        } catch (IOException e){
            log.error("Could not serve " + file.getName());
            response.reset();
            sendError(response, 500, "Could not serve file");
        }
    }

    /**
     * Sends an error
     *
     * @param response
     * @param status
     * @param message
     */
    private void sendError(HttpServletResponse response, int status, String message){
        try {
            response.sendError(status, message);
        } catch (IOException e){
            log.error("Could not send HTTP " + status + ": " + message);
        }
    }
}
