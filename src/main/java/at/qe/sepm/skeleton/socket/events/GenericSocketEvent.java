package at.qe.sepm.skeleton.socket.events;

public class GenericSocketEvent extends SocketEvent {
    public GenericSocketEvent(){}

    public GenericSocketEvent(String event){
        this.setEvent(event);
    }
}
