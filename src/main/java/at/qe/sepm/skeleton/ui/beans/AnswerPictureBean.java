package at.qe.sepm.skeleton.ui.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("request")
public class AnswerPictureBean {

    @Value("${storage.api.answers}")
    private String answers;

    //TODO: JavaDoc for getAvatar
    public String getAnswers(String path) {
        if(path == null || !path.matches(".*/.*\\.(png|jpg)")){
            return "/" + answers + "default/default/default.png";
        }
        return "/" + answers + path;
    }
}
