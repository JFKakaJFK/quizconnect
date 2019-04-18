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

const ROOT = document.getElementById('root');

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
const renderGameInfo = ({ settings }) => {
  return `
    <div class="info box">
      <div class="stat">
        ${settings.questionSets.map(qs => `<h3>${qs}</h3>`).join('')}
        <p>question sets</p>
      </div>
      <div class="stat stat-upper stat-sm">
        <h3>${settings.mode}</h3>
        <p>game mode</p>
      </div>
      <div class="stat stat-upper stat-sm">
        <h3>${settings.difficulty}</h3>
        <p>difficulty</p>
      </div>
      <div class="stat stat-lg">
        <h3>${settings.pin}</h3>
        <p>pin</p>
      </div>
    </div>
  `;
};

const renderPlayer = (player) => {
  return `
    <div class="playerbox py-3 px-3" data-id="${player.id}">
      <div class="playerbox-avatar mx-2 my-2">
          <img src="${player.avatar}" alt="avatar"/>
      </div>

      <div class="playerbox-info mx-2 my-2">
          <div class="playerbox-actions text-right">
            <input type="checkbox" ${player.ready ? 'checked' : ''}>
          </div>

          <div class="playerbox-info-row">
             <div class="playerbox-info-name stat stat-lg stat-left">
              <h3>${player.username}</h3>
              <p>name</p>
            </div>
          </div>

        </div>
    </div>
  `;
};

const renderPlayers = ({ players }) => {
  const playerNodes = ROOT.querySelectorAll('.playerbox');
  console.log(playerNodes);

  return `
    <div class="players">
      ${players.map(p => renderPlayer(p)).join('')}
    </div>
  `;
};

const renderLobby = ( {info} ) => {
  console.info('render Lobby was called');
  console.info(info);

  let elem = ROOT.querySelector('.info');

  console.log(elem);
  // TODO optimize rendering
  // info only is rendered once, but players(currently everytime)?
  if(elem === null){
    ROOT.innerHTML = renderGameInfo(info); // TODO change
  }

  ROOT.innerHTML += renderPlayers(info);
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