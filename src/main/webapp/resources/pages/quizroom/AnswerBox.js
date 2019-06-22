"use strict";

import Animate from './Animate.js';

const LAYOUTS = [
  [1,4],
  [3,6],
  [1,6],
  [2,5],
  [1,3,4,5],
  [2,3,4,6],
  [1,2,3,4],
  [1,4,5,6],
  [1,2,3,6],
  [3,4,5,6],
];

let counter = 1;
let layout = LAYOUTS[Math.floor(Math.random()*LAYOUTS.length)];

/**
 * Container element for a single answer.
 */
class AnswerBox {
  constructor(parent){
    let id = counter++;
    this._id = 'answer-box-' + id;
    this._node = document.createElement('div');
    this._node.classList.add('answer-box', 'empty', 'fast', this._id, layout.includes(id) ? 'answer-box-1x2' : 'answer-box-2x1');
    this._id = '.' + this._id;
    parent.appendChild(this._node);
    this._DELAY = 800; // delay in ms, in order to show animations for a while
    this._empty = true;
    this._emptying = false;
  }

  /**
   * Clears the box.
   *
   * @private
   */
  _clear(){
    this._node.innerHTML = '';
    this._node.classList.add('empty');
    this._emptying = false;
  }

  /**
   * Clears this box.
   */
  clear(){
    if(this._emptying) return;
    this._empty = true;
    this._emptying = true;
    let answer = this._node.querySelector('.answer');
    if(answer) answer.style.pointerEvents = 'none';
    Animate(this._id, 'fadeOut');
    setTimeout(this._clear, this._DELAY);
  }

  /**
   * Attaches the answer to the box.
   *
   * @param answer
   * @private
   */
  _assign(answer){
    this._node.innerHTML = '';
    this._node.classList.remove('empty');
    this._node.appendChild(answer);
    Animate(this._id, 'fadeIn');
  }

  /**
   * Assigns a new answer to this box. Since the animations may not have finished yet, this may take a second...
   *
   * @param answer
   */
  assign(answer){
    this._empty = false;
    if(this._emptying){
      setTimeout(() => this._assign(answer), this._DELAY);
    } else {
      this._assign(answer);
    }
  }

  /**
   * Returns whether a new answer can be assigned to this box.
   *
   * @return {boolean}
   */
  isEmpty(){
    return this._empty;
  }

  /**
   * Returns answer id or empty string.
   *
   * @return {String}
   */
  getAnswerId(){
    let answer = this._node.querySelector('[data-id]');
    if(!answer) return '';
    return answer.getAttribute('data-id');
  }
}

export default AnswerBox;