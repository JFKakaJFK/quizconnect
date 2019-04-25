"use strict";

/* ======================== STATE ========================= */

/**
 * The state is a local copy of the game state. It is updated pericdically by the server,
 * which is the single source of truth.
 *
 * @type {{pin: number, id: number, state: number}}
 */
let state = {
  pin: parseInt(localStorage.getItem('pin')),
  id: parseInt(localStorage.getItem('playerId')), // TODO: refactor as playerId | change docs
  state: LOBBY,
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
if(!state.pin || !state.id){
  window.location.href = URL_JOIN;
}

/* TODO
 * state updates
 *   - never directly: wrap in class (no information hiding -> why?)
 *   - by function:
 *     - new state vs old state
 *     - https://gist.github.com/Yimiprod/7ee176597fef230d1451
 *     - TODO https://lodash.com/docs/4.17.11#isEqual
 *     - on differences, do update stuff -> state is source of truth | method is state of truth
 *       - update on event (messy) | update on state change, events change state (react)
 *
 */

const setState = (newState) => {
  // TODO timeouts
  console.log("new", newState);

  // TODO deep merge? https://davidwalsh.name/javascript-deep-merge
  state = Object.assign(state, newState, { info: Object.assign(state.info, newState.info) }, { game: Object.assign(state.game, newState.game)});
  console.log("merged", state);

  if(state.timeoutIsActive){
    // TODO show modal
    renderTimeOutModal(state.timeoutRemainingTime);
  }
  if(state.state === INGAME){
    renderGame(state)
  } else if(state.state === LOBBY){
    renderLobby(state)
  } else if(state.state === FINISHED){
    window.location.href = `${URL_FINISH}?score=${state.game.score}`
  }
  if(state.game.answers.length > MAX_ANSWERS){
    console.error(`ERROR: only ${MAX_ANSWERS} answers allowed (currently: ${state.game.answers.length})`)
  }
};