"use strict";

/* ======================== RENDER FUNCTIONS ========================= */

const ROOT = document.getElementById('root');
const TIMEOUT_MODAL = $('#timeout'); // TODO bootstrap only works w/ jQuery Selector
// document.getElementById('timeout');
const TIMEOUT_COUNTER = document.getElementById('timeoutRemainingTime');
//TIMEOUT_MODAL.querySelector('#timeoutRemainingTime');
const SHARE_MODAL = $('#sharePin');

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
// TODO use this for modal https://stackoverflow.com/a/50523971/6244663
const renderTimeOutModal = (remaining) => {
  // console.log(TIMEOUT_MODAL);
  TIMEOUT_MODAL.modal('show'); // show modal
  TIMEOUT_COUNTER.innerHTML = `${(remaining / 1000).toFixed(1)}`;
  state.timeoutTimer = setInterval(() => {
    if(parseFloat(TIMEOUT_COUNTER.innerHTML) > 0.0){
      TIMEOUT_COUNTER.innerHTML = `${(parseFloat(TIMEOUT_COUNTER.innerHTML) - 0.1).toFixed(1)}`;
    }
  }, 100);
  setTimeout(() => TIMEOUT_MODAL.modal('hide'), 10000); // TODO?
};

const copyToClipboard = str => {
  const el = document.createElement('textarea');  // Create a <textarea> element
  el.value = str;                                 // Set its value to the string that you want copied
  el.setAttribute('readonly', '');                // Make it readonly to be tamper-proof
  el.style.position = 'absolute';
  el.style.left = '-9999px';                      // Move outside the screen to make it invisible
  document.body.appendChild(el);                  // Append the <textarea> element to the HTML document
  const selected =
    document.getSelection().rangeCount > 0        // Check if there is any content selected previously
      ? document.getSelection().getRangeAt(0)     // Store selection if found
      : false;                                    // Mark as false to know no selection existed before
  el.select();                                    // Select the <textarea> content
  document.execCommand('copy');                   // Copy - only works as a result of a user action (e.g. click events)
  document.body.removeChild(el);                  // Remove the <textarea> element
  if (selected) {                                 // If a selection existed before copying
    document.getSelection().removeAllRanges();    // Unselect everything on the HTML document
    document.getSelection().addRange(selected);   // Restore the original selection
  }
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
  const sharePin = document.getElementById('sharePin');

  const sp = sharePin.querySelector('#share_pin');
  sp.innerHTML = settings.pin.toString().padStart(6, '0');

  const wa = sharePin.querySelector('#share_whatsapp');
  wa.href = SHARE_WHATSAPP(SHARE_PIN_MESSAGE(settings.pin.toString().padStart(6, '0')));

  const twitter = sharePin.querySelector('#share_twitter');
  twitter.href = SHARE_TWITTER(SHARE_PIN_MESSAGE(settings.pin.toString().padStart(6, '0')));

  const fb = sharePin.querySelector('#share_fb');
  fb.href = SHARE_FACEBOOK(SHARE_JOIN_URL(settings.pin.toString().padStart(6, '0')));
  /* copy to clipboard */

  // TODO share icon
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
        <h3>${settings.pin.toString().padStart(6, '0')}</h3> 
        <p>pin <a href="#" data-toggle="modal" data-target="#sharePin"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24"><path class="heroicon-ui" d="M19 10h2a1 1 0 0 1 0 2h-2v2a1 1 0 0 1-2 0v-2h-2a1 1 0 0 1 0-2h2V8a1 1 0 0 1 2 0v2zM9 12A5 5 0 1 1 9 2a5 5 0 0 1 0 10zm0-2a3 3 0 1 0 0-6 3 3 0 0 0 0 6zm8 11a1 1 0 0 1-2 0v-2a3 3 0 0 0-3-3H7a3 3 0 0 0-3 3v2a1 1 0 0 1-2 0v-2a5 5 0 0 1 5-5h5a5 5 0 0 1 5 5v2z"/></svg></a></p>
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

  let lastReadyUp = copy.filter(p => !p.ready).length === 1;

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
          readyNode.setAttribute('data-ready', true);
          readyNode.innerHTML = 'ready rerendered';
        } else if(!player.ready && player.id === state.id){
          if(lastReadyUp){
            readyNode.setAttribute('data-toggle', 'modal');
            readyNode.setAttribute('data-target', '#confirmReady');
            readyNode.setAttribute('onclick', '{}');
            readyNode.innerHTML = 'last unready';
          } else if(!lastReadyUp && readyNode.hasAttribute('data-toggle')){
            readyNode.removeAttribute('data-toggle');
            readyNode.removeAttribute('data-target');
            readyNode.setAttribute('onclick', 'readyUp()');
            readyNode.innerHTML = 'ready up';
          }
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
    // TODO handle ready up if only one player in lobby (show lastready | no ready up at all )??
  }
};

