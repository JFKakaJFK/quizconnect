"use strict";

import { INGAME, LOBBY, JOIN } from './Constants.js';
import setState from './State.js';

/**
 * Class handling joining and connecting to the WebSocket
 */
class App{
  constructor(){
    // set up state
    // set up rendering
    // set up socket connection
    this._state = {

    }
  }

  init(){
    // call _init
    this._init();
  }

  _init(){
    // check pin url
    // if no pin in url checkt local storage
    // init local storage update timer
  }
}

const app = new App();
app.init();

document.addEventListener('DOMContentLoaded', () => setState({}));