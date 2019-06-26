package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.model.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Bean for getting the avatar picture of a {@link Player}.
 */
@Controller
@Scope("request")
public class AvatarBean {

    private final String TYPE = "gridy";

    @Value("${storage.api.avatars}")
    private String avatars;
    
    /**
     * @param path
     * 		Relative path of the avatar picture.
     * @param username
     * 		Username of the Player to load the avatar of.
     * @return The full file path of the avatar image or the address to a default avatar.
     */
    public String getAvatar(String path, String username) {
        if(path == null || !path.matches(".*/.*\\.(png|jpg)")){
            return "https://avatars.dicebear.com/v2/" + TYPE + "/" + username + ".svg";
            // return "/" + avatars + "default/avatar.png";
        }
        return "/" + avatars + path;
    }
    
    /**
     * @param player
     * 		Player to load the avatar of.
     * @return The full file path of the avatar image or the address to a default avatar.
     */
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
