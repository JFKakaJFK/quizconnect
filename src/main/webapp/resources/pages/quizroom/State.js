"use strict";

import {INGAME, JOIN, LOBBY} from "./Constants.js";

let state = {
  gameSessionTimer: null,
  pin: null,
  id: null, // TODO: change docs (playerid -> id)
  highScore: null,
  state: JOIN,
  timeoutIsActive: false, // TODO
  timeoutRemainingTime: 0,// TODO
  timeoutTimer: null,// TODO 
  alivePing: null,
  info: {
    settings: {
      questionSets: [],
      difficulty: '',
      mode: '',
      numJokers: 0,
      score: 0,
      pin: 0,
    },
    players: [],
  },
  game: {
    score: 0,
    question: null,
    answers: [],
  },
  messages: [],
};

const setState = (newState, render = true) => {

  const settings = {};
  const info = Object.assign(state.info, newState.info, { settings });
  const question = {};
  const game = Object.assign(state.game, newState.game, { question });

  state = Object.assign(state, newState, { info, game });
  console.debug(`STATE: merged states, new State is ${state.state === INGAME ? 'INGAME' : state.state === LOBBY ? 'LOBBY' : state.state === JOIN ? 'JOIN' : 'FINISHED'}:`, JSON.stringify(state)); // should keep state changes affecting the debug log

  if(render){
    let event = new CustomEvent('stateChange', {
      detail: state,
    });
    document.dispatchEvent(event);
  }
};

export default setState;