package at.qe.sepm.skeleton.ui.beans;
import at.qe.sepm.skeleton.logic.GameMode;
import at.qe.sepm.skeleton.logic.IRoomAction;
import at.qe.sepm.skeleton.logic.QuizRoomManager;
import at.qe.sepm.skeleton.logic.RoomDifficulty;
import at.qe.sepm.skeleton.model.Manager;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.model.QuestionSetDifficulty;
import at.qe.sepm.skeleton.services.ManagerService;
import at.qe.sepm.skeleton.services.QuestionSetService;
import at.qe.sepm.skeleton.socket.QRWebSocketConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Bean to help to create a new user object
 *
 * @author Johannes Spies
 */
@Component
@Scope("request")
public class CreateRoomBean implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IRoomAction roomAction;

    @Autowired
    QuestionSetService questionSetService;

    @Autowired
    QuizRoomManager quizRoomManager;

    @Autowired
    MessageBean messageBean;

    private int selectedPlayerLimit;
    private RoomDifficulty selectedDifficulty;
    private QuestionSet selectedQuestionSet;
    private GameMode selectedGameMode;
    private List<QuestionSet> availableQuestionSets;
    private List<RoomDifficulty> availableRoomDifficulties;
    private List<GameMode> availableGameModes;


    @PostConstruct
    public void init() {
        availableQuestionSets = questionSetService.getAllQuestionSets();
        availableRoomDifficulties = Arrays.asList(RoomDifficulty.values());
        availableGameModes = Arrays.asList(GameMode.values());
    }

    public void create() {
        List<QuestionSet> questionSets = new ArrayList<>();
        questionSets.add(selectedQuestionSet);

        if (validate() == true) {
            quizRoomManager.createRoom(selectedPlayerLimit, selectedDifficulty, selectedGameMode, questionSets, roomAction);
        } else {
            messageBean.showError("message", "Sorry, could not create room!");
        }
    }

    public boolean validate() {
        boolean valid = true;

        if (selectedPlayerLimit < 1 || selectedPlayerLimit > 999) {
            valid = false;
        }

        /*
        if (selectedDifficulty != RoomDifficulty.easy || selectedDifficulty != RoomDifficulty.hard) {
            valid = false;
        }
        if (selectedGameMode != GameMode.normal || selectedGameMode != GameMode.reverse) {
            valid = false;
        }
        */
        if (selectedQuestionSet == null) {
            valid = false;
        }
        return valid;
    }
    public void redirectToGame(String url) {
        try {
            FacesContext.getCurrentInstance().
                    getExternalContext().redirect("/login.xhtml?registration=success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getSelectedPlayerLimit() {
        return selectedPlayerLimit;
    }

    public void setSelectedPlayerLimit(int selectedPlayerLimit) {
        this.selectedPlayerLimit = selectedPlayerLimit;
    }

    public RoomDifficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    public void setSelectedDifficulty(RoomDifficulty selectedDifficulty) {
        this.selectedDifficulty = selectedDifficulty;
    }

    public QuestionSet getSelectedQuestionSet() {
        return selectedQuestionSet;
    }

    public void setSelectedQuestionSet(QuestionSet selectedQuestionSet) {
        this.selectedQuestionSet = selectedQuestionSet;
    }

    public GameMode getSelectedGameMode() {
        return selectedGameMode;
    }

    public void setSelectedGameMode(GameMode selectedGameMode) {
        this.selectedGameMode = selectedGameMode;
    }

    public List<QuestionSet> getAvailableQuestionSets() {
        return availableQuestionSets;
    }

    public void setAvailableQuestionSets(List<QuestionSet> availableQuestionSets) {
        this.availableQuestionSets = availableQuestionSets;
    }

    public List<RoomDifficulty> getAvailableRoomDifficulties() {
        return availableRoomDifficulties;
    }

    public void setAvailableRoomDifficulties(List<RoomDifficulty> availableRoomDifficulties) {
        this.availableRoomDifficulties = availableRoomDifficulties;
    }

    public List<GameMode> getAvailableGameModes() {
        return availableGameModes;
    }

    public void setAvailableGameModes(List<GameMode> availableGameModes) {
        this.availableGameModes = availableGameModes;
    }
}
