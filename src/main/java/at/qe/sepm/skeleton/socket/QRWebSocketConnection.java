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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Class implementing the interface between the frontend and the {@link QuizRoom}.
 *
 * In order to be the interface for {@link IRoomAction} objects, the socket connection
 * needs to support all server events and for the frontend all client events ({@link IPlayerAction}) need to be supported.
 * In addition chat messages for the {@link QuizRoom}s are supported as well.
 */
@Controller
public class QRWebSocketConnection implements IRoomAction {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private SimpMessagingTemplate messagingTemplate;

    private HashMap<Integer, IPlayerAction> rooms; // all active QuizRooms
    private HashMap<Integer, HashMap<Integer, Player>> players; // all active Players, by QuizRoom

    private HashMap<Integer, List<ChatMessageJSON>> chatMessages; // the chat history of each QuizRoom

    @Value("${qr.ws.server}")
    private String serverEndpoint;
    @Value("${storage.api.avatars}")
    private String avatars;

    /**
     * Initializes the WebSocket connection for all {@link QuizRoom}s.
     *
     * @param messagingTemplate Socket Message Boilerplate (is autowired).
     */
    @Autowired
    public QRWebSocketConnection(SimpMessagingTemplate messagingTemplate){
        assert messagingTemplate != null;
        this.messagingTemplate = messagingTemplate;
        this.rooms = new HashMap<>();
        this.players = new HashMap<>();
        this.chatMessages = new HashMap<>();
        log.debug("QRWSController started");
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

    /**
     * Implementation of the {@link IRoomAction} method, broadcasts new ready player to all players in room.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param p
     *            The Player who declared themselves ready.
     * @param totalReady
     */
    @Override
    public void onReadyUp(int pin, Player p, int totalReady) {
        SocketEvent event = new ReadyUpEvent(p, totalReady);
        event.setEvent(READY_UP);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " is ready");
    }

