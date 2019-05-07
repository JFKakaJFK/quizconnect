"use strict";

/* ======================== SOCKET ========================= */

const WS_FALLBACK = '/ws';
const WS_SOURCE = '/server/events';
const WS_TARGET = '/qc/events';

let socket = null;
let stompClient = null;

stompClient.debug = () => {}; // Disables debug messages

const connect = () => {
  if(socket !== null){
    return;
  }
  socket = new SockJS(WS_FALLBACK);
  stompClient = Stomp.over(socket);
  stompClient.connect({}, (frame) => {
    console.debug(frame);
    stompClient.subscribe(`${WS_SOURCE}/${state.pin}`, event => handleServerEvent(JSON.parse(event.body)));
    // sendAlivePing();
    // getGameInfo();
    // getRoomPlayers();
    getRoomInfo();
  });
};

/**
 * Disconnects from {@link QuizRoom} and cancels alive ping
 */
const disconnect = () => {
    if(stompClient){
        stompClient.disconnect();
    }
    if(state.alivePing !== null){
        clearInterval(state.alivePing);
        state.alivePing = null;
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
