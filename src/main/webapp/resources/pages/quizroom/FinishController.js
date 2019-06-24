"use strict";

import { SHARE_WHATSAPP, SHARE_FACEBOOK, SHARE_TWITTER } from "./Constants.js";
import { getState } from "./State.js";
import Confetti from "./Confetti.js";

/**
 * Handles the game end
 */
class FinishController {
  constructor(){
    this._highScore = '#highScore';
    this._shareWA = '[data-finished-whatsapp]';
    this._shareFB = '[data-finished-facebook]';
    this._shareTW = '[data-finished-twitter]';
    this._confettiDelay = 250;
  }

  /**
   * Shows some confetti, amount and type depend on game score.
   *
   * @param highScore
   * @param score
   * @private
   */
  _showConfetti(highScore, score){
    const c = new Confetti();
    if(score > highScore) {
      c.fireworks(25);
      c.cannon(Math.max(3, score / 150)); // between 3 and 10 times
      c.shower(Math.max(3, score / 150));
    } else if(score > 1500){ // somewhat good
      c.fireworks(25);
    } else if (score > 0){
      c.cannon(Math.max(3, score / 150)); // 3 - 10 times, depending on score
    } else {
      c.shower(1, 5); // sad
    }
  }

  /**
   * Shows a new highScore.
   *
   * @param highScore
   * @param score
   * @private
   */
  _showHighscore(highScore, score){
    if(highScore < score){
      const hs = document.querySelector(this._highScore);
      if(!hs) return;
      hs.textContent = 'New Highscore!';
    } else if(score < -100){
      const hs = document.querySelector(this._highScore);
      if(!hs) return;
      hs.textContent = 'You could do worse'; // todo change text?
    }
  }

  /**
   * Updates the share links at the finish screen with the final score;
   *
   * @param score
   * @private
   */
  _updateShareBtns(score){
    const wa = document.querySelector(this._shareWA);
    wa.href = SHARE_WHATSAPP(`Hey i've just scored ${score} points playing QuizConnect`);
    const tw = document.querySelector(this._shareTW);
    tw.href = SHARE_TWITTER(`Hey i've just scored ${score} points playing QuizConnect`);
    const fb = document.querySelector(this._shareFB);
    fb.href = SHARE_FACEBOOK('http://www.quizconnect.rocks');
  }

  /**
   * Handles the game end.
   *
   * @private
   */
  _handleGameEnd(){
    const { highScore, game } = getState();
    this._showHighscore(highScore, game.score);
    this._updateShareBtns(game.score);
    setTimeout(() => this._showConfetti(highScore, game.score), this._confettiDelay);
    document.removeEventListener('gameEnd', this._handleGameEnd.bind(this));
  }

  init(){
    // listen for events
    document.addEventListener('gameEnd', this._handleGameEnd.bind(this));
  }
}

const fc = new FinishController();
fc.init();