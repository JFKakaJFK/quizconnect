"use strict";

import {
  JOIN,
  LOBBY,
  INGAME,
  FINISHED,
  WS_FALLBACK,
  WS_SOURCE,
  WS_TARGET,
  ALIVE_PING_PERIOD,
  ACTIVITY_CHECK_PERIOD,
  PERIODIC_ACTIVITY_PINGS,
  URL_HOME,
  INFO_MSG,
} from "./Constants.js";
import setState, { getState } from "./State.js";

let socket = null;
let stompClient = null;
let alivePing = null; // timer for sending the aliveping regularly
let timeoutInterval = null; // timer for sending the canceltimeout regularly

/* -------- SOCKET CONNECTION --------- */

/**
 * Connects to the backend
 */
const connect = () => {
  const { pin } = getState();
  if(socket !== null || pin === null || isNaN(pin)){ return; }

  socket = new SockJS(WS_FALLBACK);
  stompClient = Stomp.over(socket);
  stompClient.debug = () => {}; // Disables debug messages

  stompClient.connect({}, () => {
    stompClient.subscribe(`${WS_SOURCE}/${pin}`, event => handleServerEvent(JSON.parse(event.body)));
    console.debug(`SOCKET: connected`);
    getRoomInfo();

    setTimeout(() => {
      alivePing = setInterval(sendAlivePing, ALIVE_PING_PERIOD)
    }, ALIVE_PING_PERIOD); // wait for some time to ensure that the beautiful SockJS doesn't f*** up ¯\_(ツ)_/¯

    if(PERIODIC_ACTIVITY_PINGS){
      setTimeout(() => {
        timeoutInterval = setInterval(checkActivity, ACTIVITY_CHECK_PERIOD);
      }, ALIVE_PING_PERIOD);
    }
  });
};

/**
 * Checks for any player activity in the last x seconds
 */
const checkActivity = () => {
  // listen for any activity
  document.addEventListener('click', cancelTimeout);
  document.addEventListener('touchstart', cancelTimeout);
  document.addEventListener('mousemove', cancelTimeout);
};

/**
 * Disconnects from backend and cancels alive ping
 */
const disconnect = () => {
  if(stompClient){
    stompClient.disconnect();
    stompClient = null;
    socket = null;
  }
  if(alivePing !== null){
    clearInterval(alivePing);
    alivePing = null;
  }
  if(timeoutInterval !== null){
    clearInterval(timeoutInterval);
    timeoutInterval = null;
  }
  // ugly but does the job
  sessionStorage.removeItem('pin');
  sessionStorage.removeItem('timeStamp');
  console.debug(`SOCKET: disconnected`)
};

/**
 * Sends an event to the server
 *
 * @param event
 */
const sendEvent = (event) => {
  const { pin } = getState();
  if(stompClient === null || pin === null || isNaN(pin)){ return; }
  try {
    stompClient.send(`${WS_TARGET}/${pin}`, {}, JSON.stringify(event));
  } catch (e) {
    console.warn('SOCKET: failed to send message')
  }
};

/* -------- CLIENT EVENTS --------- */

/* CLIENT EVENTS */
const READY = "readyUp";
const ANSWER_QUESTION = "answerQuestion";
const USE_JOKER = "useJoker";
const LEAVE_ROOM = "leaveRoom";
const CANCEL_TIMEOUT = "cancelTimeout";
const ALIVE_PING = "sendAlivePing";
const ROOM_INFO = "getRoomInfo";

const CHAT_MESSAGE = "chatMessage";
const CHAT_MESSAGES = "getChatMessages";

/**
 * Requests the room info.
 */
const getRoomInfo = () => {
  sendEvent({ event: ROOM_INFO });
  console.debug(`CLIENT: getting room info`)
};

/**
 * Changes a players ready state to ready (only in lobby)
 */
const readyUp = () => {
  const { id } = getState();
  sendEvent({event: READY, playerId: id});
  console.debug(`CLIENT: this player is ready`)
};

/**
 * Answers a specific question.
 *
 * @param questionId
 * @param answerId
 */
const answerQuestion = (questionId, answerId) => {
  const { id } = getState();
  sendEvent({
    event: ANSWER_QUESTION,
    playerId: id,
    answerId,
    questionId,
  });
  console.debug(`CLIENT: answered question ${questionId}`)
};

