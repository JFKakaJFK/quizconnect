package at.qe.sepm.skeleton.socket.events;

/**
 * JSON representation of a chat message event.
 */
public class ChatMessageEvent extends SocketEvent {

    private ChatMessageJSON message;

    public ChatMessageEvent(){}

    public ChatMessageEvent(String message, String from, int playerId, int id){
        this.setEvent("chatMessage");
        this.message = new ChatMessageJSON(message, from, playerId, id);
    }

    public ChatMessageJSON getMessage() {
        return message;
    }

    public void setMessage(ChatMessageJSON message) {
        this.message = message;
    }

}