    /**
     * Implementation of the {@link IRoomAction} method, broadcasts new player to all players in room.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param p
     */
    @Override
    public void onPlayerJoin(int pin, Player p) {
        SocketEvent event = new PlayerJoinEvent(p, avatars);
        event.setEvent(PLAYER_JOIN);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " joined");
    }

    /**
     * Implementation of the {@link IRoomAction} method, broadcasts game start to all players in room.
     *
     * @param pin
     */
    @Override
    public void onGameStart(int pin) {
        SocketEvent event = new GenericSocketEvent(GAME_START);
        broadcast(event, pin);
        log.debug("Game " + pin + ": game started");
    }

    /**
     * Implementation of the {@link IRoomAction} method, broadcasts game end to all players in room.
     *
     * @param pin
     */
    @Override
    public void onGameEnd(int pin) {
        gameEnd(pin);
        SocketEvent event = new GenericSocketEvent(GAME_END);
        broadcast(event, pin);
        log.debug("Game " + pin + ": game ended");
    }

    /**
     * Implementation of the {@link IRoomAction} method, broadcasts the remaining jokers to all players in room.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param remaining
     */
    @Override
    public void onJokerUse(int pin, int remaining) {
        SocketEvent event = new JokerUseEvent(remaining);
        event.setEvent(JOKER_USE);
        broadcast(event, pin);
        log.debug("Game " + pin + ": joker was used");
    }

    /**
     * Implementation of the {@link IRoomAction} method, broadcasts which player left to all players in room.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param p
     *            The Player who left.
     * @param reason
     */
    @Override
    public void onPlayerLeave(int pin, Player p, String reason) {
        SocketEvent event = new PlayerLeaveEvent(p, reason);
        event.setEvent(PLAYER_LEAVE);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " left");
    }

    /**
     * Implementation of the {@link IRoomAction} method, broadcasts new score to all players in room.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param newScore
     */
    @Override
    public void onScoreChange(int pin, int newScore) {
        SocketEvent event = new ScoreChangeEvent(newScore);
        event.setEvent(SCORE_CHANGE);
        broadcast(event, pin);
        log.debug("Game " + pin + ": score changed to " + newScore);
    }

    /**
     * Implementation of the {@link IRoomAction} method, informs player of imminent kick.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param player
     * @param timeoutTime
     */
    @Override
    public void onTimeoutStart(int pin, Player player, long timeoutTime) {
        SocketEvent event = new PlayerTimeoutEvent(player, timeoutTime);
        event.setEvent(TIMEOUT_START);
        broadcast(event, pin);
        log.debug("Game " + pin + ": time remaining until player " + player.getId() + " is kicked: " + timeoutTime + "ms");
    }

    /**
     * Implementation of the {@link IRoomAction} method, timer synchronization event.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param p
     *            Player the timer synchronized.
     * @param q
     *            Question of the Player to be synchronized.
     * @param remaining
     */
    @Override
    public void onTimerSync(int pin, Player p, ActiveQuestion q, long remaining) {
        SocketEvent event = new TimerSyncEvent(q, remaining);
        event.setEvent(TIMER_SYNC);
        broadcast(event, pin);
        log.debug("Game " + pin + ": time remaining of question " + q.question.getId() + " is " + remaining + "ms");
    }

    /**
     * Implementation of the {@link IRoomAction} method, kicks a specific player.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param p
     */
    @Override
    public void onKick(int pin, Player p) {
        SocketEvent event = new PlayerKickEvent(p);
        event.setEvent(KICK);
        broadcast(event, pin);
        log.debug("Game " + pin + ": player " + p.getId() + " was kicked");
    }

    /**
     * Implementation of the {@link IRoomAction} method, assigns new
     * {@link at.qe.sepm.skeleton.model.Question} and answers.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param q
     */
    @Override
    public void assignQuestion(int pin, ActiveQuestion q) {
        SocketEvent event = new AssignQuestionEvent(q);
        event.setEvent(ASSIGN_QUESTION);
        broadcast(event, pin);
        log.debug("Game " + pin + ": Question " + q.question.getId() + " assigned");
    }

    /**
     * Implementation of the {@link IRoomAction} method, removes a specific
     * {@link at.qe.sepm.skeleton.model.Question} from the game.
     *
     * @param pin
     *            Pin of the QuizRoom making the call.
     * @param q
     */
    @Override
    public void removeQuestion(int pin, ActiveQuestion q) {
        SocketEvent event = new RemoveQuestionEvent(q);
        event.setEvent(REMOVE_QUESTION);
        broadcast(event, pin);
        log.debug("Game " + pin + ": Question " + q.question.getId() + " removed");
    }

    /* Client events */

    private final String ROOM_INFO = "getRoomInfo";
    private final String READY = "readyUp";
    private final String ANSWER_QUESTION = "answerQuestion";
    private final String USE_JOKER = "useJoker";
    private final String LEAVE_ROOM = "leaveRoom";
    private final String CANCEL_TIMEOUT = "cancelTimeout";
    private final String ALIVE_PING = "sendAlivePing";

    private final String CHAT_MESSAGE = "chatMessage";
    private final String CHAT_MESSAGES = "getChatMessages";

    private final String SUCCESS = "success";
    private final String ERROR = "error";

    /**
     * Wrapper for the {@link IPlayerAction} event fulfilled by the corresponding {@link IRoomAction} object.
     *
     * Depending on the Game state the players in the lobby are omitted from the response.
     *
     * @param pin
     * @return
     */
    private SocketEvent handleGetRoomInfo(int pin){
        IPlayerAction qr = rooms.get(pin);
        if(qr == null){
            return new GenericSocketEvent(ERROR);
        }
        boolean inLobby = qr.isRoomInWaitingMode();
        List<PlayerJSON> players = new ArrayList<>();
        if(inLobby){
            List<Player> ready = qr.getRoomReadyPlayers();
            List<Player> all = qr.getRoomPlayers();
            for (Player p: all) {
                PlayerJSON pj = new PlayerJSON(p, avatars);
                if(ready.contains(p)){
                    pj.setReady(true);
                }
                players.add(pj);
            }
        }

        SocketEvent event = new RoomInfoEvent(pin,
                qr.getRoomDifficulty().name(),
                qr.getRoomMode().name(),
                qr.getRoomQuestionSets(),
                qr.getRoomScore(),
                qr.getAlivePingTimeStep(),
                qr.getNumberOfJokers(),
                inLobby,
                players);
        event.setEvent(ROOM_INFO);
        return event;
    }

    /**
     * Wrapper for the {@link IPlayerAction} event fulfilled by the corresponding {@link IRoomAction} object.
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleReadyUp(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericSocketEvent(ERROR);
        }
        qr.readyUp(p);
        return new GenericSocketEvent(SUCCESS);
    }

    /**
     * Wrapper for the {@link IPlayerAction} event fulfilled by the corresponding {@link IRoomAction} object.
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleAnswerQuestion(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericSocketEvent(ERROR);
        }
        // Cant check validity of ints, since 0 per default
        qr.answerQuestion(p, event.getQuestionId(), event.getAnswerId());
        return new GenericSocketEvent(SUCCESS);
    }

    /**
     * Wrapper for the {@link IPlayerAction} event fulfilled by the corresponding {@link IRoomAction} object.
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleUseJoker(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericSocketEvent(ERROR);
        }
        qr.useJoker(p);
        return new GenericSocketEvent(SUCCESS);
    }

    /**
     * Wrapper for the {@link IPlayerAction} event fulfilled by the corresponding {@link IRoomAction} object.
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleLeaveRoom(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericSocketEvent(ERROR);
        }
        qr.leaveRoom(p);
        players.get(pin).remove(event.getPlayerId());
        return new GenericSocketEvent(SUCCESS);
    }

    /**
     * Wrapper for the {@link IPlayerAction} event fulfilled by the corresponding {@link IRoomAction} object.
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleCancelTimeout(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericSocketEvent(ERROR);
        }
        qr.cancelTimeout(p);
        return new GenericSocketEvent(SUCCESS);
    }

    /**
     * Wrapper for the {@link IPlayerAction} event fulfilled by the corresponding {@link IRoomAction} object.
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleSendAlivePing(int pin, ClientEvent event){
        IPlayerAction qr = rooms.get(pin);
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericSocketEvent(ERROR);
        }
        qr.sendAlivePing(p);
        return new GenericSocketEvent(SUCCESS);
    }

    /**
     * Handles new {@link ChatMessageEvent}s, stores the event in the chat history and broadcasts the
     * message to all {@link Player}s in the room.
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleChatMessage(int pin, ClientEvent event){
        if(event.getMessage() == null){
            return new GenericSocketEvent(ERROR);
        }
        ChatMessageEvent message;
        if(players.get(pin) == null){
            return new GenericSocketEvent(ERROR);
        }
        Player p = players.get(pin).get(event.getPlayerId());
        if(p == null){
            return new GenericSocketEvent(ERROR);
        }
        message = new ChatMessageEvent(event.getMessage(), p.getUser().getUsername(), p.getId(), chatMessages.get(pin).size());
        chatMessages.get(pin).add(message.getMessage());
        return message;
    }

    /**
     * Handles requests for the chat history, responds with all sent messages in {@link QuizRoom}
     *
     * @param pin
     * @param event
     * @return
     */
    private SocketEvent handleGetChatMessages(int pin, ClientEvent event){
        return new ChatHistoryEvent(chatMessages.get(pin));
    }

    /**
     * Broadcast event to all {@link Player}s in a {@link QuizRoom}
     *
     * @param event
     * @param pin
     */
    private void broadcast(SocketEvent event, int pin){
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
    @SendTo("/server/events/{pin}")
    private SocketEvent handleEvent(@Payload ClientEvent request, Principal user, @DestinationVariable int pin){

        if(!rooms.containsKey(pin) || rooms.get(pin) == null){
            log.warn("Game " + pin + ": QuizRoom " + pin + " does not exist");
            return new GenericSocketEvent(ERROR);
        }

        if(!request.getEvent().equals(ALIVE_PING)){
            log.debug("Game " + pin + ": received event of type " + request.getEvent() + " from " + user.getName());
        }

        try {
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
                case CHAT_MESSAGE:
                    return handleChatMessage(pin, request);
                case CHAT_MESSAGES:
                    return handleGetChatMessages(pin, request);
                default:
                    return new GenericSocketEvent("error");
            }
        } catch (NullPointerException e){
            log.warn("Game " + pin + " failed to close correctly");
            return new GenericSocketEvent(ERROR);
        }
    }

    /**
     * Adds a {@link QuizRoom} to the active games.
     *
     * @param pin
     * @param qr
     * @param p
     */
    public void addGame(int pin, IPlayerAction qr, Player p){
        if(!rooms.containsKey(pin)){
            rooms.put(pin, qr);
            players.put(pin, new HashMap<>());
        }
        players.computeIfAbsent(pin, k -> new HashMap<>());
        chatMessages.computeIfAbsent(pin, k -> new LinkedList<>());
        players.get(pin).put(p.getId(), p);
    }

    /**
     * Returns true if the {@link Player} is in the {@link QuizRoom} with the specified pin.
     *
     * @param pin
     * @param p
     * @return
     */
    public boolean isPlayerInGame(int pin, Player p){
        return players.get(pin) != null && players.get(pin).containsKey(p.getId());
    }

    /**
     * Removes a game, its players and chat history from the active connections.
     *
     * @param pin
     */
    private void gameEnd(int pin){
        rooms.remove(pin);
        players.remove(pin);
        chatMessages.remove(pin);
    }
}
