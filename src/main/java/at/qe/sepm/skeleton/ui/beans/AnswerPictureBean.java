package at.qe.sepm.skeleton.ui.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Bean for getting the path to an answer picture.
 */
@Controller
@Scope("request")
public class AnswerPictureBean {

    @Value("${storage.api.answers}")
    private String answers;

    /**
     * @param path
     * 		Relative path of the picture.
     * @return The complete file path for the answer picture.
     */
    public String getAnswers(String path) {
        if(path == null || !path.matches(".*/.*\\.(png|jpg)")){
            return "/" + answers + "default/default/default.png";
        }
        return "/" + answers + path;
    }
}
