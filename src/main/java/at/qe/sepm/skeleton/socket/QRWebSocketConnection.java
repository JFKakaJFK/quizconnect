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
import java.util.List;

@Controller
public class QRWebSocketConnection implements IRoomAction {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SimpMessagingTemplate messagingTemplate;

    private HashMap<Integer, IPlayerAction> rooms;
    private HashMap<Integer, HashMap<Integer, Player>> players;

    @Value("${qr.ws.server}")
    private String serverEndpoint;
    @Value("${storage.api.avatars}")
    private String avatars;

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
        ServerEvent event = new PlayerJoinEvent(p, avatars);
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

    @Override // TODO: move all server logic out of here, call remove method of this controller on gameend
    public void onGameEnd(int pin) {
        gameEnd(pin);
        ServerEvent event = new GenericServerEvent(GAME_END);
        broadcast(event, pin);
        log.debug("Game " + pin + ": game ended");
    }

    @Override
    public void onJokerUse(int pin, int remaining) {
        ServerEvent event = new JokerUseEvent(remaining);
        event.setEvent(JOKER_USE);
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

    @Override
    public void assignQuestion(int pin, ActiveQuestion q) {
        ServerEvent event = new AssignQuestionEvent(q);
        event.setEvent(ASSIGN_QUESTION);
        broadcast(event, pin);
        log.debug("Game " + pin + ": Question " + q.question.getId() + " assigned");
    }

    @Override
    public void removeQuestion(int pin, ActiveQuestion q) {
        ServerEvent event = new RemoveQuestionEvent(q);
        event.setEvent(REMOVE_QUESTION);
        broadcast(event, pin);
        log.debug("Game " + pin + ": Question " + q.question.getId() + " removed");
    }

    /* Client events */

    private final String ROOM_INFO = "getRoomInfo";
    private final String GAME_INFO = "getGameInfo";
    private final String ROOM_PLAYERS = "getRoomPlayers";
    private final String READY = "readyUp";
    private final String ANSWER_QUESTION = "answerQuestion";
    private final String USE_JOKER = "useJoker";
    private final String LEAVE_ROOM = "leaveRoom";
    private final String CANCEL_TIMEOUT = "cancelTimeout";
    private final String ALIVE_PING = "sendAlivePing";

    private final String SUCCESS = "success";
    private final String ERROR = "error";

    @Deprecated
    public ServerEvent handleGetGameInfo(int pin){
        IPlayerAction qr = rooms.get(pin);

        ServerEvent event = new GameInfoEvent(pin,
                qr.getRoomDifficulty().name(),
                qr.getRoomMode().name(),
                qr.getRoomQuestionSets(),
                qr.getRoomScore(),
                qr.getAlivePingTimeStep(),
                qr.getNumberOfJokers());
        event.setEvent(GAME_INFO);
        return event;
    }

    public ServerEvent handleGetRoomInfo(int pin){
        IPlayerAction qr = rooms.get(pin);

        List<Player> ready = qr.getRoomReadyPlayers();
        List<Player> all = qr.getRoomPlayers();
        List<PlayerJSON> players = new ArrayList<>();
        for (Player p: all) {
            PlayerJSON pj = new PlayerJSON(p, avatars);
            if(ready.contains(p)){
                pj.setReady(true);
            }
            players.add(pj);
        }

        ServerEvent event = new RoomInfoEvent(pin,
                qr.getRoomDifficulty().name(),
                qr.getRoomMode().name(),
                qr.getRoomQuestionSets(),
                qr.getRoomScore(),
                qr.getAlivePingTimeStep(),
                qr.getNumberOfJokers(),
                players);
        event.setEvent(ROOM_INFO);
        return event;
    }

    @Deprecated
    public ServerEvent handleGetRoomPlayers(int pin){
        IPlayerAction qr = rooms.get(pin);
        List<Player> ready = qr.getRoomReadyPlayers();
        List<Player> all = qr.getRoomPlayers();
        List<PlayerJSON> players = new ArrayList<>();
        for (Player p: all) {
            PlayerJSON pj = new PlayerJSON(p, avatars);
            if(ready.contains(p)){
                pj.setReady(true);
            }
            players.add(pj);
        }
        ServerEvent event = new RoomPlayersEvent(players);
        event.setEvent(ROOM_PLAYERS);
        return event;
    }

    public ServerEvent handleReadyUp(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericServerEvent(ERROR);
        }
        qr.readyUp(p);
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleAnswerQuestion(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericServerEvent(ERROR);
        }
        // Cant check validity of ints, since 0 per default
        qr.answerQuestion(p, event.getQuestionId(), event.getAnswerId());
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleUseJoker(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericServerEvent(ERROR);
        }
        qr.useJoker(p);
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleLeaveRoom(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericServerEvent(ERROR);
        }
        qr.leaveRoom(p);
        players.get(pin).remove(event.getPlayerId());
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleCancelTimeout(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericServerEvent(ERROR);
        }
        qr.cancelTimeout(p);
        return new GenericServerEvent(SUCCESS);
    }

    public ServerEvent handleSendAlivePing(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericServerEvent(ERROR);
        }
        qr.sendAlivePing(p);
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
     * Responds to user request, only sends to specific user
     *
     * @param request
     * @param user
     * @param pin
     */
    @MessageMapping("/events/{pin}")
    @SendTo("/server/events/{pin}") // works but broadcast
    private ServerEvent handleEvent(@Payload ClientEvent request, Principal user, @DestinationVariable int pin){

        if(!rooms.containsKey(pin) || rooms.get(pin) == null){
            log.warn("QuizRoom " + pin + " does not exist");
            return new GenericServerEvent(ERROR);
        }

        log.debug(" --- QuizRoom " + pin + " should not be null --- ");

        if(!request.getEvent().equals(ALIVE_PING)){ // TODO remove
            log.debug("Received event of type " + request.getEvent() + " from " + user.getName());
        }

        switch (request.getEvent()){
            case ANSWER_QUESTION:
                return handleAnswerQuestion(pin, request);
            case USE_JOKER:
                return handleUseJoker(pin, request);
            case LEAVE_ROOM:
                return handleLeaveRoom(pin, request);
            case CANCEL_TIMEOUT:
                return handleCancelTimeout(pin, request);
            case ALIVE_PING:
                return handleSendAlivePing(pin, request);
            case ROOM_INFO:
                return handleGetRoomInfo(pin);
            case READY:
                return handleReadyUp(pin, request);
            case GAME_INFO:
                return handleGetGameInfo(pin);
            case ROOM_PLAYERS:
                return handleGetRoomPlayers(pin);
            default:
                return new GenericServerEvent("error");
        }
    }

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

    public boolean isPlayerInGame(int pin, Player p){
        return players.get(pin) != null && players.get(pin).containsKey(p.getId());
    }

    private void gameEnd(int pin){
        if(rooms.containsKey(pin)){
            rooms.remove(pin);
        }
        if(players.containsKey(pin)){
            players.remove(pin);
        }
    }
}