/**
 * Uses a reshuffle joker.
 */
const useJoker = () => {
  const { id } = getState();
  sendEvent({event: USE_JOKER, playerId: id});
  console.debug(`CLIENT: using joker`)
};

/**
 * Disconnect from the current game.
 */
const leaveRoom = () => {
  const { id } = getState();
  sendEvent({event: LEAVE_ROOM, playerId: id});
  disconnect();
  console.debug(`CLIENT: leaving room`)
};

/**
 * Cancels an inactivity timeout.
 */
const cancelTimeout = () => {
  const { id } = getState();
  sendEvent({event: CANCEL_TIMEOUT, playerId: id});
  const event = new CustomEvent('cancelTimeout');
  document.dispatchEvent(event);
  // remove event listeners
  document.removeEventListener('click', cancelTimeout);
  document.removeEventListener('touchstart', cancelTimeout);
  document.removeEventListener('mousemove', cancelTimeout);
  console.debug(`CLIENT: timeout cancelled`)
};

/**
 * Sends an alive ping to the server
 */
const sendAlivePing = () => {
  const { id } = getState();
  sendEvent({event: ALIVE_PING, playerId: id});
  console.debug(`CLIENT: alive ping sent`)
};

/**
 * Sends a new chat message.
 *
 * @param message
 */
const sendChatMessage = (message) => {
  const { id } = getState();
  sendEvent({event: CHAT_MESSAGE, message: message, playerId: id});
};

/**
 * Requests the chat history
 */
const getChatMessages = () => {
  sendEvent({event: CHAT_MESSAGES});
};

/**
 * A wrapper for all client events
 *
 * @type {{getRoomInfo: getRoomInfo, readyUp: readyUp, answerQuestion: answerQuestion, useJoker: useJoker, leaveRoom: leaveRoom, cancelTimeout: cancelTimeout, sendChatMessage: sendChatMessage, getChatMessages: getChatMessages}}
 */
const Client = {
  getRoomInfo,
  readyUp,
  answerQuestion,
  useJoker,
  leaveRoom,
  cancelTimeout,
  sendChatMessage,
  getChatMessages,
};

/* -------- SERVER EVENTS --------- */

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

/**
 * Handles a server event by delegating to handler function. Game events have Priority
 *
 * @param response
 */
let chatHistoryHandled = false;
let roomInfoHandled = false;
const handleServerEvent = (response) => {
  const { state } = getState();
  // do nothing if event is no action
  if(response === undefined || response.event === null || response.event === "success" || response.event === "error"){
    return;
  }
  if(!roomInfoHandled && response.event !== ROOM_INFO){
    getRoomInfo();
  }

  console.debug(`SERVER: received '${response.event}' event, state is ${state === INGAME ? 'INGAME' : state === LOBBY ? 'LOBBY' : state === JOIN ? 'JOIN' : 'FINISHED'}`);
  if(state === INGAME){ // handle game events w/ priority
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
  } else if(state === LOBBY) { // handle lobby events only if in lobby
    switch (response.event){
      case GAME_START:
        return handleGameStart(response);
      case READY_UP:
        return handleReadyUp(response);
    }
  }
  switch (response.event){  // handle events where the state doesn't matter
    case TIMEOUT_START:
      return handleTimeoutStart(response);
    case PLAYER_JOIN:
      return handlePlayerJoin(response);
    case KICK:
      return handleKick(response);
    case PLAYER_LEAVE:
      return handlePlayerLeave(response);
    case CHAT_MESSAGE:
      return handleChatMessage(response);
    default:
      if(!roomInfoHandled && response.event === ROOM_INFO){
        roomInfoHandled = true;
        handleRoomInfo(response);
        return getChatMessages();
      } else if(!chatHistoryHandled && response.event === CHAT_MESSAGES) {
        chatHistoryHandled = true;
        return handleChatMessages(response);
      } else if(!chatHistoryHandled){
        getChatMessages();
      } else {
        console.debug(`SERVER: received '${response.event}' event`)
      }
  }
};

/* Event Handlers */

/**
 * Handles a room info event.
 *
 * @param pin
 * @param difficulty
 * @param mode
 * @param questionSets
 * @param numJokers
 * @param num
 * @param players
 * @param state
 */
