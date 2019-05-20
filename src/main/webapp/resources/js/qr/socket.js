"use strict";

/* ======================== SOCKET ========================= */

const WS_FALLBACK = '/ws';
const WS_SOURCE = '/server/events';
const WS_TARGET = '/qc/events';

let socket = null;
let stompClient = null;

const connect = () => {
  if(socket !== null){
    return;
  }
  socket = new SockJS(WS_FALLBACK);
  stompClient = Stomp.over(socket);
  stompClient.debug = () => {}; // Disables debug messages

  stompClient.connect({}, (frame) => {
    // console.debug(frame);
    stompClient.subscribe(`${WS_SOURCE}/${state.pin}`, event => handleServerEvent(JSON.parse(event.body)));
    // sendAlivePing();
    console.debug(`SOCKET: connected`)
    getRoomInfo();
    sendAlivePing();// TODO
    getChatMessages();
  });
};

/**
 * Disconnects from {@link QuizRoom} and cancels alive ping
 */
const disconnect = () => {
    if(stompClient){
        stompClient.disconnect();
        stompClient = null;
        socket = null;
    }
    if(state.alivePing !== null){
        clearInterval(state.alivePing);
        state.alivePing = null;
    }
  console.debug(`SOCKET: disconnected`)
};

/**
 * Sends an event to the server
 *
 * @param event
 */
const sendEvent = (event) => {
  try {
    stompClient.send(`${WS_TARGET}/${state.pin}`, {}, JSON.stringify(event));
  } catch (e) {
    console.warn('SOCKET: failed to send message')
  }

};
