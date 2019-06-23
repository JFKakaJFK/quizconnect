"use strict";

import { random } from './Utils.js';

/**
 * Class wrapping confetti.js, provides a few custom effects.
 */
class Confetti {
  constructor(options){
    this._interval = 300; // time in ms until effect is fired again
  }

  /**
   * A firework like effect.
   *
   * @param times
   * @param particles
   */
  fireworks(times, particles = null){
    if(isNaN(times) || times <= 0) return;
    confetti({
      startVelocity: 30,
      spread: 360,
      ticks: 60,
      particleCount: particles === null ? random(50, 100) : particles,
      origin: {
        x: Math.random(),
        // since they fall down, start a bit higher than random
        y: Math.random() - 0.2
      }
    });
    setTimeout(() => this.fireworks(times - 1, particles), this._interval);
  }

  /**
   * A simple confetti shower.
   *
   * @param times
   * @param particles
   */
  shower(times, particles = null){
    if(isNaN(times) || times <= 0) return;
    confetti({
      angle: 270,
      spread: random(50, 100),
      particleCount: particles === null ? random(50, 100) : particles,
      origin: {
        y: -0.6
      }
    });
    setTimeout(() => this.shower(times - 1, particles), this._interval);
  }

  /**
   * A simple confetti cannon.
   *
   * @param times
   * @param particles
   */
  cannon(times, particles = null){
    if(isNaN(times) || times <= 0) return;
    confetti({
      angle: random(55, 125),
      spread: random(50, 70),
      particleCount: particles === null ? random(50, 100) : particles,
      origin: {
        y: 0.6
      }
    });
    setTimeout(() => this.cannon(times - 1, particles), this._interval);
  }
}

export default Confetti;