const countdown = (remaining, nodeId) => {
  const node = document.getElementById(nodeId);
  if(remaining > 0){
    console.debug(`COUNTDOWN: ${remaining}`);
    if(node !== null) node.innerHTML = remaining.toString();
    showChatMessage(`Game will start in ${remaining}s`);
    setTimeout(() => countdown(remaining - 1, nodeId), 1000)
  } else {
    if(node !== null) node.innerHTML = '0';
  }
};

/**
 * Renders the lobby state
 *
 * @param info
 */
const renderLobby = ( {info} ) => {

  if(ROOT.getAttribute('data-state') == null || parseInt(ROOT.getAttribute('data-state')) !== LOBBY) {
    ROOT.setAttribute('data-state', LOBBY.toString());
    clearScreen();
  }
  console.debug('RENDER: rendering lobby')

  let allReady = info.players.filter(p => !p.ready).length === 0 && info.players.length > 0;
  if(allReady){
    if(document.getElementById('countdown') === null){
      clearScreen();
      /*
      ROOT.innerHTML = `
        <div>
          <h1>Game will start in <span id="countdown">5</span>s</h1>
        </div>
      `;
      countdown(5, 'countdown');
      */
      ROOT.innerHTML = `
        <div class="countdown" id="counter"><div>
      `;
      let cd = new Countdown({
        selector: '#counter',
        values: 6,
      });
      cd.start();
    }
    return;
  }

  let elem = ROOT.querySelector('.info');

  // info only is rendered once
  if(elem === null){
    ROOT.innerHTML = renderGameInfo(info);
  }

  if(ROOT.querySelector('.players') === null){
    ROOT.innerHTML += `<div class="players"></div>`;
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
    // console.log(q.innerText)
    if(q.innerText === question){
      let total = parseInt(qt.getAttribute('data-total'));
      qt.style.width = `${(remaining / total) * 100}%`;
    } else {
      if(type === ANSWERTYPE_MATH){
        let elem = document.createElement('p');
        katex.render(question, elem, {
          throwOnError: false,
        });
        q.innerHTML = elem.innerHTML;
      } else {
        q.innerHTML = question;
      }
      qt.setAttribute('data-total', remaining);
      qt.style.width = '100%';
    }
    return;
  }
  parent.innerHTML = `
    <h2 id="question">${question}</h2>
    <div id="questionTime"><span data-total="${remaining}" style="width: 100%"></span></div>
  `;
};

const renderQuestionPlaceholder = (parent) => {
  if(ROOT.querySelector('#question') !== null && ROOT.querySelector('#question').value === '') return;
  parent.innerHTML = `
    <h2 id="question"></h2>
    <div id="questionTime"><span></span></div>
  `;
};

const renderAnswerPlaceholder = () => {
  return `
    <div class="answer box answer-placeholder"></div>
  `;
}

const renderGenericAnswer = ({ classes, content, answerId, questionId} ) => {
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

const renderMathAnswer = (answer) => {
  let elem = document.createElement('p');
  katex.render(answer, elem, {
    throwOnError: false,
  });
  return {
    content: `<p>${elem.innerHTML}</p>`,
    classes: 'answer-math',
  };
};

const renderAnswer = ({ questionId, type, answerId, answer }) => {
  switch (type){
    case ANSWERTYPE_TEXT:
      return renderGenericAnswer({ ...renderTextAnswer(answer), questionId, answerId });
    case ANSWERTYPE_PICTURE:
      return renderGenericAnswer({ ...renderPictureAnswer(answer), questionId, answerId });
    case ANSWERTYPE_MATH:
      return renderGenericAnswer({ ...renderMathAnswer(answer), questionId, answerId});
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
      if(!node.classList.contains('answer-placeholder')){

        let questionId = parseInt(node.getAttribute('data-questionId'));
        let answerId = parseInt(node.getAttribute('data-answerId'));
        let answer = copy.find(a => a.questionId === questionId && a.answerId === answerId);

        // delete if not in answers
        if(answer === undefined){
          // change to default // TODO
          let placeholder = document.createElement('div');
          node.parentNode.replaceChild(placeholder, node); // removes event listeners
          placeholder.classList.add('answer-placeholder');
          placeholder.classList.add('answer');
          placeholder.classList.add('box');
          placeholder.innerHTML = `<p></p>`
          // parent.removeChild(node);
        }

        // remove answer from copy (make search space smaller)
        copy = copy.filter(a => a.questionId !== questionId || a.answerId !== answerId);
      }
    });
  }


  const emptyNodes = parent.querySelectorAll('.answer-placeholder');
  // add new answers
  if(copy.length > 0){
    // first populate placeholders
    for (let node of emptyNodes) {
      if(copy.length > 0){
        let answer = copy.pop();

        let pseudo = document.createElement('div');
        pseudo.innerHTML = renderAnswer(answer);

        node.parentNode.replaceChild(pseudo.firstElementChild, node);
      } else {
        break;
      }
    }

    // then create new answers
    if(copy.length > 0){
      copy.forEach(a => parent.innerHTML += renderAnswer(a));
    }

    // copy.forEach(a => parent.innerHTML += renderAnswer(a));
  }

  // delete all empty nodes at end of parent (only placeholders in between) // TODO
  let lastNode = parent.lastElementChild;
  while(lastNode !== null && lastNode !== undefined && lastNode.classList.contains('answer-placeholder')){
    parent.removeChild(lastNode);
    lastNode = parent.lastElementChild;
  }
};

