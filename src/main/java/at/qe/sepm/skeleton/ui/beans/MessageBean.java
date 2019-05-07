package at.qe.sepm.skeleton.ui.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Bean to show a message for a given id with text.
 *
 * @author Simon Jenewein
 */

@Controller

public class MessageBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Show success message
     *
     * @param id
     * @param text
     */
    public void showInformation(String id, String text) {
        logger.info("messageBean called with target-id: " + id + " and text: "+text);
        if(FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(id,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", text)
            );
        }
    }

    public void showGlobalInformation(String text) {
        logger.info("messageBean called with global and text: "+text);
        if(FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", text)
            );
        }
    }
    /**
     * Show error message
     * @param id
     * @param text
     */
    public void showError(String id, String text) {
        if(FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(id,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler", text)
            );
        }
    }

    public void updateComponent(String id) {
        logger.info("updating component: " + id);
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(id);
    }
}
