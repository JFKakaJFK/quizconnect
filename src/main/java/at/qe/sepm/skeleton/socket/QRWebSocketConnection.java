package at.qe.sepm.skeleton.socket;

import at.qe.sepm.skeleton.logic.ActiveQuestion;
import at.qe.sepm.skeleton.logic.IPlayerAction;
import at.qe.sepm.skeleton.logic.IRoomAction;
import at.qe.sepm.skeleton.logic.QuizRoom;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.socket.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.ApplicationScope;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
// @ApplicationScope
// @Scope("application")
public class QRWebSocketConnection implements IRoomAction {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SimpMessagingTemplate messagingTemplate;

    private HashMap<Integer, IPlayerAction> rooms;
    private HashMap<Integer, HashMap<Integer, Player>> players;

    @Value("${qr.ws.server}")
    private String serverEndpoint;

    @Autowired
    public QRWebSocketConnection(SimpMessagingTemplate messagingTemplate){
        assert messagingTemplate != null;
        this.messagingTemplate = messagingTemplate;
        this.rooms = new HashMap<>();
        this.players = new HashMap<>();
        log.debug("WQController started");
    }

    /* Server events */

    private final String READY_UP = "onReadyUp";
    private final String PLAYER_JOIN = "onPlayerJoin";
    private final String PLAYER_LEAVE = "onPlayerLeave";
    private final String GAME_START = "onGameStart";
    private final String GAME_END = "onGameEnd";
    private final String JOKER_USE = "onJokerUse";
    private final String SCORE_CHANGE = "onScoreChange";
    private final String TIMER_SYNC = "onTimerSync";
    private final String TIMEOUT_START = "onTimeoutStart";
    private final String KICK = "onKick";
    private final String ASSIGN_QUESTION = "assignQuestion";
    private final String REMOVE_QUESTION = "removeQuestion";

