"use strict";

/* ======================== CONSTANTS ========================= */

const WS_FALLBACK = '/ws';
const WS_SOURCE = '/server/events';
const WS_TARGET = '/qc/events';

const LOBBY = 0;
const INGAME = 1;

/* CLIENT EVENTS */
const GAME_INFO = "getGameInfo";
const ROOM_PLAYERS = "getRoomPlayers";
const READY = "readyUp";
const ANSWER_QUESTION = "answerQuestion";
const USE_JOKER = "useJoker";
const LEAVE_ROOM = "leaveRoom";
const CANCEL_TIMEOUT = "cancelTimeout";
const ALIVE_PING = "sendAlivePing";

/* SERVER EVENTS */
const READY_UP = "onReadyUp";
const PLAYER_JOIN = "onPlayerJoin";
const PLAYER_LEAVE = "onPlayerLeave";
const GAME_START = "onGameStart";
const GAME_END = "onGameEnd";
const JOKER_USE = "onJokerUse";
const SCORE_CHANGE = "onScoreChange";
const TIMER_SYNC = "onTimerSync";
const TIMEOUT_START = "onTimeoutStart";
const KICK = "onKick";
const ASSIGN_QUESTION = "assignQuestion";
const REMOVE_QUESTION = "removeQuestion";

/* ======================== STATE ========================= */

/* TODO
 *
 * 1. state structure
 *   - flattened (almost everything on top level)
 *   - semantically (what is rendered together is grouped together)
 *   - by state (what happens simultaneously is grouped together)
 * 2. state updates
 *   - never directly: wrap in class (no information hiding -> why?)
 *   - by function:
 *     - new state vs old state
 *     - https://gist.github.com/Yimiprod/7ee176597fef230d1451
 *     - TODO finally learn https://lodash.com/docs/4.17.11#isEqual
 *     - on differences, do update stuff -> state is source of truth | method is state of truth
 *       - update on event (messy) | update on state change, events change state (react)
 *
 */

class STATE {
    constructor(){
        this.state = {

        }
    }


}

// TODO playerId & Pin: either put in localstorage or mage getId RequestMapping (no broadcast)
// -> join per requestmapping: info as return

/**
 * The state is a local copy of the game state. It is updated pericdically by the server,
 * which is the single source of truth.
 *
 * @type {{pin: number, id: number, state: number}}
 */
const state = {
    pin: parseInt(localStorage.getItem('pin')),
    id: parseInt(localStorage.getItem('playerId')),
    state: LOBBY,
};

/* ======================== SOCKET ========================= */

const socket = new SockJS(WS_FALLBACK);
const stompClient = Stomp.over(socket);
stompClient.connect({}, (frame) => {
    console.log(frame);
    stompClient.subscribe(`${WS_SOURCE}/${state.pin}`, event => handleServerEvent(JSON.parse(event.body)));
    // TODO get game info, set alive ping & room players
    getGameInfo();
    getRoomPlayers();
});

/**
 * Disconnects from {@link QuizRoom} and cancels alive ping
 */
const disconnect = () => {
    if(stompClient){
        stompClient.disconnect();
    }
    if(ping){
        clearInterval(ping);
    }
};

/**
 * Sends an event to the server
 *
 * @param event
 */
const sendEvent = (event) => {
    stompClient.send(`${WS_TARGET}/${state.pin}`, {}, JSON.stringify(event));
};

/**
 * Handles a {@link ServerEvent} by delegating to handler function. Game events have Priority
 *
 * @param response
 */
