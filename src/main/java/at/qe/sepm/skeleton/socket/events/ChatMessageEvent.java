package at.qe.sepm.skeleton.socket.events;

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
