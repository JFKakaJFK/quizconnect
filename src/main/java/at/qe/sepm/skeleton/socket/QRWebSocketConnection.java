package at.qe.sepm.skeleton.socket;

import at.qe.sepm.skeleton.logic.ActiveQuestion;
import at.qe.sepm.skeleton.logic.IRoomAction;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.socket.events.ServerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class QRWebSocketConnection implements IRoomAction {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${qr.ws.server}")
    private String serverEndpoint;

    private int PIN;
    private Player player;

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

    }

    @Override
    public void onJokerUse() {

    }

    @Override
    public void onPlayerLeave(Player p, String reason) {

    }

    @Override
    public void onScoreChange(int newScore) {

    }

    @Override
    public void onTimerSync(long currentTime) {

    }

    @Override
    public void onTimeoutStart(long timeoutTime) {

    }

    @Override
    public void onKick() {

    }

    @Override
    public void assignQuestion(ActiveQuestion q) {

    }

    @Override
    public void assignAnswer(ActiveQuestion q, int index) {

    }

    @Override
    public void removeQuestion(ActiveQuestion q) {

    }

    @Override
    public void removeAnswer(ActiveQuestion q) {

    }

    private void sendEvent(ServerEvent event){
        // TODO validate PIN
        this.messagingTemplate.convertAndSend(serverEndpoint + "/" + PIN, event);
    }

    public int getPIN() {
        return PIN;
    }

    public void setPIN(int PIN) {
        this.PIN = PIN;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
