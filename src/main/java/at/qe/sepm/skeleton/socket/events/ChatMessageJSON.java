package at.qe.sepm.skeleton.socket.events;

import java.util.Date;

/**
 * JSON representation of a single chat message.
 */
public class ChatMessageJSON {

    private int id;
    private int playerId;
    private String message;
    private String from;
    private long timestamp;

    public ChatMessageJSON(){}

    public ChatMessageJSON(String message, String from, int playerId, int id){
        this.message = message;
        this.from = from;
        this.id = id;
        this.playerId = playerId;
        this.timestamp = new Date().getTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
