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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

@Controller
@RequestMapping
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    private final StorageService storageService;
    @Value("${storage.thumbnails.location}")
    private String thumbnails;
    @Value("${storage.prefixes.avatar}")
    private String avatarPrefix;
    @Value("${storage.thumbnails.default}")
    private String defaultAvatar;

    @Autowired
    public ImageController(StorageService storageService){
        this.storageService = storageService;
    }

    @RequestMapping(value = "/thumbnails/{manager}/{file}.{type}", method = RequestMethod.GET)
    public void getThumbnail(HttpServletResponse response, @PathVariable String manager, @PathVariable String file, @PathVariable String type){
        File img = Paths.get(thumbnails).resolve(manager).resolve(avatarPrefix).resolve(file + "." + type).toFile();
        // TODO check if manager exists, maybe serve a default on error
        if(manager.equals("none") || !(type.toLowerCase().equals("png") || type.toLowerCase().equals("jpg"))){
            log.error("Request for thumbnails/" + manager + "/" + file + "." + type + " is not well-formed");
            img = Paths.get(thumbnails).resolve(defaultAvatar).toFile();
        }
        if(!img.exists()){
            log.error("Could not serve thumbnails/" + manager + "/" + file + "." + type + ": Does not exist");
            img = Paths.get(thumbnails).resolve(defaultAvatar).toFile();
        }

        try {
            response.setHeader("cache-control", "max-age=31104000");
            // TODO: set moar headers (maybe get a nice eTag, expires & last modified working)
            response.setContentType("image/" + type);
            response.setContentLengthLong(img.length());
            IOUtils.copy(new FileInputStream(img), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e){
            log.error("Could not serve thumbnails/" + manager + "/" + file + "." + type);
        }
    }
}