const handleRoomInfo = ({ pin, difficulty, mode, questionSets, numJokers, num, players, state }) => {
  setState({
    state,
    info: {
      pin,
      difficulty,
      mode,
      questionSets,
      numJokers,
      players,
    },
    game: {
      score: 0,
      jokersLeft: numJokers,
    }
  });
  console.debug(`SERVER: room info updated`);
};

/**
 * Handles the ready up of another player
 *
 * @param playerId
 */
const handleReadyUp = ({ playerId }) => {
  const { info } = getState();
  setState({
    info: {
      players: info.players.map(p => {
        if(p.id === playerId){
          p.ready = true;
        }
        return p;
      }),
    },
  });
  let p = info.players.find(p => p.id === playerId);
  if(p !== undefined) showChatMessage(`${p.username} is ready`);
  console.debug(`SERVER: player ${playerId} is ready`);
};

/**
 * Handles the join of another player
 *
 * @param player
 */
const handlePlayerJoin = ({ player }) => {
  const { info } = getState();
  setState({
    info: {
      players: info.players.concat([player])
    },
  });
  showChatMessage(`${player.username} joined the game`);
  console.debug(`SERVER: player ${player.id} joined`);
};

/**
 * Handles the leave event of another player.
 *
 * @param playerId
 */
const handlePlayerLeave = ({ playerId }) => {
  const { info } = getState();
  setState({
    info: {
      players: info.players.filter(p => p.id !== playerId),
    }
  });
  let p = info.players.find(p => p.id === playerId);
  if(p !== undefined) showChatMessage(`${p.username} left the game`);
  console.debug(`SERVER: player ${playerId} left`)
};

/**
 * Handles the start of the game.
 */
const handleGameStart = () => {
  setState({
    state: INGAME,
  });
  console.debug(`SERVER: game started`)
};

/**
 * Handles the end of the game.
 */
const handleGameEnd = () => {
  const { state } = getState();
  if(state === LOBBY || state === JOIN){
    disconnect();
    // TODO show error or something -> game ended early
    alert('Something happened');
    setTimeout(() => window.location.href = URL_HOME, 500);
  } else {
    const params = new URLSearchParams(window.location.search);
    if(params.has('pin')) window.history.replaceState({}, document.title, '/quizroom/index.html');
    showChatMessage(`Game ended.`);
    disconnect(); // TODO leave open for chat?
    setState({
      state: FINISHED,
    });
    const event = new CustomEvent('gameEnd');
    document.dispatchEvent(event);
  }
  console.debug(`SERVER: game ended`)
};

/**
 * Handles the use of a joker
 *
 * @param remaining
 */
const handleJokerUse = ({ remaining }) => {
  setState({
    game: {
      jokersLeft: remaining,
    }
  });
  showChatMessage(`A joker was used`);
  const event = new CustomEvent('jokerUse');
  document.dispatchEvent(event);
  console.debug(`SERVER: joker was used (${remaining} jokers remaining)`)
};

/**
 * Handles a change of the score.
 *
 * @param newScore
 */
const handleScoreChange = ({ newScore }) => {
  setState({
    game: {
      score: newScore,
    }
  });
  console.debug(`SERVER: score changed to ${newScore}`)
};

/**
 * Handles a timer syncronization between client and server.
 *
 * @param questionId
 * @param remaining
 */
const handleTimerSync = ({ questionId, remaining }) => { // TODO how to handle?
  const { game } = getState();
  if(game.question == null){ return; }
  if(questionId === game.question.questionId){
    setState({
      game: {
        question: Object.assign(game.question, { remaining, })
      }
    })
  }
  console.debug(`SERVER: timer sync, question ${questionId} has ${remaining/1000}s left`);
};

/**
 * Handles the start of a timeout.
 *
 * @param playerId
 * @param remaining
 */
const handleTimeoutStart = ({ playerId, remaining }) => {
  const { id } = getState();
  if(id !== playerId){
    return;
  }
  // listen for any activity
  document.addEventListener('click', cancelTimeout);
  document.addEventListener('touchstart', cancelTimeout);
  document.addEventListener('mousemove', cancelTimeout);
  const event = new CustomEvent('timeoutStart', { detail: {
    remaining,
  }});
  document.dispatchEvent(event);
  console.debug(`SERVER: timeout started, ${remaining / 1000}s left`)
};

