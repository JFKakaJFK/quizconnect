package at.qe.sepm.skeleton.ui.beans;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("request")
public class AvatarBean {

    @Value("${storage.api.avatars}")
    private String avatars;

    public String getAvatar(String path) {
        if(!path.matches(".*/.*\\.(png|jpg)")){
            return "/" + avatars + "default/avatar.png";
        }
        return "/" + avatars + path;
    }
}