// TODO structure, rerender only on change
const renderJoker = (parent, jokers) => {
  if(jokers === 0){
    parent.removeEventListener('click', useJoker);
  }
  parent.innerHTML = jokers;
  if(jokers > 0){
    parent.addEventListener('click', useJoker); // TODO use timeout after click to prevent joker spamming
  }
};

/**
 * Renders the game state
 *
 * @param game
 */
const renderGame = ( {game} ) => {
  console.debug('RENDER: rendering game');
  // if LOBBY was rendered before, clear the ROOT node and add INGAME containers
  if(ROOT.getAttribute('data-state') == null || parseInt(ROOT.getAttribute('data-state')) !== INGAME) {
    ROOT.setAttribute('data-state', INGAME.toString());
    clearScreen();

    let score = document.createElement('div');
    score.classList.add('score');
    score.classList.add('box');
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

const r = (min, max) => {
  return Math.random() * (max - min) + min;
};

const confettiShower = (times, particleCount = r(50, 100)) => {
  if(times <= 0){
    return;
  }
  confetti({
    angle: 270,
    spread: r(50, 100),
    particleCount: particleCount,
    origin: {
      y: -0.6
    }
  });
  setTimeout(() => confettiShower(times - 1), 300);
};

const confettiCannon = (times, particleCount = r(50, 100)) => {
  if(times <= 0){
    return;
  }
  confetti({
    angle: r(55, 125),
    spread: r(50, 70),
    particleCount: particleCount,
    origin: {
      y: 0.6
    }
  });
  setTimeout(() => confettiCannon(times - 1), 300);
};

const fireworks = (end, particleCount = r(50, 100)) => {
  if(Date.now() > end){
    return;
  }
  confetti({
    startVelocity: 30,
    spread: 360,
    ticks: 60,
    particleCount: particleCount,
    origin: {
      x: Math.random(),
      // since they fall down, start a bit higher than random
      y: Math.random() - 0.2
    }
  });
  setTimeout(() => fireworks(end), 300);
};

const renderGameEnd = ({ game, highScore}) => {
  if(ROOT.getAttribute('data-state') == null || parseInt(ROOT.getAttribute('data-state')) !== FINISHED) {
    ROOT.setAttribute('data-state', FINISHED.toString());
    clearScreen();

    let score = document.createElement('div');
    score.classList.add('score-container');
    ROOT.appendChild(score);
    score.innerHTML = `
    <div class="text-center">
      ${game.score > highScore ? `<h2>New Highscore!</h2>` : ''}
      <div class="stat stat-lg">
          <h3>${game.score}</h3>
          <p>Score</p>
      </div>
      <a href="/player/home.xhtml" class="btn btn-primary">Home</a>
    </div>
    <div class="share-links">
        <a class="share-link share-whatsapp" href="${SHARE_WHATSAPP(`Hey i've just scored ${game.score} points playing QuizConnect`)}" ping="ibiza.fpoe.at/sellout" rel="noopener nofollow noreferrer" target="_blank">
            <?xml version="1.0" encoding="utf-8"?>
            <svg width="1792" height="1792" viewBox="0 0 1792 1792" xmlns="http://www.w3.org/2000/svg"><path d="M1113 974q13 0 97.5 44t89.5 53q2 5 2 15 0 33-17 76-16 39-71 65.5t-102 26.5q-57 0-190-62-98-45-170-118t-148-185q-72-107-71-194v-8q3-91 74-158 24-22 52-22 6 0 18 1.5t19 1.5q19 0 26.5 6.5t15.5 27.5q8 20 33 88t25 75q0 21-34.5 57.5t-34.5 46.5q0 7 5 15 34 73 102 137 56 53 151 101 12 7 22 7 15 0 54-48.5t52-48.5zm-203 530q127 0 243.5-50t200.5-134 134-200.5 50-243.5-50-243.5-134-200.5-200.5-134-243.5-50-243.5 50-200.5 134-134 200.5-50 243.5q0 203 120 368l-79 233 242-77q158 104 345 104zm0-1382q153 0 292.5 60t240.5 161 161 240.5 60 292.5-60 292.5-161 240.5-240.5 161-292.5 60q-195 0-365-94l-417 134 136-405q-108-178-108-389 0-153 60-292.5t161-240.5 240.5-161 292.5-60z" /></svg>
        </a>
        <a href="${SHARE_TWITTER(`Hey i've just scored ${game.score} points playing QuizConnect`)}" class="share-link share-twitter" ping="ibiza.fpoe.at/sellout" rel="noopener nofollow noreferrer" target="_blank">
              <?xml version="1.0" encoding="utf-8"?>
              <svg width="1792" height="1792" viewBox="0 0 1792 1792" xmlns="http://www.w3.org/2000/svg"><path d="M1684 408q-67 98-162 167 1 14 1 42 0 130-38 259.5t-115.5 248.5-184.5 210.5-258 146-323 54.5q-271 0-496-145 35 4 78 4 225 0 401-138-105-2-188-64.5t-114-159.5q33 5 61 5 43 0 85-11-112-23-185.5-111.5t-73.5-205.5v-4q68 38 146 41-66-44-105-115t-39-154q0-88 44-163 121 149 294.5 238.5t371.5 99.5q-8-38-8-74 0-134 94.5-228.5t228.5-94.5q140 0 236 102 109-21 205-78-37 115-142 178 93-10 186-50z" /></svg>
          </a>
          <a href="${SHARE_FACEBOOK('http://www.quizconnect.rocks')}" class="share-link  share-facebook" ping="ibiza.fpoe.at/sellout" rel="noopener nofollow noreferrer" target="_blank">
              <?xml version="1.0" encoding="utf-8"?>
              <svg width="1792" height="1792" viewBox="0 0 1792 1792" xmlns="http://www.w3.org/2000/svg"><path d="M1343 12v264h-157q-86 0-116 36t-30 108v189h293l-39 296h-254v759h-306v-759h-255v-296h255v-218q0-186 104-288.5t277-102.5q147 0 228 12z" /></svg>
          </a>
      </div>
`;

    if(game.score > highScore) {
      fireworks(Date.now() + (10 * 1000));
      confettiCannon(Math.max(3, game.score / 150));
      confettiShower(Math.max(3, game.score / 150));
    } else if(game.score > 1500){ // somewhat good
      fireworks(Date.now() + (10 * 1000)); // for 10s
    } else if (game.score > 0){
      confettiCannon(Math.max(3, game.score / 150)); // 3 - 10 times, depending on score
    } else {
      confettiShower(1, 5);
    }
  }
};

const renderChatMessage = ({ message, from, playerId, id, timestamp }) => {
  let ts = new Date(timestamp);
  let escapedMessage = escapeHTML(message);
  let escapedFrom = escapeHTML(from);
  return `<div class="chat chat-message ${state.id === playerId ? 'chat-outgoing' : from === INFO ? 'chat-info' : ''}" data-id="${id}">
    <span class="chat chat-timestamp">[${ts.getHours().toString().padStart(2, '0')}:${ts.getMinutes().toString().padStart(2, '0')}:${ts.getSeconds().toString().padStart(2, '0')}]</span>
    ${from === INFO ? '' : `<span class="chat chat-from">${escapedFrom}:</span>`}
    ${escapedMessage}
  </div>`
};

const renderChatMessages = ({ messages }) => {
  let copy = [...messages];

  const container = document.getElementById('messages');
  /*
  const messageNodes = container.querySelectorAll('.message');


  // remove already rendered nodes from copy
  messageNodes.forEach(node => {
    let nodeId = parseInt(node.getAttribute('data-id'));
    if(copy.find(msg => msg.id === nodeId) === undefined){ // removes duplicates
      container.removeChild(node);
    }

    copy = copy.filter(msg => msg.id !== nodeId);
  });
  */
  if(copy.length > 0){
    copy.forEach(m => {
      container.innerHTML += renderChatMessage(m);
    })
  }
  state.messages = []; // remove all rendered messages from state without triggering a rerender
};

const clearMessages = () => {
  const MESSAGES = document.getElementById('messages');
  let lc = MESSAGES.lastChild;
  while(lc){
    MESSAGES.removeChild(lc);
    lc = MESSAGES.lastChild;
  }
};


const render = (state) => {
  const container = document.getElementById('messages');
  const start = performance.now();

  if(state.timeoutIsActive){
    renderTimeOutModal(state.timeoutRemainingTime);
  }
  if(state.state === INGAME){
    renderGame(state);
    renderChatMessages(state);
  } else if(state.state === LOBBY){
    renderLobby(state);
    renderChatMessages(state);
  } else if(state.state === FINISHED){
    renderGameEnd(state);
    clearMessages();
  }

  const time = performance.now() - start;
  console.debug(`RENDER: rendering took ${time.toFixed(3)}ms`)
};