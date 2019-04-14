package at.qe.sepm.skeleton.socket.events;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("generic")
public class GenericClientEvent extends ClientEvent {
    public GenericClientEvent(){}

    public GenericClientEvent(String event){
        this.setEvent(event);
    }
}