const handleServerEvent = (response) => {
    console.debug(response);
    console.log(response); // TODO remove

    // do nothing if event is no action
    if(response === undefined || response.event === "success" || response.event === "error"){
        return;
    }
    if(state.state === INGAME){ // handle game events w/ priority
        switch (response.event){
            case TIMER_SYNC:
                return handleTimerSync(response);
            case JOKER_USE:
                return handleJokerUse(response);
            case ASSIGN_QUESTION:
                return handleAssignQuestion(response);
            case REMOVE_QUESTION:
                return handleRemoveQuestion(response);
            case SCORE_CHANGE:
                return handleScoreChange(response);
            case GAME_END:
                return handleGameEnd(response);
        }
    } else if(state.state === LOBBY) { // handle lobby events only if in lobby
        switch (response.event){
            case GAME_START:
                return handleGameStart(response);
            case READY_UP:
                return handleReadyUp(response);
            case PLAYER_JOIN:
                return handlePlayerJoin(response);
        }
    }
    switch (response.event){  // handle events where the state doesn't matter
        case TIMEOUT_START: // TODO only ingame??
            return handleTimeoutStart(response);
        case KICK:
            return handleKick(response);
        case PLAYER_LEAVE: // TODO only ingame?
            return handlePlayerLeave(response);
        default:
            console.error("invalid event type");
    }
};

/* Server Event Handlers */

const handleReadyUp = (event) => {
  // TODO: find player in state & update view
    console.log("Handle ready up");
};

const handlePlayerJoin = (event) => {
    // TODO: add player to state & update view
    console.log("player joined");
};

const handlePlayerLeave = (event) => {
    // TODO: remove player from state & update view
    console.log("player left");
};

const handleGameStart = (event) => {
    // TODO: state.state from LOBBY -> INGAME
    console.log("game started");
};

const handleGameEnd = (event) => {
    // TODO change view(&state) with current(=final) score
    console.log("game ended");
};

const handleJokerUse = (event) => {
    // TODO: sync joker number w/ state, rerender if no jokers left
    console.log("joker was used");
};

const handleScoreChange = (event) => {
    // TODO: update state & rerender current score
    console.log("score change");
};

const handleTimerSync = (event) => {
    // TODO: update time left (gets changed & rendered @ fixed interval, so nothing to do else?)
    console.log("timer sync");
};

const handleTimeoutStart = (event) => {
    // TODO: display modal & start countdown
    console.log("timeout started")
};

const handleKick = (event) => {
    if(event.playerId === state.id){
        disconnect();
        window.location.href = "/player/home.xhtml?kicked=true"; // TODO ?
    }
};

const handleAssignQuestion = (event) => {
    // TODO update questions in state & display if relevat to user
    console.log("new question");
};

const handleRemoveQuestion = (event) => {
    // TODO update questions in state & rerender(remove) if necessary
    console.log("remove question");
};

// TODO

/* Client Events */

const getGameInfo = () => {
    sendEvent({event: GAME_INFO});
};

const getRoomPlayers = () => {
    sendEvent({event: ROOM_PLAYERS});
};

const readyUp = () => {
    sendEvent({event: READY_UP, playerId: state.id});
};

// TODO or handle by onclick event
const answerQuestion = (questionId, answerId) => {
    sendEvent({
        event: ANSWER_QUESTION,
        playerId: state.id,
        answerId,
        questionId,
    })
};

// TODO disable joker for x seconds after click & no multiple joker waste (easy sync w/ server)
const useJoker = () => {
  sendEvent({event: USE_JOKER, playerId: state.id})
};

const leaveRoom = () => {
    sendEvent({event: LEAVE_ROOM, playerId: state.id})
};

const cancelTimeout = () => {
    sendEvent({event: CANCEL_TIMEOUT, playerId: state.id})
};

const sendAlivePing = () => {
    sendEvent({event: ALIVE_PING, playerId: state.id})
};

/* ======================== RENDER FUNCTIONS ========================= */

/* TODO
 *
 *  - render Lobby
 *    - render game info
 *    - render players
 *      - render player
 *      - remove player
 *  - render game
 *    - render question
 *    - render timer
 *    - render answers
 *      - render answer
 *      - remove answer
 */

/* ======================== STATE LOGIC ========================= */

// TODO

// 1. getGameInfo

// 2. setAlivePings

// TODO call methods in stomp connect callback
const ping = setInterval(sendAlivePing(), state.alivePingInterval);

/* IF STATE IS LOBBY */

// 4. get and show Players

//getRoomPlayers();

// Wait for game start, update view on ready | player join

/* IF STATE IS INGAME */

// 4. Wait for question & answer assignment

// Wait for answer events | update view on server events

// TODO view updates

// Based on states

// Wrap state change -> diff old & newstate & rerender differences

// install event listeners for all player actions at given time


