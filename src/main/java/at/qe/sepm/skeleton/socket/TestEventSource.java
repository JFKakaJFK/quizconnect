package at.qe.sepm.skeleton.socket;

import at.qe.sepm.skeleton.socket.events.GenericServerEvent;
import at.qe.sepm.skeleton.socket.events.ServerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@EnableScheduling
public class TestEventSource {

    @Autowired
    private QRWebSocketConnection qrWebSocketConnection;

    @Scheduled(fixedRate = 5000)
    public void createEvent(){
        qrWebSocketConnection.onGameEnd();
        // qrWebSocketConnection.onJokerUse();
    }
}
