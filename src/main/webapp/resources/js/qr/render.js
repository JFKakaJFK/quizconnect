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

const renderPlayer = (player) => { // TODO ready stuff (only data-ready & class ready stays)
  return `
    <div class="playerbox py-3 px-3" data-id="${player.id}">
      <div class="playerbox-avatar mx-2 my-2">
          <img src="${player.avatar}" alt="avatar"/>
      </div>

      <div class="playerbox-info mx-2 my-2">
          <div class="playerbox-actions text-right">
            ${player.id === state.id ?
              `<input class="ready" data-ready="${player.ready}" type="checkbox" ${player.ready ? 'checked' : ''}>` 
              : 
              `<span class="ready" data-ready="${player.ready}">${player.ready ? 'ready' : 'not ready'}</span>`
            }
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

const renderPlayers = ( parent, { players }) => {
  if(parent === null){
    return;
  }
  const playerNodes = parent.querySelectorAll('.playerbox');
  const copy = [ ...players ];
  console.log(playerNodes);

  if(playerNodes.length > 0){
    playerNodes.forEach(node => {

      let id = parseInt(node.getAttribute('data-id'))
      let player = copy.find(p => p.id === id);
      console.log(player);

      // check for all in playerNodes if they are in players (n^2)
      if(player){
        // if yes, check if anything to rerender
        let readyNode = node.querySelector('.ready');
        let ready = readyNode.getAttribute('data-ready') === 'true';
        if(!ready && player.ready){
          // rerender ready
          readyNode.innerHTML = `<span class=".ready" data-ready="true">ready</span>`; // TODO
        }
      } else { // if not, delete
        parent.removeChild(node);
      }

      // remove player from copy
      copy.filter(p => p.id !== id);
    });
  }

  // add new players
  copy.forEach(p => parent.innerHTML += renderPlayer(p));
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

  elem = ROOT.querySelector('.players');
  if(elem === null){
    ROOT.innerHTML += `<div class="players"></div>`; // TODO change
  }

  renderPlayers(elem, info);
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