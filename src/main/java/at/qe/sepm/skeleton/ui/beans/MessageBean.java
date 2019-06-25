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
 */

@Controller

public class MessageBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /**
     * Show success message
     *
     * @param id
     * 		Target id.
     * @param text
     * 		Text to be displayed.
     */
    public void showInformation(String id, String text) {
        logger.info("messageBean called with target-id: " + id + " and text: "+text);
        if(FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(id,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", text)
            );
        }
    }
    
    /**
     * Show success message
     *
     * @param summary
     *      Short summary.
     * @param text
     * 		Text to be displayed.
     */
    public void alertInformation(String summary, String text) {
        logger.info("messageBean called with global and text: "+text);
        if(FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, summary, text)
            );
        }
    }

    /**
     * Show error message
     *
     * @param summary
     *      Short summary.
     * @param text
     * 		Text to be displayed.
     */
    public void alertError(String summary, String text) {
        logger.info("messageBean called with global and text: " + text);
        if(FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, text)
            );
        }
    }

    /**
     * Updates a message component.
     *
     * @param id
     *  Id to update.
     */
    public void updateComponent(String id) {
        logger.info("updating component: " + id);
        FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add(id);
    }
}
