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
    this._delay = 1000; // room join delay
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
   * Deletes the join variables from sessionStorage
   *
   * @private
   */
  _clearStorage(){
    sessionStorage.removeItem('pin');
    sessionStorage.removeItem('timeStamp');
  }

  /**
   * Returns stored pin from sessionStorage if the timestamp is valid or null
   *
   * @return {Number}
   * @private
   */
  _getStoredPin(){
    const { state } = getState();
    if(state !== JOIN){ return null; }

    const pin = parseInt(sessionStorage.getItem('pin'));
    const timeStamp = parseInt(sessionStorage.getItem('timeStamp'));
    // game data should still be valid if the timeStamp is valid
    console.warn(timeStamp, new Date().valueOf(), timeStamp >= new Date().valueOf()); // TODO remove
    return timeStamp >= new Date().valueOf() && !isNaN(pin) ? pin : null;
  }

  /**
   * Stores a pin
   * @private
   */
  _storePin(){
    console.debug('JOIN_CONTROLLER: updating sessionStorage');
    const { state, pin } = getState();
    if(state === JOIN || state === FINISHED){
      this._clearStorage();
    } else {
      sessionStorage.setItem('pin', pin.toString());
      sessionStorage.setItem('timeStamp', (new Date().valueOf() + 20000).toString()); // 20sec from now
    }
    if(state !== FINISHED) setTimeout(this._storePin.bind(this), 15000); // repeat 15s
  }

  /**
   * Tries to join the room with a certain pin, and displays success/error messages
   *
   * @param pin
   * @param delay
   * @private
   */
  _join(pin, delay = true){
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
        Animate(this._root, 'fadeOut');
        console.warn('join successful');
        errors.classList.remove('error');
        errors.classList.add('success');
        errors.innerText = 'Successfully joined room';
        const elem = document.querySelector(`${this._root} ${this._selector}`);
        elem.setAttribute('readonly', 'true');
        elem.removeEventListener('change', this._handleInput.bind(this));
        elem.removeEventListener('input', this._handleInput.bind(this));

        setTimeout(() => {
          setState({
            state: LOBBY,
            pin,
            id: parseInt(data.playerId),
            highScore: parseInt(data.highScore),
          }, false);
          // connect to socket
          connect();
          // init local storage update timer
          this._storePin();
        }, delay ? this._delay : 0); // delay the room join for animation purposes
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
   * Attaches event listeners to input, checks URL and sessionStorage for PIN
   */
  init(){
    // check pin url
    console.debug('JOIN_CONTROLLER: checking URL params');
    let pin = this._checkURL();
    if(pin === null){
      // if no pin in url checkt local storage
      console.debug('JOIN_CONTROLLER: checking storage');
      pin = this._getStoredPin();
    }
    let elem = document.querySelector(`${this._root} ${this._selector}`);
    if(!elem){
      throw Error('input element is not valid');
    }
    elem.addEventListener('change', this._handleInput.bind(this));
    elem.addEventListener('input', this._handleInput.bind(this));
    if(pin !== null){
      elem.value = pin.toString().padStart(6, '0');
      this._join(pin, false);
    }
  }
}

const joinController = new JoinController('#join', '#pin', '#errors');
joinController.init();

// document.addEventListener('DOMContentLoaded', () => setState({}));