"use strict";

const WS_FALLBACK = '/ws';
const WS_SOURCE = '/server/events';
const WS_TARGET = '/qc/events';

const PIN = 10;

/* CLIENT EVENTS */

/* SERVER EVENTS */
const KICK = 'onKick';
const GAMEEND = 'onGameEnd';
// TODO

const handleServerEvent = (response) => {
  console.log(response);
  const { event } = response;
  if(!event){
      console.error("Invalid Response")
  }
  switch (event){
      case KICK:
          console.log("event of type kick");
          break;
      case GAMEEND:
          disconnect();
          // TODO change view with current(=final) score
          console.log("disconnected");
          break;
      default:
          console.log("TODO handle correctly")
  }
};

const socket = new SockJS(WS_FALLBACK);
const stompClient = Stomp.over(socket);
stompClient.connect({}, (frame) => {
    console.log(frame);
    stompClient.subscribe(`${WS_SOURCE}/${PIN}`, event => handleServerEvent(JSON.parse(event.body)));
});

const disconnect = () => {
    if(stompClient){
        stompClient.disconnect();
    }
};

const sendEvent = (event) => {
    stompClient.send(`${WS_TARGET}/${PIN}`, {}, JSON.stringify(event));
};
