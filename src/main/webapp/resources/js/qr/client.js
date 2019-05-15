"use strict";

/* ======================== CLIENT EVENTS ========================= */

/* CLIENT EVENTS */
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
  console.debug(`CLIENT: getting room info`)
};

const readyUp = () => {
  sendEvent({event: READY, playerId: state.id});
  console.debug(`CLIENT: this player is ready`)
};

const answerQuestion = (questionId, answerId) => {
  sendEvent({
    event: ANSWER_QUESTION,
    playerId: state.id,
    answerId,
    questionId,
  })
  console.debug(`CLIENT: answered question ${questionId}`)
};

// TODO disable joker for x seconds after click & no multiple joker waste (easy sync w/ server)
const useJoker = () => {
  sendEvent({event: USE_JOKER, playerId: state.id})
  console.debug(`CLIENT: using joker`)
};

const leaveRoom = () => {
  sendEvent({event: LEAVE_ROOM, playerId: state.id});
  disconnect();
  clearLocalStorage();
  console.debug(`CLIENT: leaving room`)
};

const cancelTimeout = () => {
  sendEvent({event: CANCEL_TIMEOUT, playerId: state.id});
  if(state.timeoutTimer){
    clearInterval(state.timeoutTimer);
    state.timeoutTimer = null;
    setState({
      timeoutIsActive: false,
    })
  }
  // remove event listeners
  document.removeEventListener('click', cancelTimeout);
  document.removeEventListener('touchstart', cancelTimeout);
  document.removeEventListener('mousemove', cancelTimeout);
  console.debug(`CLIENT: timeout cancelled`)
};

const sendAlivePing = () => {
  sendEvent({event: ALIVE_PING, playerId: state.id})
};