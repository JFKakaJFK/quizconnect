"use strict";

/**
 * Class handling everything the quizroom frontend needs.
 */
class App{
  constructor(){
    // set up state
    // set up rendering
    // set up socket connection
    this.state = {

    }
  }

  setState(newState, render = true){

    state = Object.assign(state, newState, { info: Object.assign(state.info, newState.info) }, { game: Object.assign(state.game, newState.game)});
    console.debug(`STATE: merged states, new State is ${state.state === INGAME ? 'INGAME' : state.state === LOBBY ? 'LOBBY' : state.state === JOIN ? 'JOIN' : 'FINISHED'}:`, JSON.stringify(state)); // should keep state changes affecting the debug log

    // todo
    if(render){
      let event = new CustomEvent('stateChange', {
        detail: this.state,
      })
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