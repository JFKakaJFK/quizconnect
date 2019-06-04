package at.qe.sepm.skeleton.ui.beans;

import at.qe.sepm.skeleton.logic.GameMode;
import at.qe.sepm.skeleton.logic.IRoomAction;
import at.qe.sepm.skeleton.logic.QuizRoomManager;
import at.qe.sepm.skeleton.logic.RoomDifficulty;
import at.qe.sepm.skeleton.model.QuestionSet;
import at.qe.sepm.skeleton.services.QuestionSetService;
import at.qe.sepm.skeleton.utils.ScrollPaginator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for the creation of {@link at.qe.sepm.skeleton.logic.QuizRoom}s.
 * The input of the corresponding view is validated and on successful creation of a new {@link at.qe.sepm.skeleton.logic.QuizRoom},
 * the creating {@link at.qe.sepm.skeleton.model.Player} is redirected to the game view.
 *
 * @author Johannes Spies
 */
@Controller
@Scope("view")
public class CreateRoomBean implements Serializable {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final int MAX_SETS = 10;

    private IRoomAction roomAction;
    private QuizRoomManager quizRoomManager;

    private int playerLimit;
    private String searchPhrase = "";
    private RoomDifficulty difficulty;
    private GameMode mode;
    private List<QuestionSet> selectedQuestionSets;
    private List<QuestionSet> selectableQuestionSets;
    private ScrollPaginator<QuestionSet> selectableSetPaginator;
    private List<RoomDifficulty> allDifficulties;
    private List<GameMode> allModes;

    @Autowired
    public CreateRoomBean(IRoomAction roomAction,
                          QuestionSetService questionSetService,
                          QuizRoomManager quizRoomManager){
        assert roomAction != null;
        assert questionSetService != null;
        assert quizRoomManager != null;

        this.roomAction = roomAction;
        this.quizRoomManager = quizRoomManager;

        selectableQuestionSets = questionSetService.getAllQuestionSets();
        if(selectableQuestionSets == null){
            selectableQuestionSets = new ArrayList<>();
        }
        allDifficulties = Arrays.asList(RoomDifficulty.values());
        allModes = Arrays.asList(GameMode.values());
        selectedQuestionSets = new ArrayList<>(10);
        selectableSetPaginator = new ScrollPaginator<>( 30);
        filterSelectableQuestionSets();
    }

    /**
     * Updates the currently shown players by filtering accoriding to user input
     *
     * @param event
     */
    public void handleSearch(AjaxBehaviorEvent event){
        filterSelectableQuestionSets();
    }

    /**
     * Filters {@link CreateRoomBean#selectableQuestionSets} by the {@link CreateRoomBean#searchPhrase} and
     * updates {@link CreateRoomBean#selectableSetPaginator}.
     */
    private void filterSelectableQuestionSets(){
        List<QuestionSet> copy = new ArrayList<>(selectableQuestionSets);
        if(searchPhrase == null || searchPhrase.equals("")){
            selectableSetPaginator.updateList(copy);
        } else {
            selectableSetPaginator.updateList(copy.stream().parallel()
                    .filter(qs -> qs.getName().toLowerCase().contains(searchPhrase.toLowerCase())
                            || qs.getDescription().toLowerCase().contains(searchPhrase.toLowerCase()))
                    .collect(Collectors.toList()));
        }
    }

    /**
     * Validates the input for creating a new {@link at.qe.sepm.skeleton.logic.QuizRoom}.
     *
     * @return true if the input for room creation is valid
     */
    public boolean inputIsValid(){
        if(roomAction == null){
            return false;
        }
        if(playerLimit < 3 || playerLimit > 999){ // validate Player limit
            return false;
        }
        if(difficulty == null || mode == null){
            return false;
        }
        if(mode == GameMode.mathgod) return true; // TODO frontend hint that other selected sets not loaded...
        return !(selectedQuestionSets == null || selectedQuestionSets.size() < 1 || selectedQuestionSets.size() > MAX_SETS); // check if at least one set is selected
    }

    /**
     * Creates a new {@link at.qe.sepm.skeleton.logic.QuizRoom} if the input is valid and
     * redirects to the game view.
     */
    public void createRoomAndRedirect(){
        if(inputIsValid()){
            try {
                int pin = quizRoomManager.createRoom(playerLimit, difficulty, mode, selectedQuestionSets, roomAction);
                FacesContext.getCurrentInstance().getExternalContext().redirect("/quizroom/index.html?pin=" + pin);
            } catch (IllegalArgumentException e){
                log.error("Failed to create QuizRoom: " + e.getMessage());
            } catch (IOException e){
                log.error("Failed to redirect to game view");
            }
        }
    }

    /**
     * Returns wheter further {@link QuestionSet}s are selectable, prevents the user from adding to many
     *
     * @return true if more sets can be added
     */
    public boolean selectable(){
        return selectedQuestionSets.size() < MAX_SETS;
    }

    /**
     * Moves a {@link QuestionSet} from the {@link CreateRoomBean#selectableQuestionSets} to the {@link CreateRoomBean#selectedQuestionSets}.
     *
     * @param questionSet
     */
    public void selectSet(QuestionSet questionSet){
        if(!selectableQuestionSets.contains(questionSet)){
            return;
        }
        selectableQuestionSets.remove(questionSet);
        if(!selectedQuestionSets.contains(questionSet)){
            selectedQuestionSets.add(questionSet);
        }
        filterSelectableQuestionSets();
    }

    /**
     * Moves a {@link QuestionSet} from the {@link CreateRoomBean#selectedQuestionSets} to the {@link CreateRoomBean#selectableQuestionSets}.
     *
     * @param questionSet
     */
    public void unselectSet(QuestionSet questionSet){
        if(!selectedQuestionSets.contains(questionSet)){
            return;
        }
        selectedQuestionSets.remove(questionSet);
        if(!selectableQuestionSets.contains(questionSet)){
            selectableQuestionSets.add(questionSet);
        }
        filterSelectableQuestionSets();
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit = playerLimit;
    }

    public RoomDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(RoomDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public GameMode getMode() {
        return mode;
    }

    public void setMode(GameMode mode) {
        this.mode = mode;
    }

    public List<RoomDifficulty> getAllDifficulties() {
        return allDifficulties;
    }

    public void setAllDifficulties(List<RoomDifficulty> allDifficulties) {
        this.allDifficulties = allDifficulties;
    }

    public List<GameMode> getAllModes() {
        return allModes;
    }

    public void setAllModes(List<GameMode> allModes) {
        this.allModes = allModes;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public void setSearchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
    }

    public List<QuestionSet> getSelectedQuestionSets() {
        return selectedQuestionSets;
    }

    public void setSelectedQuestionSets(List<QuestionSet> selectedQuestionSets) {
        this.selectedQuestionSets = selectedQuestionSets;
    }

    public ScrollPaginator<QuestionSet> getSelectableSetPaginator() {
        return selectableSetPaginator;
    }

    public void setSelectableSetPaginator(ScrollPaginator<QuestionSet> selectableSetPaginator) {
        this.selectableSetPaginator = selectableSetPaginator;
    }
}
