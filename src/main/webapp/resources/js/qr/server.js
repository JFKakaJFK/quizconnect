"use strict";

/* ======================== SERVER EVENTS ========================= */

/* SERVER EVENTS */
const READY_UP = "onReadyUp";
const PLAYER_JOIN = "onPlayerJoin";
const PLAYER_LEAVE = "onPlayerLeave";
const GAME_START = "onGameStart";
const GAME_END = "onGameEnd";
const JOKER_USE = "onJokerUse";
const SCORE_CHANGE = "onScoreChange";
const TIMER_SYNC = "onTimerSync";
const TIMEOUT_START = "onTimeoutStart";
const KICK = "onKick";
const ASSIGN_QUESTION = "assignQuestion";
const REMOVE_QUESTION = "removeQuestion";

/**
 * Handles a {@link ServerEvent} by delegating to handler function. Game events have Priority
 *
 * @param response
 */
let messagesHandled = false;
let infoHandled = false;
const handleServerEvent = (response) => {
  // do nothing if event is no action
  if(response === undefined || response.event === null || response.event === "success" || response.event === "error"){
    return;
  }
  if(!infoHandled && response.event !== ROOM_INFO){
    getRoomInfo();
  }
  // console.log(response); // TODO remove
  console.debug(`SERVER: received '${response.event}' event, state is ${state.state === INGAME ? 'INGAME' : state.state === LOBBY ? 'LOBBY' : state.state === JOIN ? 'JOIN' : 'FINISHED'}`);
  if(state.state === INGAME){ // handle game events w/ priority
    switch (response.event){
      case TIMER_SYNC:
        return handleTimerSync(response);
      case JOKER_USE:
        return handleJokerUse(response);
      case ASSIGN_QUESTION:
        return handleAssignQuestion(response);
      case REMOVE_QUESTION:
        return handleRemoveQuestion(response);
      case SCORE_CHANGE:
        return handleScoreChange(response);
      case GAME_END:
        return handleGameEnd(response);
    }
  } else if(state.state === LOBBY) { // handle lobby events only if in lobby
    switch (response.event){
      case GAME_START:
        return handleGameStart(response);
      case READY_UP:
        return handleReadyUp(response);
    }
  }
  switch (response.event){  // handle events where the state doesn't matter
    case TIMEOUT_START:
      return handleTimeoutStart(response);
    case PLAYER_JOIN:
      return handlePlayerJoin(response);
    case KICK:
      return handleKick(response);
    case PLAYER_LEAVE:
      return handlePlayerLeave(response);
    case CHAT_MESSAGE:
      return handleChatMessage(response);
    default:
      if(!infoHandled && response.event === ROOM_INFO){
        infoHandled = true;
        return handleRoomInfo(response);
      } else if(!messagesHandled && response.event === CHAT_MESSAGES) {
        messagesHandled = true;
        return handleChatMessages(response);
      } else if(!messagesHandled){
        getChatMessages();
      } else {
        console.debug(`SERVER: received '${response.event}' event`)
      }
  }
};

/* Event Handlers */

const handleRoomInfo = ({ pin, difficulty, mode, questionSets, score, alivePingInterval, numJokers, num, players, state }) => {
  if(state === INGAME){
    sendAlivePing();
  }
  setState({
    state,
    alivePing: state === INGAME ? setInterval(sendAlivePing, alivePingInterval - 50) : null, // TODO always set aliveping once backend supports it
    gameSessionTimer: setInterval(updateLocalStorage(), 45000),
    info: {
      settings: {
        pin,
        difficulty,
        mode,
        questionSets,
        score,
        alivePingInterval,
        numJokers,
      },
      num,
      players,
    },
    game: {
      score: score,
      jokersLeft: numJokers,
    }
  });
  console.debug(`SERVER: room info updated`);
};

// set the player to ready
const handleReadyUp = ({ playerId }) => {
  setState({
    info: {
      players: state.info.players.map(p => {
        if(p.id === playerId){
          p.ready = true;
        }
        return p;
      }),
    },
  });
  let p = state.info.players.find(p => p.id === playerId);
  if(p !== undefined) showChatMessage(`${p.username} is ready`);
  console.debug(`SERVER: player ${playerId} is ready`);
};

// on join add player to state.info.players
const handlePlayerJoin = ({ player }) => {
  setState({
    info: {
      players: state.info.players.concat([player])
    },
  });
  showChatMessage(`${player.username} joined the game`);
  console.debug(`SERVER: player ${player.id} joined`);
};

// remove player from state.info.players
const handlePlayerLeave = ({ playerId }) => {
  setState({
    info: {
      players: state.info.players.filter(p => p.id !== playerId),
    }
  });
  let p = state.info.players.find(p => p.id === playerId);
  if(p !== undefined) showChatMessage(`${p.username} left the game`);
  console.debug(`SERVER: player ${playerId} left`)
};

