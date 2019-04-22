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
const TIMEOUT_MODAL = $('#timeout'); // TODO bootstrap only works w/ jQuery Selector
// document.getElementById('timeout');
const TIMEOUT_COUNTER = document.getElementById('timeoutRemainingTime');
//TIMEOUT_MODAL.querySelector('#timeoutRemainingTime');


/**
 * Clears all elements within the ROOT
 *
 */
const clearScreen = () => {
  let lc = ROOT.lastChild;
  while(lc){
    ROOT.removeChild(lc);
    lc = ROOT.lastChild;
  }
};

/**
 * Activates timeout modal
 * @param remaining
 */
// TODO fix
const renderTimeOutModal = (remaining) => {
  console.log(TIMEOUT_MODAL);
  TIMEOUT_MODAL.modal('show'); // show modal
  TIMEOUT_COUNTER.innerHTML = `${(remaining / 1000).toFixed(1)}`;
  state.timeoutTimer = setInterval(() => {
    if(parseFloat(TIMEOUT_COUNTER.innerHTML) > 0.0){
      TIMEOUT_COUNTER.innerHTML = `${(parseFloat(TIMEOUT_COUNTER.innerHTML) - 0.1).toFixed(1)}`;
    }
  }, 100);
  setTimeout(() => TIMEOUT_MODAL.modal('hide'), 10000); // TODO?
};

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
      <div class="stat stat-lg pin">
        <h3>${settings.pin}${false ? 'share pin here' : ''}</h3>
        <p>pin</p>
      </div>
    </div>
  `;
};

/**
 * Renders a player
 *
 * @param player
 * @returns {string}
 */
const renderPlayer = (player) => { // TODO ready stuff (only data-ready & class ready stays)
  return `
    <div class="playerbox py-3 px-3" data-id="${player.id}">
      <div class="playerbox-avatar mx-2 my-2">
          <img src="${player.avatar}" alt="avatar"/>
      </div>

      <div class="playerbox-info mx-2 my-2">
          <div class="playerbox-actions text-right">
            ${player.id === state.id ?
              `<div class="ready" data-ready="${player.ready}" ${player.ready ? '' : 'onclick="readyUp()"'}>${player.ready ? 'ready' : 'ready up'}</div>` 
              : 
              `<div class="ready" data-ready="${player.ready}">${player.ready ? 'ready' : 'not ready'}</div>`
            }
          </div>

          <div class="playerbox-info-row">
             <div class="playerbox-info-name stat stat-lg stat-left">
              <h3>${player.username}</h3>
              <p>username</p>
            </div>
          </div>

        </div>
    </div>
  `;
};

/**
 * Renders all players in the lobby
 *
 * @param parent
 * @param players
 */
const renderPlayers = ( parent, { players }) => {
  if(parent === null || players.length === 0){
    return;
  }
  const playerNodes = parent.querySelectorAll('.playerbox');
  let copy = [ ...players ];

  if(playerNodes.length > 0){
    playerNodes.forEach(node => {

      let id = parseInt(node.getAttribute('data-id'));
      let player = copy.find(p => p.id === id);

      // check for all in playerNodes if they are in players
      if(player){
        // if yes, check if anything to rerender
        let readyNode = node.querySelector('.ready');
        let ready = readyNode.getAttribute('data-ready') === 'true';

        if(!ready && player.ready){
          // rerender ready
          readyNode.innerHTML = `<div class="ready" data-ready="true">ready rerendered</div>`; // TODO
        }
      } else { // if not in players, delete // TODO test
        parent.removeChild(node);
      }

      // remove player from copy (make search space smaller)
      copy = copy.filter(p => p.id !== id);
    });
  }

  // add new players
  if(copy.length > 0){
    copy.forEach(p => parent.innerHTML += renderPlayer(p));
  }
};

/**
 * Renders the lobby state
 *
 * @param info
 */
const renderLobby = ( {info} ) => {
  console.info('render Lobby was called');
  console.info(info);

  let elem = ROOT.querySelector('.info');

  // info only is rendered once
  if(elem === null){
    ROOT.innerHTML = renderGameInfo(info); // TODO change
  }

  if(ROOT.querySelector('.players') === null){
    ROOT.innerHTML += `<div class="players"></div>`; // TODO change
  }
  let players = ROOT.querySelector('.players');

  // renders the players
  renderPlayers(players, info);
};


const renderQuestion = () => {

};

const renderAnswer = () => {

};

const renderAnswers = () => {

};



/**
 * Renders the game state
 *
 * @param game
 */
const renderGame = ( {game} ) => {
  if(ROOT.querySelector('.info') !== null || ROOT.querySelector('.players') !== null){
    clearScreen();
    let question = document.createElement('div');
    question.classList.add('question');
    ROOT.appendChild(question);
    let answers = document.createElement('div');
    answers.classList.add('answers');
    ROOT.appendChild(answers);
  }

  ROOT.innerHTML = JSON.stringify(game);
};