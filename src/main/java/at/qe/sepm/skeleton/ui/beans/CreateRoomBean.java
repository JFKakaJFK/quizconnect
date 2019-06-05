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

    private IRoomAction roomAction; // the websocket connection
    private QuizRoomManager quizRoomManager;
    private QuestionSetService questionSetService;

    private int playerLimit = 42; // initial value
    private int step = 0; // the creation is split into 2 or 3 steps (depending on chosen options)
    private boolean setSelectionIsInitialized = false;
    private String searchPhrase = "";
    private RoomDifficulty difficulty = RoomDifficulty.easy;
    private GameMode mode = GameMode.normal;
    private List<RoomDifficulty> allDifficulties;
    private List<GameMode> allModes;

    private List<QuestionSet> selectedQuestionSets;
    private List<QuestionSet> selectableQuestionSets;
    private ScrollPaginator<QuestionSet> selectableSetPaginator;


    @Autowired
    public CreateRoomBean(IRoomAction roomAction,
                          QuestionSetService questionSetService,
                          QuizRoomManager quizRoomManager){
        assert roomAction != null;
        assert questionSetService != null;
        assert quizRoomManager != null;

        this.roomAction = roomAction;
        this.quizRoomManager = quizRoomManager;
        this.questionSetService = questionSetService;


        allDifficulties = Arrays.asList(RoomDifficulty.values());
        allModes = Arrays.asList(GameMode.values());
    }

    // validation

    /**
     * Validates the input for creating a new {@link at.qe.sepm.skeleton.logic.QuizRoom}.
     *
     * @return true if the input for room creation is valid
     */
    public boolean inputIsValid(){
        if(stepOneIsInvalid()) return false;
        if(mode == GameMode.mathgod) return true;
        return !(selectedQuestionSets == null || selectedQuestionSets.size() < 1 || selectedQuestionSets.size() > MAX_SETS); // check if at least one set is selected
    }

    private boolean stepOneIsInvalid(){
        if(playerLimit < 3 || playerLimit > 999){ // validate Player limit
            return true;
        }
        return difficulty == null || mode == null;
    }

    /**
     * Creates a new {@link at.qe.sepm.skeleton.logic.QuizRoom} if the input is valid and
     * redirects to the game view.
     */
    public void createRoomAndRedirect(){
        if(inputIsValid()){
            try {
                int pin = quizRoomManager.createRoom(playerLimit, difficulty, mode, mode.equals(GameMode.mathgod) ? null : selectedQuestionSets, roomAction);
                FacesContext.getCurrentInstance().getExternalContext().redirect("/quizroom/index.html?pin=" + pin);
            } catch (IllegalArgumentException e){
                log.error("Failed to create QuizRoom: " + e.getMessage());
            } catch (IOException e){
                log.error("Failed to redirect to game view");
            }
        }
    }

    // QuestionSet selection logic

    private void initSetSelection(){
        if(setSelectionIsInitialized) return;
        selectableQuestionSets = questionSetService.getAllQuestionSets();
        if(selectableQuestionSets == null){
            selectableQuestionSets = new ArrayList<>();
        }
        selectedQuestionSets = new ArrayList<>(10);
        selectableSetPaginator = new ScrollPaginator<>( 30);
        this.setSelectionIsInitialized = true;
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
        if(!setSelectionIsInitialized) return;
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
     * Returns wheter further {@link QuestionSet}s are selectable, prevents the user from adding to many
     *
     * @return true if more sets can be added
     */
    public boolean selectable(){
        if(!setSelectionIsInitialized) return false;
        return selectedQuestionSets.size() < MAX_SETS;
    }

    /**
     * Moves a {@link QuestionSet} from the {@link CreateRoomBean#selectableQuestionSets} to the {@link CreateRoomBean#selectedQuestionSets}.
     *
     * @param questionSet
     */
    public void selectSet(QuestionSet questionSet){
        moveSet(questionSet, selectableQuestionSets, selectedQuestionSets);
    }

    /**
     * Moves a {@link QuestionSet} from the {@link CreateRoomBean#selectedQuestionSets} to the {@link CreateRoomBean#selectableQuestionSets}.
     *
     * @param questionSet
     */
    public void unselectSet(QuestionSet questionSet){
        moveSet(questionSet, selectedQuestionSets, selectableQuestionSets);
    }

    /**
     * Helper function to move(remove from origin list, add to target list) a {@link QuestionSet}.
     *
     * @param questionSet
     *      The QuestionSet to move.
     * @param from
     *      The origin list.
     * @param to
     *      The target list.
     */
    private void moveSet(QuestionSet questionSet, List<QuestionSet> from, List<QuestionSet> to){
        if(!setSelectionIsInitialized) return;
        if(!from.contains(questionSet)){
            return;
        }
        from.remove(questionSet);
        if(!to.contains(questionSet)){
            to.add(questionSet);
        }
        filterSelectableQuestionSets();
    }

    // form step logic
    /*
     * The state machine contains 3 states, step 0 (mode selection), step 1 (set selection) and step 3 (confirmation).
     *
     * The valid state changes are:
     *
     * State 0 -> Mode Normal | Mode Reverse -> State 1
     * State 0 -> Mode Mathgod -> State 2
     *
     * State 1 -> Sets are selected -> State 2
     * State 1 -> Back -> State 0
     *
     * State 2 -> Back && Mode Mathgod -> State 0
     * State 2 -> Back && Mode != Mathgod -> State 1
     */

    /**
     * Changes the current step in the {@link at.qe.sepm.skeleton.logic.QuizRoom} creation to the next step.
     */
    public void nextStep(){
        if(disableNextStep()) return;
        if(step == 2){
            createRoomAndRedirect();
        } else if(step == 0){
            // check if selected mode is mathgod
            if(mode == GameMode.mathgod){
                if(selectedQuestionSets != null && !selectedQuestionSets.isEmpty()){
                    // unselect all sets
                    List<QuestionSet> copy = new ArrayList<>(selectedQuestionSets);
                    for(QuestionSet set: copy){
                        unselectSet(set);
                    }
                }
                this.step = 2;
            } else {
                initSetSelection();
                this.step = 1;
            }
        } else if(step == 1){
            this.step = 2;
        }
    }

    /**
     * Changes the current step in the {@link at.qe.sepm.skeleton.logic.QuizRoom} creation to the previous step.
     */
    public void prevStep(){
        if(disablePrevStep()) return;
        if(step == 2 && mode == GameMode.mathgod) {
            this.step = 0;
        } else if(step > 0){
            step--;
        }
    }

    /**
     * Checks if all inputs of the current step are valid, and advancing is possible
     * @return
     *          True if the next step should be disabled.
     */
    public boolean disableNextStep(){
        if(step == 0){
            // check if mode, maxplayers & diff are set
            return stepOneIsInvalid();
        } else if(step == 1){
            // check if selected questionsets are valid
            return !inputIsValid();
        }
        // step 2 is overview and confirmation of valid inputs, no validation required
        return false;
    }

    /**
     * Checks if going back to a previous step is possible
     * @return
     *          True if the previous step should be disabled.
     */
    public boolean disablePrevStep(){
        return step == 0;
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
        this.searchPhrase = searchPhrase.trim();
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

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
