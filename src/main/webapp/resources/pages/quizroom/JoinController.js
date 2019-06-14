"use strict";

import { LOBBY, JOIN, FINISHED, JOIN_ENDPOINT } from './Constants.js';
import setState, { getState } from './State.js';
import { connect } from './Socket.js';
import Animate from './Animate.js';

/**
 * Class handling joining and connecting to the WebSocket
 */
class JoinController{
  constructor(root, selector, messages){
    this._selector = selector;
    this._messages = messages;
    this._root = root;
    this._delay = 300; // room join delay
  }

  /**
   * Checks the url for the GET parameter pin, returns pin as int or null.
   *
   * @returns {Number}
   * @private
   */
  _checkURL(){
    const params = new URLSearchParams(window.location.search);
    const pin = params.has('pin') ? parseInt(params.get('pin')) : null;
    return pin !== null && !isNaN(pin) ? pin : null;
  }

  /**
   * Deletes the join variables from localStorage
   *
   * @private
   */
  _clearStorage(){
    localStorage.removeItem('pin');
    localStorage.removeItem('timeStamp');
  }

  /**
   * Returns stored pin from localStorage if the timestamp is valid or null
   *
   * @return {Number}
   * @private
   */
  _getStoredPin(){
    const { state } = getState();
    if(state !== JOIN){ return null; }

    const pin = parseInt(localStorage.getItem('pin'));
    const timeStamp = new Date(parseInt(localStorage.getItem('timeStamp')));
    // game data should still be valid if the timeStamp is valid
    return timeStamp >= new Date() && !isNaN(pin) ? pin : null;
  }

  /**
   * Stores a pin
   * @private
   */
  _storePin(){
    console.debug('JOIN_CONTROLLER: updating localStorage');
    const { state, pin } = getState();
    if(state === JOIN || state === FINISHED){
      this._clearStorage();
    } else {
      localStorage.setItem('pin', pin.toString());
      localStorage.setItem('timeStamp', (new Date().valueOf() + 20000).toString()); // 20sec from now
    }
    setTimeout(this._storePin.bind(this), 15000); // repeat 15s
  }

  /**
   * Tries to join the room with a certain pin, and displays success/error messages
   *
   * @param pin
   * @private
   */
  _join(pin){
    const { state } = getState();
    if(state !== JOIN || isNaN(pin)){ return; }
    const errors = document.querySelector(`${this._root} ${this._messages}`);
    // fetch
    fetch(`${JOIN_ENDPOINT}${pin}`, {
      method: 'POST'
    }).then(response => {
      if(response.ok){return response.json()}
      throw Error(response.statusText || response.status.toString());
    }).then(data => {
      if(data.error){ // show error message
        errors.classList.remove('success');
        errors.classList.add('error');
        errors.innerText = data.error;
        Animate(this._messages, 'shake');
      } else if(data.playerId && data.highScore){ // on success store pin + success
        // delay + animation
        // TODO
        console.warn('join successful');
        errors.classList.remove('error');
        errors.classList.add('success');
        errors.innerText = 'Successfully joined room ' + pin;
        const elem = document.querySelector(`${this._root} ${this._selector}`);
        elem.setAttribute('readonly', 'true');
        elem.removeEventListener('change', this._handleInput.bind(this));
        elem.removeEventListener('input', this._handleInput.bind(this));

        setTimeout(() => {
          console.warn('connecting to socket'); // TODO remove
          setState({
            state: LOBBY,
            pin,
            id: parseInt(data.playerId),
            highScore: parseInt(data.highScore),
          }, false);
          // connect to socket
          connect();
        }, this._delay); // delay the room join for animation purposes

        // init local storage update timer
        this._storePin();
      }
    }).catch(e => {
      console.error(error);
    })
  }

  /**
   * Sanitizes input and tries to join game if the PIN structure is valid
   *
   * @param e The event.
   * @private
   */
  _handleInput(e){
    const elem = document.querySelector(`${this._root} ${this._selector}`);
    if(!elem){ return; }
    // strip non numeric
    // if padding replace(/^0+/, '').substring(0, 6)
    let sanitized = e.target.value.replace(/^0+/, '').replace(/[^\d]/gi, '').substring(0, 6);
    elem.value = sanitized.padStart(6, '0');
    // try to join
    if(sanitized.length < 1){ return; }
    this._join(parseInt(sanitized));
  }

  /**
   * Attaches event listeners to input, checks URL and localStorage for PIN
   */
  init(){
    let elem = document.querySelector(`${this._root} ${this._selector}`);
    if(!elem){
      throw Error('input element is not valid');
    }
    elem.addEventListener('change', this._handleInput.bind(this));
    elem.addEventListener('input', this._handleInput.bind(this));
    // check pin url
    console.debug('JOIN_CONTROLLER: checking URL params');
    let pin = this._checkURL();
    if(pin === null){
      // if no pin in url checkt local storage
      console.debug('JOIN_CONTROLLER: checking localStorage');
      pin = this._getStoredPin();
    }

    if(pin !== null){
      elem.value = pin;
      this._join(pin);
    }
  }
}

const joinController = new JoinController('#join', '#pin', '#errors');
joinController.init();

// document.addEventListener('DOMContentLoaded', () => setState({}));