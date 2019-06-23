"use strict";

/**
 * Handles the in game sfx.
 */
class SFXController {
  constructor(){
    this._sfx = {
      end: '[data-game-end]',
      start: '[data-game-start]',
      joker: '[data-game-joker]',
      question: '[data-game-new-question]',
      right: '[data-game-right-answer]',
      wrong: '[data-game-wrong-answer]',
    }
  }

  /**
   * Initializes event listeners for sfx events.
   */
  init(){
    document.addEventListener('sfxGameStart', () => playSound(this._sfx.start));
    document.addEventListener('sfxGameEnd', () => playSound(this._sfx.end));
    document.addEventListener('sfxJoker', () => playSound(this._sfx.joker));
    document.addEventListener('sfxRightAnswer', () => playSound(this._sfx.right));
    document.addEventListener('sfxWrongAnswer', () => playSound(this._sfx.wrong));
    document.addEventListener('sfxNewQuestion', () => playSound(this._sfx.question));
  }
}

const sfx = new SFXController();
sfx.init();