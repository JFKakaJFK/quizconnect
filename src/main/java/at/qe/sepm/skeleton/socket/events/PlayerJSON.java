package at.qe.sepm.skeleton.socket.events;

import at.qe.sepm.skeleton.model.Player;

public class PlayerJSON {

    private int id;
    private String username;
    private String avatar;
    private boolean ready;

    public PlayerJSON(){
        this.ready = false;
    }

    public PlayerJSON(Player p, boolean ready){
        this.id = p.getId();
        this.username = p.getUser().getUsername();
        this.avatar = p.getAvatarPath();
        this.ready = ready;
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
