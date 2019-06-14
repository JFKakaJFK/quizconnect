package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.logic.IPlayerAction;
import at.qe.sepm.skeleton.logic.QuizRoomManager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.socket.QRWebSocketConnection;
import at.qe.sepm.skeleton.ui.beans.SessionInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;

/**
 * A controller for joining a QuizRoom
 */
@Controller
@RequestScope
@RequestMapping
public class JoinGameController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SessionInfoBean sessionInfoBean;
    private QuizRoomManager quizRoomManager;
    private QRWebSocketConnection qrWebSocketConnection;

    @Autowired
    public JoinGameController(SessionInfoBean sessionInfoBean,
                              QuizRoomManager quizRoomManager,
                              QRWebSocketConnection qrWebSocketConnection){
        assert sessionInfoBean != null;
        assert quizRoomManager != null;
        assert qrWebSocketConnection != null;
        this.sessionInfoBean = sessionInfoBean;
        this.quizRoomManager = quizRoomManager;
        this.qrWebSocketConnection = qrWebSocketConnection;
    }

    /**
     * Handles requests to the join endpoint and tries to join the QuizRoom if it exists.
     *
     * @param pin
     *          The pin of the QuizRoom.
     * @return
     *          Error message or Player id of the Player who joined the QuizRoom.
     */
    @RequestMapping(value = "/qr/join/{pin}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity joinRoom(@PathVariable String pin){
        if(!sessionInfoBean.isLoggedIn()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        if(!sessionInfoBean.hasRole("PLAYER")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        int PIN = Integer.valueOf(pin);
        Player p  = sessionInfoBean.getCurrentUser().getPlayer();
        if(quizRoomManager.doesRoomExist(PIN)){ // TODO since the pin is checked for each keystroke if two pins are nearly identical
            // (e.g. 10 & 100, then a player wanting to join room 100 inevitably tries to join room 10)
            if(qrWebSocketConnection.isPlayerInGame(PIN, p)){
                logger.debug("Player " + p.getUser().getUsername() + " rejoined room " + pin);
                return ResponseEntity.ok("{\"playerId\":" + p.getId() + ",\"highScore\":" + p.getHighScore() + "}");
            }
            IPlayerAction qr;
            try{
                qr = quizRoomManager.joinRoom(PIN, p);
            } catch (IllegalArgumentException e){
                return ResponseEntity.ok("{\"error\":\"room is full\"}");
            }
            qrWebSocketConnection.addGame(PIN, qr, p);
            logger.debug("Player " + p.getUser().getUsername() + " joined room " + pin);
            return ResponseEntity.ok("{\"playerId\":" + p.getId() + ",\"highScore\":" + p.getHighScore() + "}");
        }
        return ResponseEntity.ok("{\"error\":\"room does not exist\"}");
    }
}
