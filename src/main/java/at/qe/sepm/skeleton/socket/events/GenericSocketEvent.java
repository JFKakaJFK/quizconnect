package at.qe.sepm.skeleton.socket.events;

/**
 * A generic outgoing event.
 */
public class GenericSocketEvent extends SocketEvent {
    public GenericSocketEvent(){}

    public GenericSocketEvent(String event){
        this.setEvent(event);
    }
}
