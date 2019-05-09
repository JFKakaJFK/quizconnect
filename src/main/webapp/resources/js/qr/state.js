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
    },
    players: [],
  },
  game: {
    score: 0,
    question: null,
    answers: [],
  },
};

// if pin & playerId not set, redirect to join page
/*
if(!state.pin || !state.id){
  window.location.href = URL_JOIN;
}
*/

const setState = (newState) => {
  // console.log("new", newState);

  // TODO deep merge? https://davidwalsh.name/javascript-deep-merge
  state = Object.assign(state, newState, { info: Object.assign(state.info, newState.info) }, { game: Object.assign(state.game, newState.game)});
  console.log("new State", state);

  render(state);
};