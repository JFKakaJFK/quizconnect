"use strict";

const PIN = document.getElementById('pin');
// const msg = document.getElementById('msg');

const validatePIN = () => {
  let input = PIN.value.replace(/^0+/, '').substring(0, 6);
  let pin = parseInt(input.replace(/[^\d]/gi, ''));

  PIN.value = (isNaN(pin) ? '' : pin).toString().padStart(6, '0');
  return pin;
};

const joinGame = (pin) => {
  fetch(`${JOIN_ENDPOINT}${pin}`, {
      method: 'POST',
  })
  .then(response => response.json())
  .then(data => {
    // console.log(data);
    if(data.error){
        // msg.innerHTML = data.message; // TODO ignore? | default: show invalid indicator (e.g. red border) until pin is valid?
      console.log('TODO: error animation / progress')
    }
    if(data.playerId){
      console.log('PLAYER:', data.playerId);
      console.log('TODO: fancy animation')
      /*
      setState({
        state: LOBBY,
        pin,
        id: data.playerId,
      });
      */
      state.state = LOBBY;
      state.pin = pin;
      state.id = data.playerId;
      updateLocalStorage();
      console.log(state.pin, state.id, stompClient);
      connect();
      console.log('should have connected')
    }
  })
  .catch(error => console.error(error));
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

checkLocalStorage();

const clearLocalStorage = () => {
  localStorage.removeItem('pin');
  localStorage.removeItem('timeStamp');
};

const updateLocalStorage = () => {
  console.log('updating localStorage')
  if(state.state === JOIN || state.state === FINISHED){
    clearLocalStorage();
  } else {
    localStorage.setItem('pin', state.pin.toString());
    localStorage.setItem('timeStamp', (new Date().valueOf() + 60 * 1000).toString()); // 1 min from now
  }
};