package at.qe.sepm.skeleton.socket.events;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryEvent extends SocketEvent {
    private List<ChatMessageJSON> messages;

    public ChatHistoryEvent(){}

    public ChatHistoryEvent(List<ChatMessageJSON> messages){
        this.setEvent("getChatMessages");
        this.messages = messages == null ? new ArrayList<>(0) : messages;
    }

    public List<ChatMessageJSON> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageJSON> messages) {
        this.messages = messages;
    }
}
