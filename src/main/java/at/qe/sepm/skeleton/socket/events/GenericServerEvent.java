package at.qe.sepm.skeleton.socket.events;

public class GenericServerEvent extends ServerEvent {
    public GenericServerEvent(){}

    public GenericServerEvent(String event){
        this.setEvent(event);
    }
}
