package at.qe.sepm.skeleton.ui.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;

@Controller
public class QCModalController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void defaultAction(){
    }

    public void updateComponent(String id) {
        logger.info("updating component: " + id);
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(id);
    }
}
