package at.qe.sepm.skeleton.ui.controllers;

import at.qe.sepm.skeleton.logic.IPlayerAction;
import at.qe.sepm.skeleton.logic.QuizRoomManager;
import at.qe.sepm.skeleton.model.Player;
import at.qe.sepm.skeleton.socket.QRWebSocketConnection;
import at.qe.sepm.skeleton.ui.beans.SessionInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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


    @RequestMapping(value = "/qr/join/{pin}", method = RequestMethod.POST)
    @ResponseBody
    public String joinRoom(@PathVariable String pin){
        if(!sessionInfoBean.isLoggedIn() || !sessionInfoBean.hasRole("PLAYER")){
            return null;
        }
        int PIN = Integer.valueOf(pin);
        Player p  = sessionInfoBean.getCurrentUser().getPlayer();
        if(quizRoomManager.doesRoomExist(PIN)){
            IPlayerAction qr = quizRoomManager.joinRoom(PIN, p);
            qrWebSocketConnection.addGame(PIN, qr, p);
            logger.info("Player " + p.getUser().getUsername() + " joined room " + pin);
            return "{\"playerId\":" + p.getId() + "}";
        }
        return "{\"error\":\"Room [" + pin + "] doesn't exist\"}";
    }
}
