package at.qe.sepm.skeleton.socket.events;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("generic")
public class GenericServerEvent extends ServerEvent {
    public GenericServerEvent(){}

    public GenericServerEvent(String event){
        this.setEvent(event);
    }
}