// set state.state = INGAME
const handleGameStart = (event) => {
  sendAlivePing();
  setState({
    alivePing: setInterval(sendAlivePing, state.info.settings.alivePingInterval - 50), // account for latency
    state: INGAME,
  });
  // TODO or register every 1s & unregister on activity -> only 1 call of cancelTimeout/s
  /*
  document.addEventListener('click', cancelTimeout);
  document.addEventListener('touchstart', cancelTimeout);
  document.addEventListener('mousemove', cancelTimeout);
  */
  console.debug(`SERVER: game started`)
};

// set state.state = FINISH
const handleGameEnd = (event) => {
  if(state.alivePing !== null){
    clearInterval(state.alivePing);
    state.alivePing = null;
  }
  if(state.gameSessionTimer !== null){
    clearInterval(state.gameSessionTimer);
    state.gameSessionTimer = null;
  }
  disconnect();
  clearLocalStorage();
  setState({
    state: FINISHED,
  });
  showChatMessage(`Game ended.`);
  console.debug(`SERVER: game ended`)
};

// update jokersLeft
const handleJokerUse = ({ remaining }) => {
  setState({
    game: {
      jokersLeft: remaining,
    }
  });
  showChatMessage(`A joker was used`);
  console.debug(`SERVER: joker was used (${remaining} jokers remaining)`)
};

// update score
const handleScoreChange = ({ newScore }) => {
  setState({
    game: {
      score: newScore,
    }
  });
  console.debug(`SERVER: score changed to ${newScore}`)
};

const handleTimerSync = ({ questionId, remaining }) => {
  if(state.game.question == null){
    return;
  }
  if(questionId === state.game.question.questionId){
    setState({
      game: {
        question: Object.assign(state.game.question, { remaining, })
      }
    })
  }
  console.debug(`SERVER: timer sync, question ${questionId} has ${remaining/1000}s left`);
};

const handleTimeoutStart = ({ playerId, remaining }) => {
  if(state.id !== playerId){
    return;
  }
  // listen for any activity
  document.addEventListener('click', cancelTimeout);
  document.addEventListener('touchstart', cancelTimeout);
  document.addEventListener('mousemove', cancelTimeout);
  setState({
    timeoutIsActive: true,
    timeoutRemainingTime: remaining,
  });
  console.debug(`SERVER: timeout started, ${remaining / 1000}s left`)
};

const handleKick = ({ playerId }) => {
  if(playerId === state.id){
    disconnect();
    clearLocalStorage();
    window.location.href = URL_KICKED;
  }
  let p = state.info.players.find(p => p.id === playerId);
  if(p !== undefined) showChatMessage(`${p.username} was kicked`);
  console.debug(`SERVER: player ${playerId} was kicked`)
};

/**
 * Sets the game state according to the question assignment. If question & answer are assigned to the
 * same user, the state is updated multiple times(Given the chances of that for sufficiently many players
 * this should be more efficient).
 *
 * @param questionId
 * @param type
 * @param question
 * @param playerId
 * @param timeRemaining
 * @param answers
 */
const handleAssignQuestion = ({ questionId, type, question, playerId, timeRemaining, answers }) => {
  // check if player is current player
  if(playerId === state.id){
    if(state.game.question !== null){
      console.error('Cannot display multiple questions');
    }
    setState({
      game: {
        question: {
          questionId,
          type,
          question,
          remaining: timeRemaining,
        }
      }
    });
    console.debug(`SERVER: new question assigned to this player`);
  }
  // if new answers for player, add answers
  if(answers.find(a => a.playerId === state.id) !== undefined){
    setState({
      game: {
        answers: state.game.answers.concat(
          answers.filter(a => a.playerId === state.id)
            .map(a => ({
              questionId,
              type,
              answerId: a.answerId,
              answer: a.answer,
            }))
        ),
      }
    });
    console.debug(`SERVER: new answers assigned to this player`);
  }
};

const handleRemoveQuestion = ({ questionId }) => {
  // has this player the question?
  if(state.game.question !== null && questionId === state.game.question.questionId){
    setState({
      game: {
        question: null,
      }
    })
  }
  // does the player have an answer/answers to the question? if so remove them
  if(state.game.answers.find(a => a.questionId === questionId) !== undefined){
    setState({
      game: {
        answers: state.game.answers.filter(a => a.questionId !== questionId),
      }
    })
  }
  console.debug(`SERVER: question ${questionId} was removed`);
};

const handleChatMessage = ({ message }) => {
  setState({
    messages: state.messages.concat([message]),
  });
  console.debug(`SERVER: received '${message.message}' from '${message.from}'`)
};

const handleChatMessages = ({ messages }) => {
  setState({
    messages,
  });
  console.debug(`SERVER: received all chat messages`)
};