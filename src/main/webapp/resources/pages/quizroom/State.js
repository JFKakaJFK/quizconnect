"use strict";

import Animate from './Animate.js';
import { INGAME, FINISHED, LOBBY, JOIN } from "./Constants.js";

/**
 * The game state.
 * @type {{pin: null, id: null, highScore: null, state: number, info: {questionSets: Array, difficulty: string, mode: string, numJokers: number, score: number, pin: number, players: Array}, game: {score: number, jokersLeft: number, allowJokerUse: boolean, question: null, answers: Array}, messageQueue: Array}}
 */
let state = {
  // general
  pin: null,
  id: null, // TODO: frontend + socket docs
  highScore: null,
  state: JOIN,

  // lobby
  info: {
    questionSets: [],
    difficulty: '',
    mode: '',
    numJokers: 0,
    score: 0,
    pin: 0,
    players: [],
  },

  // ingame
  game: {
    score: 0,
    jokersLeft: 0,
    question: null,
    answers: [],
  },

  // chat
  messageQueue: [],
};

/**
 * All changes to the game state happen through this function, allowing fine grained control over rendering.
 * Adds the name of the current state to the classList of the document body and sets the page (not window) title.
 *
 * @param newState
 * @param render
 */
const setState = (newState, render = true) => {
  const { state: prevState } = state;

  const info = Object.assign(state.info, newState.info);
  let game = Object.assign(state.game, newState.game);

  if(newState.hasOwnProperty('game') && newState.game.hasOwnProperty('question')){
    if(newState.game.question === null){
      game.question = null;
    } else {
      game = Object.assign(game, {question: Object.assign(state.game.question, newState.game.question)});
    }
  }

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

  // trigger stateChange event
  if(render){
    let event = new CustomEvent('stateChange', {
      detail: state,
    });
    document.dispatchEvent(event);
  }

  // animate state changes
  const title = document.getElementById('title');
  if(!title) return;
  if(gameState === 'ingame') gameState = 'in game';
  if(prevState !== state.state || title.textContent !== gameState) Animate('#title', 'fadeOut', () => {
    title.textContent = gameState;
    Animate('#title', 'fadeIn');
  });
};

/**
 * Returns the current state, as unmodifiable object.
 *
 * @returns {{gameSessionTimer: null, pin: null, id: null, highScore: null, state: number, timeoutIsActive: boolean, timeoutRemainingTime: number, timeoutTimer: null, alivePing: null, info: {settings: {questionSets: Array, difficulty: string, mode: string, numJokers: number, score: number, pin: number}, players: Array}, game: {score: number, question: null, answers: Array}, messages: Array}}
 */
const getState = () => {
  const s = { ...state };
  Object.freeze(s); // allow only read access
  return s;
};

export default setState;

export {
  setState,
  getState,
}