"use strict";

/* ======================== STATE ========================= */

/**
 * The state is a local copy of the game state. It is updated pericdically by the server,
 * which is the single source of truth.
 *
 * @type {{pin: number, id: number, state: number}}
 */
let state = {
  gameSessionTimer: null,
  pin: null,
  id: null, // TODO: refactor as playerId | change docs
  state: JOIN,
  timeoutIsActive: false,
  timeoutRemainingTime: 0,
  timeoutTimer: null,
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

const setState = (newState) => {
  // console.log("new", newState);

  // TODO deep merge? https://davidwalsh.name/javascript-deep-merge
  state = Object.assign(state, newState, { info: Object.assign(state.info, newState.info) }, { game: Object.assign(state.game, newState.game)});
  console.debug(`STATE: merged states, new State is ${state.state === INGAME ? 'INGAME' : state.state === LOBBY ? 'LOBBY' : state.state === JOIN ? 'JOIN' : 'FINISHED'}:`, JSON.stringify(state)); // should keep state changes affecting the debug log

  render(state);
};