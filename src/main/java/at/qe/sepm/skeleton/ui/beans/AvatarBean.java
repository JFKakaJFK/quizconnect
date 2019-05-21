package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("request")
public class AvatarBean {

    private final String TYPE = "gridy";

    @Value("${storage.api.avatars}")
    private String avatars;

    //TODO: JavaDoc for getAvatar
    public String getAvatar(String path, String username) {
        if(path == null || !path.matches(".*/.*\\.(png|jpg)")){
            return "https://avatars.dicebear.com/v2/" + TYPE + "/" + username + ".svg";
            // return "/" + avatars + "default/avatar.png";
        }
        return "/" + avatars + path;
    }

    public String getAvatar(Player player) {
        if(player == null){
            return "";
        }
        String path = player.getAvatarPath();
        if(path == null || !path.matches(".*/.*\\.(png|jpg)")){
            return "https://avatars.dicebear.com/v2/" + TYPE + "/" + player.getUser().getUsername() + ".svg";
            // return "/" + avatars + "default/avatar.png";
        }
        return "/" + avatars + path;
    }
}
