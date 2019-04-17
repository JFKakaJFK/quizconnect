"use strict";

/* ======================== RENDER FUNCTIONS ========================= */

/* TODO
 *
 *  - render Lobby
 *    - render game info
 *    - render players
 *      - render player
 *      - remove player
 *  - render game
 *    - render question
 *    - render timer
 *    - render answers
 *      - render answer
 *      - remove answer
 */

/**
 * Takes in a JS Object with following attributes
 *
 * - pin
 * - difficulty
 * - mode
 * - questionSets
 * - numJokers
 *
 * @param info
 */
const renderGameInfo = (info) => {

};

const renderLobby = ( {info} ) => {
  const body = document.querySelector('body');
  let elem = document.querySelector('#info');
  console.log(elem)
  if(elem === null){
    elem = document.createElement('div');
    elem.setAttribute('id', 'info');

  }

  elem.innerHTML = `
    <h2>Lobby</h2>
    <p>Pin: ${info.settings.pin}</p>
    <p>Difficulty: ${info.settings.difficulty}</p>
    <p>Mode: ${info.settings.mode}</p>
    <p>Question Sets: ${info.settings.questionSets}</p>
    <p>Number of jokers: ${info.settings.numJokers}</p>
  `;
  body.appendChild(elem);
};

const renderGame = ( {game} ) => {
  const body = document.querySelector('body');
  let div = document.createElement('div');
  div.innerHTML = `
    <h2>Ingame</h2>
    <p>Score: ${game.score}</p>
  `;
  body.appendChild(div);
};