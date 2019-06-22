"use strict";

import { URL_HOME } from "./Constants.js";
import { Client } from "./Socket.js";
import { setSimpleText } from "./Utils.js";
import Animate from './Animate.js';

/**
 * Controller handling activity timeouts.
 */
class TimeoutController {
  constructor(){
    this._remaining = 0;
    this._active = false;
    this._interval = null;
    this._STEP_SIZE = 100;
  }

  /**
   * Cancel a timeout.
   *
   * @private
   */
  _abortTimeout(){
    if(!this._active) return;
    this._active = false;
    clearInterval(this._interval);
    this._interval = null;
    // hide modal
    $('#timeout').modal('hide');
    console.debug(`TIMEOUT_CONTROLLER: timeout aborted`);
  }

  /**
   * Handles the start of a timeout.
   *
   * @param e
   * @private
   */
  _handleTimeoutStart(e){
    this._active = true;
    this._remaining = e.detail.remaining;
    this._interval = setInterval(this._handleTimeout.bind(this), this._STEP_SIZE);
    // show modal
    $('#timeout').modal('show');
    console.debug(`TIMEOUT_CONTROLLER: timeout started`);
  }

  /**
   * Decreases the timer and shows the remaining time.
   *
   * @private
   */
  _handleTimeout(){
    if(!this._active) return;
    // display remaining in seconds
    const seconds = (this._remaining / 1000).toFixed(1);
    let elem = document.getElementById('timeoutRemainingTime');
    setSimpleText(elem, seconds.toString());

    this._remaining -= this._STEP_SIZE;
    if(this._remaining <= 0){
      Client.leaveRoom();
      this._abortTimeout();
      document.body.classList.add('fast');
      Animate('body', 'fadeOut', () => window.location.href = URL_HOME);
    }
    console.debug(`TIMEOUT_CONTROLLER: timeout is active, ${seconds}s remaining`);
  }

  /**
   * Starts to listen for timeout events.
   */
  init(){
    // listen for timeout events
    document.addEventListener('timeoutStart', this._handleTimeoutStart.bind(this));
    document.addEventListener('cancelTimeout', this._abortTimeout.bind(this));
  }
}

const tc = new TimeoutController();
tc.init();