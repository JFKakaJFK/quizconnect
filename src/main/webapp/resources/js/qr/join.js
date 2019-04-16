"use strict";

const URL = '/qr/join/';
const pin = document.getElementById('pin');
const msg = document.getElementById('msg');

localStorage.removeItem('pin');
localStorage.removeItem('playerId');

const validatePIN = () => {
    let input = pin.value.replace(/^0+/, '').substring(0, 6);
    let PIN = parseInt(input.replace(/[^\d]/gi, ''));

    pin.value = (isNaN(PIN) ? '' : PIN).toString().padStart(6, '0');
    return PIN;
};

const joinGame = (pin) => {
    fetch(`${URL}${pin}`, {
        method: 'POST',
    })
    .then(response => response.json())
    .then(data => {
        console.log(data);
        if(data.error){
            msg.innerHTML = data.message;
        }
        if(data.playerId){
            console.log(data.playerId);
            localStorage.setItem('pin', pin);
            localStorage.setItem('playerId', data.playerId);
            window.location.href = '/quizroom/index.html'
        }
    })
    .catch(error => msg.innerHTML = error.message);
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
    pin.value = params.get('pin');
}

pin.addEventListener('change', join);
pin.addEventListener('input', join);