"use strict";

const Timers = { // TODO add all timers here, init w/ null
  timeoutTimer: null,
  clear: (timer) => {
    if(Timers.hasOwnProperty(timer) && Timers[timer] !== null){
      clearInterval(timer);
      Timers[timer] = null;
    }
  }
};

export default Timers;