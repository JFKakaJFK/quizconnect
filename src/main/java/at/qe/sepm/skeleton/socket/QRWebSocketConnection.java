package at.qe.sepm.skeleton.socket;

import at.qe.sepm.skeleton.logic.ActiveQuestion;
import at.qe.sepm.skeleton.logic.IPlayerAction;
import at.qe.sepm.skeleton.logic.IRoomAction;
import at.qe.sepm.skeleton.logic.QuizRoom;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.socket.events.ClientEvent;
import at.qe.sepm.skeleton.socket.events.GClientEvent;
import at.qe.sepm.skeleton.socket.events.GenericServerEvent;
import at.qe.sepm.skeleton.socket.events.ServerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class QRWebSocketConnection implements IRoomAction {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SimpMessagingTemplate messagingTemplate;

    @Value("${qr.ws.server}")
    private String serverEndpoint;

    @Autowired
    public QRWebSocketConnection(SimpMessagingTemplate messagingTemplate){
        assert messagingTemplate != null;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onReadyUp(Player p, int totalReady) {

    }

    @Override
    public void onPlayerJoin(Player p) {

    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void onGameEnd() {
        // TODO
        ServerEvent event = new GenericServerEvent("onKick");

        sendToPlayer(null, event, 10);
        // broadcast(event, 10); // works
    }

    @Override
    public void onJokerUse() {
        // TODO
        ServerEvent event = new GenericServerEvent("onJokerUse");
        broadcast(event, 10);
    }

    @Override
    public void onPlayerLeave(Player p, String reason) {

    }

    @Override
    public void onScoreChange(int newScore) {

    }

    @Override
    public void onTimeoutStart(long timeoutTime) {

    }

    @Override
    public void onTimerSync(Player p, ActiveQuestion q, long currentTime) {

    }

    @Override
    public void onKick(Player p) {

    }

    @Override
    public void assignQuestion(Player p, ActiveQuestion q) {

    }

    @Override
    public void assignAnswer(Player p, ActiveQuestion q, int index) {

    }

    @Override
    public void removeQuestion(Player p, ActiveQuestion q) {

    }

    @Override
    public void removeAnswer(Player p, ActiveQuestion q) {

    }

    /**
     * Broadcast event to all {@link Player}s in a {@link QuizRoom}
     *
     * @param event
     * @param pin
     */
    private void broadcast(ServerEvent event, int pin){
        this.messagingTemplate.convertAndSend(serverEndpoint + "/" + pin, event);
    }

    /**
     * Send to single {@link Player} in a {@link QuizRoom}
     *
     * @param p
     * @param event
     * @param pin
     */
    private void sendToPlayer(Player p, ServerEvent event, int pin){
        log.info("Sending '" + event.getEvent() + "' event"); // to " + p.getUser().getUsername());
        //this.messagingTemplate.convertAndSendToUser(p.getUser().getUsername(), serverEndpoint + "/" + pin, event);
        this.messagingTemplate.convertAndSendToUser("user3", serverEndpoint + "/" + pin, event);
    }

    /**
     * Responds to user request, only sends to specific user
     *
     * @param request
     * @param user
     * @param pin
     */

    /*
    @MessageMapping("/events/{pin}")
    @SendTo("/server/events/{pin}") // works but broadcast
    // @SendToUser(destinations = "/server/events/{pin}", broadcast = false)
    private ServerEvent listen(@Payload GClientEvent request, Principal user, @DestinationVariable int pin){
        System.out.println("Listen");
        log.info("Received event of type " + request.getEvent() + " from " + user.getName());
        // TODO: get request event type, get response
        ServerEvent response =  new GenericServerEvent("Hello");
        return response;
        //this.messagingTemplate.convertAndSendToUser(user.getName(), serverEndpoint + "/" + pin, response );

    }
    */



    @MessageMapping("/events/{pin}")
    //@SendTo("/server/events/{pin}")
    //@SendToUser(destinations = "/server/events/{pin}", broadcast = false)
    private void listen2(@Payload GClientEvent request, Principal user, @DestinationVariable int pin){
        System.out.println("Listen2");
        log.info("Received event of type " + request.getEvent() + " from " + user.getName());
        // TODO: get request event type, get response
        ServerEvent response =  new GenericServerEvent("Hello");
        //return response;
        //this.messagingTemplate.convertAndSendToUser(user.getName(), serverEndpoint + "/" + pin, response );
        this.messagingTemplate.convertAndSendToUser(user.getName(), "events/" + pin, response );

    }
}