/**
 * Handles the kick of the current player.
 *
 * @param playerId
 */
const handleKick = ({ playerId }) => {
  const { id, info } = getState();
  if(playerId === id){
    disconnect();
    window.location.href = URL_HOME;
  }
  let p = info.players.find(p => p.id === playerId);
  if(p !== undefined) showChatMessage(`${p.username} was kicked`);
  console.debug(`SERVER: player ${playerId} was kicked`)
};

/**
 * Sets the game state according to the question assignment. If question & answer are assigned to the
 * same user, the state is updated multiple times(Given the chances of that for sufficiently many players
 * this should be more efficient).
 *
 * @param questionId
 * @param type
 * @param question
 * @param playerId
 * @param timeRemaining
 * @param answers
 */
const handleAssignQuestion = ({ questionId, type, question, playerId, timeRemaining, answers }) => {
  const state = getState();
  // check if player is current player
  if(playerId === state.id){
    if(state.game.question !== null){
      console.error('Cannot display multiple questions');
    }
    setState({
      game: {
        question: {
          questionId,
          type,
          question,
          remaining: timeRemaining,
        }
      }
    });
    console.debug(`SERVER: new question assigned to this player`);
  }
  // if new answers for player, add answers
  if(answers.find(a => a.playerId === state.id) !== undefined){
    setState({
      game: {
        answers: state.game.answers.concat(
          answers.filter(a => a.playerId === state.id)
            .map(a => ({
              questionId,
              type,
              answerId: a.answerId,
              answer: a.answer,
            }))
        ),
      }
    });
    console.debug(`SERVER: new answers assigned to this player`);
  }
};

/**
 * Handles the removal of a question and its answers.
 *
 * @param questionId
 */
const handleRemoveQuestion = ({ questionId }) => {
  const state = getState();
  // has this player the question?
  if(state.game.question !== null && questionId === state.game.question.questionId){
    setState({
      game: {
        question: null,
      }
    })
  }
  // does the player have an answer/answers to the question? if so remove them
  if(state.game.answers.find(a => a.questionId === questionId) !== undefined){
    setState({
      game: {
        answers: state.game.answers.filter(a => a.questionId !== questionId),
      }
    })
  }
  console.debug(`SERVER: question ${questionId} was removed`);
};

/**
 * Handles a new chat message.
 *
 * @param message
 */
const handleChatMessage = ({ message }) => {
  const { messageQueue } = getState();
  setState({
    messageQueue: messageQueue.concat([message]),
  });
  console.debug(`SERVER: received '${message.message}' from '${message.from}'`)
};

/**
 * Handles the chat history.
 *
 * @param messages
 */
const handleChatMessages = ({ messages }) => {
  /* FUN FACT
   * The LobbyControllers functions to handle the ready up process have a fundamental design flaw
   * based on this method and the fact, that the question history is requested immediately after the room info.
   * Since the ready up buttons attach the event listeners before the node (player box) is attached to the DOM
   * the ready up events can only be listened for if the ready up button method is called twice, which is achieved
   * by this method...*/
  setState({
    messageQueue: messages,
  });
  console.debug(`SERVER: received all chat messages`)
};

/* ------------ UTIL --------- */

/**
 * Adds an info message to the chat.
 *
 * @param message
 */
const showChatMessage = (message) => {
  const { messageQueue } = getState();
  setState({
    messageQueue: messageQueue.concat([{
      message,
      from: INFO_MSG,
      id: -messageQueue.length, // a simple but hacky way of generating definitely unique ids
      timestamp: new Date().valueOf(),
    }]),
  })
};

/**
 * Handles the socket connection if the window is closed or refreshed.
 *
 * @param e
 */
const handleUnload = (e) => {
  e.preventDefault();
  const state = getState();
  disconnect();
  sessionStorage.setItem('pin', state.pin.toString());
  sessionStorage.setItem('timeStamp', (new Date().valueOf() + 5000).toString());
  e.returnValue = '';
};

document.addEventListener('unload', handleUnload);

export default Client;

export {
  connect,
  disconnect,
  showChatMessage,
  Client,
}
