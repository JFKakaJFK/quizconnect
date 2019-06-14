"use strict";

import { INGAME, FINISHED, LOBBY, JOIN } from "./Constants.js";

/**
 * The game state.
 *
 * @type {{gameSessionTimer: null, pin: null, id: null, highScore: null, state: number, timeoutIsActive: boolean, timeoutRemainingTime: number, timeoutTimer: null, alivePing: null, info: {settings: {questionSets: Array, difficulty: string, mode: string, numJokers: number, score: number, pin: number}, players: Array}, game: {score: number, question: null, answers: Array}, messages: Array}}
 */
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
  messageQueue: [],
};

/**
 * All changes to the game state happen through this function, allowing fine grained control over rendering.
 * Adds the name of the current state to the classList of the document body.
 *
 * @param newState
 * @param render
 */
const setState = (newState, render = true) => {

  const settings = {};
  const info = Object.assign(state.info, newState.info, { settings });
  const question = {};
  const game = Object.assign(state.game, newState.game, { question });

  state = Object.assign(state, newState, { info, game });
  let gameState = state.state;
  switch (gameState){
    case INGAME:
      gameState = 'ingame';
      break;
    case LOBBY:
      gameState = 'lobby';
      break;
    case FINISHED:
      gameState = 'finished';
      break;
    default:
      gameState = 'join';
  }
  console.debug(`STATE: merged states, new State is ${gameState.toUpperCase()}:`, JSON.stringify(state)); // should keep state changes affecting the debug log
  document.body.setAttribute('class', gameState);

  if(render){
    let event = new CustomEvent('stateChange', {
      detail: state,
    });
    document.dispatchEvent(event);
  }
};

/**
 * Returns the current state, as unmodifiable object.
 *
 * @returns {{gameSessionTimer: null, pin: null, id: null, highScore: null, state: number, timeoutIsActive: boolean, timeoutRemainingTime: number, timeoutTimer: null, alivePing: null, info: {settings: {questionSets: Array, difficulty: string, mode: string, numJokers: number, score: number, pin: number}, players: Array}, game: {score: number, question: null, answers: Array}, messages: Array}}
 */
const getState = () => {
  const s = { ...state };
  Object.freeze(s);
  return s;
};

export default setState;

export {
  setState,
  getState,
}