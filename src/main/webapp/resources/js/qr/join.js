"use strict";

const PIN = document.getElementById('pin');

const validatePIN = () => {
  let input = PIN.value.replace(/^0+/, '').substring(0, 6);
  let pin = parseInt(input.replace(/[^\d]/gi, ''));

  PIN.value = (isNaN(pin) ? '' : pin).toString().padStart(6, '0');
  return pin;
};

const joinGame = (pin) => {
  if(state.state === INGAME && state.pin !== null && state.id !== null){
    return;
  }
  fetch(`${JOIN_ENDPOINT}${pin}`, {
    method: 'POST',
  })
  .then(response => {
    if(!response.ok){
      throw Error(response.statusText || response.status.toString());
    }
    return response;
  })
  .then(response => response.json())
  .then(data => {
    if(data.error){
      console.error(data.error);
      console.log('TODO: error animation / progress') // TODO
      document.getElementById('errors').innerHTML = data.error;
    } else if(data.playerId){
      console.debug('JOIN: connected as player', data.playerId);
      console.log('TODO: fancy animation'); // TODO
      state.state = LOBBY;
      state.pin = pin;
      state.id = data.playerId;
      updateLocalStorage();
      connect();
    }
  })
  .catch(error => {
    console.error(error);
  });
};

const join = () => {
  const pin = validatePIN();
  if(isNaN(pin)) return;
  joinGame(pin);
};

/**
 * If hotlink directly try to join game
 *
 * @type {URLSearchParams}
 */
const params = new URLSearchParams(window.location.search);
if(params.has('pin')){
  joinGame(parseInt(params.get('pin')));
  PIN.value = params.get('pin');
}

PIN.addEventListener('change', join);
PIN.addEventListener('input', join);

const checkLocalStorage = () => {
  if(state.state !== JOIN){
    return;
  }
  let pin = parseInt(localStorage.getItem('pin'));
  let timeStamp = new Date(parseInt(localStorage.getItem('timeStamp')));
  if(timeStamp >= new Date()){ // game data should still be valid
    if(!isNaN(pin)){
      joinGame(pin);
    }
  }
};


// TODO move functions to state
checkLocalStorage();

const clearLocalStorage = () => {
  localStorage.removeItem('pin');
  localStorage.removeItem('timeStamp');
};

const updateLocalStorage = () => {
  console.debug('STATE: updating localStorage');
  console.error('STATE: updating localStorage');
  if(state.state === JOIN || state.state === FINISHED){
    clearLocalStorage();
  } else {
    localStorage.setItem('pin', state.pin.toString());
    localStorage.setItem('timeStamp', (new Date().valueOf() + 60 * 1000).toString()); // 1 min from now
  }
};