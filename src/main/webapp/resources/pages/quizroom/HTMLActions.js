"use strict";
import { JOIN, FINISHED, URL_HOME } from "./Constants.js";
import { getState } from "./State.js";
import { Client } from "./Socket.js";

/**
 * Checks whether the user is currently playing, and shows a modal to confirm if so.
 */
const leaveAction = () => {
  const { state } = getState();
  if(state === JOIN || state === FINISHED){
    window.location.href = URL_HOME;
  } else {
    // open modal
    $('#leave').modal({backdrop: 'static'}); // $('#leave').modal('show');
  }
};

const leaveActions = document.querySelectorAll('[data-leave]');
leaveActions.forEach(a => a.addEventListener('click', leaveAction));

/**
 * Leaves the game correctly by closing the socket and leaving
 */
const confirmLeaveAction = () => {
  // call leaveRoom (socket) & redirect TODO
  Client.leaveRoom();
  window.location.href = URL_HOME;
};

const confirmLeaves = document.querySelectorAll('[data-confirm-leave]');
confirmLeaves.forEach(a => a.addEventListener('click', confirmLeaveAction));

