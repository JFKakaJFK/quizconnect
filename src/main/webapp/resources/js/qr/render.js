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
// TODO use this for modal https://stackoverflow.com/a/50523971/6244663
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

// TODO structure, rerender only on change
const renderScore = (parent, score) => {
  parent.innerHTML = score;
};

// TODO other types for reverse mode
// TODO progress bar? or SVG + anime.js??
// TODO save initial question time? for progress %
const renderQuestion = (parent, { questionId, type, question, remaining }) => {
  let q = parent.querySelector('#question');
  let qt = parent.querySelector('#questionTime span');
  if(q !== null){
    console.log(q.value)
    console.log(q.innerHTML)
    console.log(q.innerText)
    if(q.value === question){
      let total = parseInt(qt.getAttribute('data-total'));
      qt.style.width = `${remaining / total}%`;
    } else {
      q.innerHTML = question;
      qt.setAttribute('data-total', remaining);
      qt.style.width = '100%';
    }
    return;
  }
  parent.innerHTML = `
    <h2 id="question box">${question}</h2>
    <div id="questionTime"><span data-total="${remaining}" style="width: 100%"></span></div>
  `;
};

const renderQuestionPlaceholder = (parent) => {
  if(ROOT.querySelector('#question') !== null && ROOT.querySelector('#question').value === '') return;
  parent.innerHTML = `
    <h2 id="question box"></h2>
    <div id="questionTime"><span></span></div>
  `;
};

const renderGenericAnswer = ({ classes, content, answerId, questionId} ) => { // TODO parseInt necessary?
  return `
    <div class="answer box ${classes}" data-questionId="${questionId}" data-answerId="${answerId}" onclick="answerQuestion(${questionId}, ${answerId})">
      ${content}
    </div>
  `;
};

// TODO depending on text length, add different size style classes
// using these classes + displey: grid + grid-auto-flow do different layouts
const renderTextAnswer = (answer) => {
  return {
    content: `<p>${answer}</p>`,
    classes: 'answer-text',
  };
};

const renderPictureAnswer = (answer) => {
  return {
    content: `<img src="${answer}" alt="answer image" />`,
    classes: 'answer-img',
  };
};

const renderAnswer = ({ questionId, type, answerId, answer }) => {
  switch (type){
    case ANSWERTYPE_TEXT:
      return renderGenericAnswer({ ...renderTextAnswer(answer), questionId, answerId });
    case ANSWERTYPE_PICTURE:
      return renderGenericAnswer({ ...renderPictureAnswer(answer), questionId, answerId });
    default:
      return renderGenericAnswer({ classes: 'answer-default', content: answer, questionId, answerId });
  }
};

const renderAnswers = ( parent, { answers }) => {
  // TODO render placeholders?
  if(parent === null){
    return;
  }
  const answerNodes = parent.querySelectorAll('.answer');
  let copy = [ ...answers ];

  if(answerNodes.length > 0){
    // remove answer if not in state
    answerNodes.forEach(node => {

      let questionId = parseInt(node.getAttribute('data-questionId'));
      let answerId = parseInt(node.getAttribute('data-answerId'));
      let answer = copy.find(a => a.questionId === questionId && a.answerId === answerId);

      // delete if not in answers
      if(answer === undefined){
        parent.removeChild(node);
      }

      // remove answer from copy (make search space smaller)
      copy = copy.filter(a => a.questionId !== questionId || a.answerId !== answerId);
    });
  }

  // add new answers
  if(copy.length > 0){
    copy.forEach(a => parent.innerHTML += renderAnswer(a));
  }
};

// TODO structure, rerender only on change
const renderJoker = (parent, jokers) => {
  if(jokers === 0){
    parent.removeEventListener('click', useJoker);
  }
  parent.innerHTML = jokers;
  if(jokers > 0){
    parent.addEventListener('click', useJoker);
  }
};

/**
 * Renders the game state
 *
 * @param game
 */
const renderGame = ( {game} ) => {
  // if LOBBY was rendered before, clear the ROOT node and add INGAME containers
  if(ROOT.querySelector('.info') !== null || ROOT.querySelector('.players') !== null){
    clearScreen();
    let score = document.createElement('div');
    score.classList.add('score');
    ROOT.appendChild(score);

    let question = document.createElement('div');
    question.classList.add('question');
    question.classList.add('box');
    ROOT.appendChild(question);

    let answers = document.createElement('div');
    answers.classList.add('answers');
    ROOT.appendChild(answers);

    let joker = document.createElement('div');
    joker.classList.add('joker');
    joker.classList.add('box');
    ROOT.appendChild(joker);
  }

  renderScore(ROOT.querySelector('.score'), game.score);

  if(game.question != null){
    renderQuestion(ROOT.querySelector('.question'), game.question);
  } else {
    renderQuestionPlaceholder(ROOT.querySelector('.question'));
  }

  if(state.game.answers.length > MAX_ANSWERS){
    console.error(`ERROR: only ${MAX_ANSWERS} answers allowed (currently: ${state.game.answers.length})`)
  }
  renderAnswers(ROOT.querySelector('.answers'), game);

  renderJoker(ROOT.querySelector('.joker'), game.jokersLeft);
};

const render = (state) => {
  if(state.timeoutIsActive){
    // TODO show modal
    renderTimeOutModal(state.timeoutRemainingTime);
  }
  if(state.state === INGAME){
    renderGame(state)
  } else if(state.state === LOBBY){
    renderLobby(state)
  } else if(state.state === FINISHED){
    // TODO maybe not per GET Param (or player can view ending screen for any score...)
    window.location.href = `${URL_FINISH}?score=${state.game.score}`
  }
};