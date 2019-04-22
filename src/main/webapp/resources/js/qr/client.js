"use strict";

/* ======================== CLIENT EVENTS ========================= */

/* CLIENT EVENTS */
const GAME_INFO = "getGameInfo";
const ROOM_PLAYERS = "getRoomPlayers";
const READY = "readyUp";
const ANSWER_QUESTION = "answerQuestion";
const USE_JOKER = "useJoker";
const LEAVE_ROOM = "leaveRoom";
const CANCEL_TIMEOUT = "cancelTimeout";
const ALIVE_PING = "sendAlivePing";
const ROOM_INFO = "getRoomInfo";

/* Client Events */

const getRoomInfo = () => {
  sendEvent({ event: ROOM_INFO });
};

const getGameInfo = () => {
  sendEvent({event: GAME_INFO});
};

const getRoomPlayers = () => {
  sendEvent({event: ROOM_PLAYERS});
};

const readyUp = () => {
  sendEvent({event: READY, playerId: state.id});
};

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
  disconnect()
};

const cancelTimeout = () => {
  sendEvent({event: CANCEL_TIMEOUT, playerId: state.id})
  if(state.timeoutTimer){ // TODO do w/ setState?!!
    clearInterval(state.timeoutTimer);
    state.timeoutTimer = null;
  }
  // remove event listeners
  document.removeEventListener('click', cancelTimeout);
  document.removeEventListener('touchstart', cancelTimeout);
  document.removeEventListener('mousemove', cancelTimeout);
};

const sendAlivePing = () => {
  sendEvent({event: ALIVE_PING, playerId: state.id})
};