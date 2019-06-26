package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;

/**
 * JSON representation for a single {@link Player}.
 */
public class PlayerJSON {

    private int id;
    private String username;
    private String avatar;
    private boolean ready;

    public PlayerJSON(){
        this.ready = false;
    }

    public PlayerJSON(Player p, String pathPrefix){
        this.id = p.getId();
        this.username = p.getUser().getUsername();
        this.ready = false;
        String path = p.getAvatarPath();
        this.avatar = path == null || !path.matches(".*/.*\\.(png|jpg)")
                ? "https://avatars.dicebear.com/v2/gridy/" + username + ".svg" : "/" + pathPrefix + path;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
