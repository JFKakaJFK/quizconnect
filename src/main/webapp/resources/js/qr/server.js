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
let playersHandled = false;
let infoHandled = false;
const handleServerEvent = (response) => {
  // do nothing if event is no action
  if(response === undefined || response.event === "success" || response.event === "error"){
    return;
  }
  console.log(response); // TODO remove
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
      case PLAYER_JOIN:
        return handlePlayerJoin(response);
    }
  }
  switch (response.event){  // handle events where the state doesn't matter
    case TIMEOUT_START: // TODO only ingame??
      return handleTimeoutStart(response);
    case KICK:
      return handleKick(response);
    case PLAYER_LEAVE: // TODO only ingame?
      return handlePlayerLeave(response);
    default:
      /*
      if(!playersHandled && response.event === ROOM_PLAYERS){
        playersHandled = true;
        return handleRoomPlayers(response)
      } else if(!infoHandled && response.event === GAME_INFO){
        infoHandled = true;
        return handleGameInfo(response)
      } else { // TODO remove case after debugging
        console.error(`Invalid event: ${response.event}`);
      }
      */
      if(!infoHandled && response.event === ROOM_INFO){
        infoHandled = true;
        return handleRoomInfo(response);
      } else {
        console.log(`Received '${response.event}' event`)
      }
  }
};

/* Event Handlers */

// update game info
const handleGameInfo = ({ pin, difficulty, mode, questionSets, score, alivePingInterval, numJokers }) => {
  setState({
    alivePing: setInterval(sendAlivePing, alivePingInterval - 50), // account for latency
    info: {
      settings: {
        pin,
        difficulty,
        mode,
        questionSets,
        score,
        alivePingInterval,
        numJokers,
      }
    },
    game: {
      jokersLeft: numJokers,
    }
  });
  console.info(`Info updated`);
};

// update room players
const handleRoomPlayers = ({ num, players }) => {
  setState({
    info: {
      num,
      players,
    }
  });
  console.info(`Players updated`);
};

const handleRoomInfo = ({ pin, difficulty, mode, questionSets, score, alivePingInterval, numJokers, num, players }) => {
  setState({
    alivePing: setInterval(sendAlivePing, alivePingInterval - 50), // account for latency
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
      jokersLeft: numJokers,
    }
  });
  console.info(`room info updated`);
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
  console.info(`Player ${playerId} is ready`);
};

// on join add player to state.info.players
const handlePlayerJoin = ({ player }) => {
  setState({
    info: {
      players: state.info.players.concat([player])
    },
  });
  console.info(`Player ${player.id} joined`);
};

// remove player from state.info.players
const handlePlayerLeave = ({ playerId }) => {
  setState({
    info: {
      players: state.info.players.filter(p => p.id !== playerId),
    }
  });
  console.info(`Player ${playerId} left`)
};

// set state.state = INGAME
const handleGameStart = (event) => {
  setState({
    state: INGAME,
  });
  console.info(`Game started`)
};

// set state.state = URL_FINISH
const handleGameEnd = (event) => {
  setState({
    state: URL_FINISH,
  });
  console.info(`Game ended`)
};

// update jokersLeft
const handleJokerUse = ({ remaining }) => {
  setState({
    game: {
      jokersLeft: remaining,
    }
  });
  console.info(`Joker was used (${remaining} remaining)`)
};

// update score
const handleScoreChange = ({ newScore }) => {
  setState({
    game: {
      score: newScore,
    }
  });
  console.info(`Score changed to ${newScore}`)
};

const handleTimerSync = (event) => {
  // TODO: update time left (gets changed & rendered @ fixed interval, so nothing to do else?)
  console.log("timer sync");
};

const handleTimeoutStart = (event) => {
  // TODO: display modal & start countdown
  console.log("timeout started")
};

const handleKick = ({ playerId }) => {
  if(playerId === state.id){
    disconnect();
    window.location.href = URL_KICKED;
  }
  console.info(`Player ${playerId} was kicked`)
};

const handleAssignQuestion = (event) => {
  // TODO update questions in state & display if relevat to user
  console.log("new question");
};

const handleRemoveQuestion = (event) => {
  // TODO update questions in state & rerender(remove) if necessary
  console.log("remove question");
};