    @Override
    public void onReadyUp(int pin, Player p, int totalReady) {
        ServerEvent event = new ReadyUpEvent(p, totalReady);
        event.setEvent(READY_UP);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " is ready");
    }

    @Override
    public void onPlayerJoin(int pin, Player p) {
        ServerEvent event = new PlayerJoinEvent(p);
        event.setEvent(PLAYER_JOIN);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " joined");
    }

    @Override
    public void onGameStart(int pin) {
        ServerEvent event = new GenericServerEvent(GAME_START);
        broadcast(event, pin);
        log.debug("Game " + pin + ": game started");
    }

    @Override // TODO: move all server logic out of here, call remove mehtod of this controller on gameend
    public void onGameEnd(int pin) {
        ServerEvent event = new GenericServerEvent(GAME_END);
        broadcast(event, pin);
        gameEnd(pin);
        log.debug("Game " + pin + ": game ended");
    }

    @Override // TODO update num jokers
    public void onJokerUse(int pin) {
        ServerEvent event = new GenericServerEvent(JOKER_USE);
        broadcast(event, pin);
        log.debug("Game " + pin + ": joker was used");
    }

    @Override
    public void onPlayerLeave(int pin, Player p, String reason) {
        ServerEvent event = new PlayerLeaveEvent(p, reason);
        event.setEvent(PLAYER_LEAVE);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " left");
    }

    @Override
    public void onScoreChange(int pin, int newScore) {
        ServerEvent event = new ScoreChangeEvent(newScore);
        event.setEvent(SCORE_CHANGE);
        broadcast(event, pin);
        log.debug("Game " + pin + ": score changed to " + newScore);
    }

    @Override
    public void onTimeoutStart(int pin, Player player, long timeoutTime) {
        ServerEvent event = new PlayerTimeoutEvent(player, timeoutTime);
        event.setEvent(TIMEOUT_START);
        broadcast(event, pin);
        log.debug("Game " + pin + ": time remaining until player " + player.getId() + " is kicked: " + timeoutTime + "ms");
    }

    @Override // TODO maybe not BROADCAST
    public void onTimerSync(int pin, Player p, ActiveQuestion q, long remaining) {
        ServerEvent event = new TimerSyncEvent(q, remaining);
        event.setEvent(TIMER_SYNC);
        broadcast(event, pin);
        log.debug("Game " + pin + ": time remaining of question " + q.question.getId() + " is " + remaining + "ms");
    }

    @Override // TODO maybe not BROADCAST
    public void onKick(int pin, Player p) {
        ServerEvent event = new PlayerKickEvent(p);
        event.setEvent(KICK);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " was kicked");
    }

    @Override // TODO init list fully
    public void assignQuestion(int pin, Player p, ActiveQuestion q) {
        ServerEvent event = new AssignQuestionEvent(q);
        event.setEvent(ASSIGN_QUESTION);
        broadcast(event, pin);
        log.debug("Game " + pin + ": Question " + q.question.getId() + " assigned");
    }

    @Override
    public void assignAnswer(int pin, Player p, ActiveQuestion q, int index) {

    }

    @Override
    public void removeQuestion(int pin, Player p, ActiveQuestion q) {
        ServerEvent event = new RemoveQuestionEvent(q);
        event.setEvent(REMOVE_QUESTION);
        broadcast(event, pin);
        log.debug("Game " + pin + ": Question " + q.question.getId() + " removed");
    }

    @Override
    public void removeAnswer(int pin, Player p, ActiveQuestion q) {

    }

    /* Client events */

    private final String GAME_INFO = "getGameInfo";
    private final String ROOM_PLAYERS = "getRoomPlayers";
    private final String READY = "readyUp";
    private final String ANSWER_QUESTION = "answerQuestion";
    private final String USE_JOKER = "useJoker";
    private final String LEAVE_ROOM = "leaveRoom";
    private final String CANCEL_TIMEOUT = "cancelTimeout";
    private final String ALIVE_PING = "sendAlivePing";

    private final String SUCCESS = "success";

    public ServerEvent handleGetGameInfo(int pin){
        // TODO
        return new GenericServerEvent(GAME_INFO);
    }

    public ServerEvent handleGetRoomPlayers(int pin){
        // TODO
        return new GenericServerEvent(ROOM_PLAYERS);
    }

    public ServerEvent handleReadyUp(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        qr.readyUp(players.get(pin).get(event.getPlayerId()));
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleAnswerQuestion(int pin, ClientEvent event){
        // TODO
        IPlayerAction qr = rooms.get(pin);
        return new GenericServerEvent(ANSWER_QUESTION);
    }

    public ServerEvent handleUseJoker(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        qr.useJoker(players.get(pin).get(event.getPlayerId()));
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleLeaveRoom(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        qr.leaveRoom(players.get(pin).get(event.getPlayerId()));
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleCancelTimeout(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        qr.cancelTimeout(players.get(pin).get(event.getPlayerId()));
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleSendAlivePing(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        qr.sendAlivePing(players.get(pin).get(event.getPlayerId()));
        return new GenericServerEvent(SUCCESS);
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
    // TODO where is the endpoint?
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
    @MessageMapping("/events/{pin}")
    @SendTo("/server/events/{pin}") // works but broadcast
    // @SendToUser(destinations = "/server/events/{pin}", broadcast = false)
    private ServerEvent handleEvent(@Payload ClientEvent request, Principal user, @DestinationVariable int pin){

        log.debug("Received event of type " + request.getEvent() + " from " + user.getName());

        switch (request.getEvent()){
            case GAME_INFO:
                return handleGetGameInfo(pin);
            case ROOM_PLAYERS:
                return handleGetRoomPlayers(pin);
            case READY:
                return handleReadyUp(pin, request);
            case ANSWER_QUESTION:
                return handleAnswerQuestion(pin, request);
            case USE_JOKER:
                return handleUseJoker(pin, request);
            case LEAVE_ROOM:
                return handleCancelTimeout(pin, request);
            case CANCEL_TIMEOUT:
                return handleCancelTimeout(pin, request);
            case ALIVE_PING:
                return handleSendAlivePing(pin, request);
            default:
                return new GenericServerEvent("error");
        }
    }

    @SubscribeMapping("/events/{pin}")
    public ServerEvent connect(@DestinationVariable int pin){
        return handleGetGameInfo(pin);
    }



    /* // TODO where is the endpoint?
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
    */

    public void addGame(int pin, IPlayerAction qr, Player p){
        if(!rooms.containsKey(pin)){
            rooms.put(pin, qr);
            players.put(pin, new HashMap<>());
        }
        if(players.get(pin) == null){
           players.put(pin, new HashMap<>());
        }
        players.get(pin).put(p.getId(), p);
    }

    public void gameEnd(int pin){
        if(rooms.containsKey(pin)){
            rooms.remove(pin);
        }
        if(players.containsKey(pin)){
            players.remove(pin);
        }
    }
}
