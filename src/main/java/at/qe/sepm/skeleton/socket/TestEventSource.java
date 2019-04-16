package at.qe.sepm.skeleton.socket;

import at.qe.sepm.skeleton.logic.ActiveQuestion;
import at.qe.sepm.skeleton.logic.IRoomAction;
import at.qe.sepm.skeleton.model.*;
import at.qe.sepm.skeleton.socket.events.GenericServerEvent;
import at.qe.sepm.skeleton.socket.events.ServerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
@EnableScheduling
public class TestEventSource {

    @Autowired
    private IRoomAction qrWebSocketConnection;

    private int pin = 10;
    private int counter = 0;

    private Player getRandomPlayer(){
        Player p = new Player();
        int id = new Random().nextInt();
        p.setId(id);
        p.setUser(new User());
        p.getUser().setUsername("u" + id);
        p.setAvatarPath("/bad/request.png");
        return p;
    }

    private ActiveQuestion getRandomActiveQuestion(){
        Question q = new Question();
        int id = new Random().nextInt();
        q.setId(id);
        QuestionSet qs = new QuestionSet();
        qs.setName("qs" + id);
        q.setQuestionSet(qs);
        q.setQuestionString("q" + id);
        q.setRightAnswerString("r" + id);
        q.setWrongAnswerString_1("w1" + id);
        q.setWrongAnswerString_2("w2" + id);
        q.setWrongAnswerString_3("w3" + id);
        q.setWrongAnswerString_4("w4" + id);
        q.setWrongAnswerString_5("w5" + id);
        List<Player> players = new ArrayList<>();
        for(int i = 0; i <= 5; i++){
            players.add(getRandomPlayer());
        }
        return new ActiveQuestion(q, getRandomPlayer(), getRandomPlayer(), players, 20000);
    }

    @Scheduled(fixedRate = 5000)
    public void createEvent(){
        switch (counter){
            case 0:
                qrWebSocketConnection.onReadyUp(pin, getRandomPlayer(), 42);
                break;
            case 1:
                qrWebSocketConnection.onPlayerJoin(pin, getRandomPlayer());
                break;
            case 2:
                qrWebSocketConnection.onPlayerLeave(pin, getRandomPlayer(), "reason");
                break;
            case 3:
                qrWebSocketConnection.onGameStart(pin);
                break;
            case 4:
                qrWebSocketConnection.onGameEnd(pin);
                break;
            case 5:
                qrWebSocketConnection.onJokerUse(pin, 7);
                break;
            case 6:
                qrWebSocketConnection.onScoreChange(pin, new Random().nextInt());
                break;
            case 7:
                qrWebSocketConnection.onTimerSync(pin, getRandomPlayer(), getRandomActiveQuestion(), 3242);
                break;
            case 8:
                qrWebSocketConnection.onTimeoutStart(pin, getRandomPlayer(), 23402);
                break;
            case 9:
                qrWebSocketConnection.onKick(pin, getRandomPlayer());
                break;
            case 10:
                qrWebSocketConnection.assignQuestion(pin, getRandomActiveQuestion());
                break;
            default:
                qrWebSocketConnection.removeQuestion(pin, getRandomActiveQuestion());
        }
        counter++;
        counter %= 12;
    }
}
