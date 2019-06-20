"use strict";

import { FINISHED, INGAME, ANSWERTYPE_MATH, ANSWERTYPE_TEXT, ANSWERTYPE_PICTURE, MAX_ANSWERS, PREFIX_ANSWER_PICTURE, SERVER_REFRESH_RATE } from "./Constants.js";
import Client from './Socket.js';
import {JOKER_REUSE_BUFFER} from "./Constants.js";
import { getState } from "./State.js";
import Animate from './Animate.js';
import { setLayoutText, dangerouslySetHTML, setSimpleText, hash, verify } from "./Utils.js";

/**
 * Handles all game duties.
 */
class GameController {
  constructor(){
    this._question = '#question';
    this._time = '#timer';
    this._answers = '#answers';
    this._joker = '#joker';
    this._score = '[data-score]';
    this._allowJokerUse = true;
    this._totalTime = 0;
    this._remainingTime = 0;
    this._delta = 1000 / 60;
    this._interval = null;

    this._aheadOfServer = 0;
    this._animation = null;
    this._start = false;
  }

  /**
   * Handles the use of a joker.
   *
   * @private
   */
  _handleJokerUse(){
    console.warn('joker use called');
    const { state, game } = getState();
    const joker = document.querySelector(this._joker);
    if(!joker) return;
    if(this._allowJokerUse && state === INGAME && game.jokersLeft > 0) {
      Client.useJoker();
      if(joker) joker.setAttribute('data-disabled', 'true');
      this._allowJokerUse = false;
      setTimeout(() => {
        this._allowJokerUse = game.jokersLeft > 0;
        if(joker && this._allowJokerUse) joker.setAttribute('data-disabled', 'false');
      }, JOKER_REUSE_BUFFER);
    }
  }

  /**
   * Animation for joker use.
   *
   * @private
   */
  _jokerUseAnimation(){
    Animate(this._question, 'pulse');
    Animate(this._answers, 'pulse');
  }

  /**
   * Disables the joker if no jokers are left.
   *
   * @param jokersLeft
   * @private
   */
  _checkJoker(jokersLeft){
    const joker = document.querySelector(this._joker);
    if(!joker) return;
    if(jokersLeft <= 0){
      joker.setAttribute('data-disabled', 'true');
      joker.removeEventListener('click', this._handleJokerUse.bind(this));
    }
  }

  /**
   * Sets the current timer progress.
   *
   * @param percent
   * @private
   */
  _setTimerProgress(percent){
    const elem = document.querySelector(this._time);
    if(elem) elem.style.width = percent + '%';
  }

  /**
   * Clears the timer interval.
   *
   * @private
   */
  _clearInterval(){
    clearInterval(this._interval);
    this._interval = null;
  }

  /**
   * Animates the timer by setting the timer progress.
   * @private
   */
  _animateTimer = () => {
    // update remaining time
    this._remainingTime -= this._delta;
    // get remaining time in percent
    let remaining = (this._remainingTime / this._totalTime) * 100;
    //console.log('INT', this._delta, new Date().valueOf(), remaining);
    // set progress
    this._setTimerProgress(remaining);
    // check if time has run out
    if(this._remainingTime <= 0 || remaining <= 0) this._clearInterval();
  };

  _animateTimerRAF(timeStamp){
    if(!this._start) this._start = timeStamp;
    let delta = timeStamp - this._start;
    //let correction = this._aheadOfServer * delta;

    //this._remainingTime -= delta + correction;

    let remaining = ((this._totalTime - delta) / this._totalTime) * 100;
    //console.log('RAF', delta, new Date().valueOf(), remaining, this._aheadOfServer);

    // set progress
    //this._setTimerProgress(remaining);
    const elem = document.querySelector('#timerRAF');
    if(elem) elem.style.width = remaining + '%';

    if(remaining > 0){
      requestAnimationFrame(this._animateTimerRAF.bind(this));
    } else {
      cancelAnimationFrame(this._animation);
      this._start = false;
    }
  }

  /**
   * Handles the assignQuestion event.
   *
   * @param mode
   * @param question
   * @private
   */
  _handleAssignQuestion({ mode, question }){
    if(question === null) throw new Error('Question cannot be null');
    const parent = document.querySelector(this._question);
    if(!parent) throw new Error('Question box not found');
    const { type, question: qtext, remaining } = question;
    // set timer
    this._setTimerProgress(100);
    this._remainingTime = remaining;
    this._totalTime = remaining;
    // start timer
    this._clearInterval();
    this._interval = setInterval(this._animateTimer.bind(this), this._delta);

    this._start = false;
    this._animation = requestAnimationFrame(this._animateTimerRAF.bind(this));
    console.log('raf should have started');
    // create node
    let node;
    if(mode === 'reverse' && type === ANSWERTYPE_PICTURE){
      node = document.createElement('img');
      node.alt = 'question';
      node.src = PREFIX_ANSWER_PICTURE(qtext);
      node.setAttribute('class', 'question question-picture');
    } else {
      node = document.createElement('h2');
      node.setAttribute('class', 'text-center question question-text');
      if(type === ANSWERTYPE_MATH){
        katex.render(qtext, node, { throwOnError: false });
      } else {
        setLayoutText(node, qtext);
      }
    }
    // clear parent
    parent.innerHTML = '';
    // append node
    parent.appendChild(node);
    // fade in
    Animate(this._question, 'fadeIn');
  }

  /**
   * Updates a question by updating the timer.
   *
   * @param question
   * @private
   */
  _handleUpdateQuestion({ remaining }){
    // update timer
    this._aheadOfServer = (remaining - this._remainingTime) / SERVER_REFRESH_RATE;
    if(this._interval) this._clearInterval();
    this._remainingTime = remaining;
    this._interval = setInterval(this._animateTimer.bind(this), this._delta);

    //this._remainingTime = remaining; // don't even chenge the animation...
  }

  /**
   * Removes a question by removing the timer and deleting the question.
   *
   * @private
   */
  _handleRemoveQuestion(){
    // destroy timer
    cancelAnimationFrame(this._animation);
    if(this._interval) this._clearInterval();
    // fadeout
    Animate(this._question, 'fadeOut');
    // removing the question after the animation doesn't work, as the animation
    // is finished after the new question is appended to the layout...
  }

  /**
   * Update the displayed score.
   *
   * @param score
   * @private
   */
  _renderScore(score){
    if(!score) return;
    const scores = document.querySelectorAll(this._score);
    scores.forEach(s => {
      const prev = parseInt(s.textContent);
      if(prev !== score){
        let change = prev > score ? 'decrease' : 'increase';
        //animateScore(s, prev, score);

        s.classList.add('faster');
        Animate(this._score, 'fadeOut', () => {
          setSimpleText(s, score);
          s.classList.add(change);
          Animate(this._score, 'fadeIn', () => {
            s.classList.remove(change, 'faster');
          })
        })
      }
    });
  }

  /**
   * Handles question answering.
   *
   * @param node
   * @param questionId
   * @param answerId
   * @private
   */
  _handleAnswerQuestion(node, questionId, answerId){
    Client.answerQuestion(questionId, answerId);
    let selector = `[data-id="${node.getAttribute('data-id')}"]`;
    node.classList.remove('fast');
    if(answerId === 0){
      node.classList.add('correct');
      Animate(selector, 'pulse', () => {
        node.classList.add('fast');
        node.classList.remove('correct');
      })
    } else {
      node.classList.add('incorrect');
      Animate(selector, 'shake', () => {
        node.classList.add('fast');
        node.classList.remove('incorrect');
      })
    }
  }

  /**
   * Returns a custom id for an answer.
   *
   * @param questionId
   * @param answerId
   * @return {string}
   * @private
   */
  _getAnswerId(questionId, answerId){
    return questionId.toString() + answerId.toString(); // todo hash & verify
  }

  /**
   * Renders a single answer.
   *
   * @param mode
   * @param answer
   * @return {HTMLElement}
   * @private
   */
  _renderAnswer(answer, mode){
    if(!answer) return null;
    const { questionId, type, answerId, answer: text } = answer;
    const answerNode = document.createElement('div');
    answerNode.classList.add('box', 'answer', 'fast', 'answer-' + type); //, 'answer-order-' + Math.floor(Math.random() * 9));
    answerNode.setAttribute('data-id', this._getAnswerId(questionId, answerId));
    answerNode.addEventListener('click', () => this._handleAnswerQuestion(answerNode, questionId, answerId));
    let inner;
    switch (type){
      case ANSWERTYPE_MATH:
        inner = document.createElement('p');
        katex.render(text, inner, { throwOnError: false });
        break;
      case ANSWERTYPE_PICTURE:
        if(mode === 'reverse' && !(text.endsWith('.png') || text.endsWith('.jpg'))){
          inner = document.createElement('p');
          inner.innerText = text;
        } else {
          inner = document.createElement('img');
          inner.alt = 'answer';
          inner.src = PREFIX_ANSWER_PICTURE(text);
        }
        break;
      default:
        inner = document.createElement('p');
        inner.innerText = text;
    }
    answerNode.appendChild(inner);
    return answerNode;
  }

  /**
   * Shuffles the answers.
   *
   * @param answers
   * @return {*}
   * @private
   */
  _shuffleAnswers(answers){
    for (let i = answers.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [answers[i], answers[j]] = [answers[j], answers[i]];
    }
    return answers;
  }

  /**
   * Renders all currently active answers.
   *
   * @param mode
   * @param answers
   * @private
   */
  _renderAnswers({ mode, answers }){
/*
    const container = document.querySelector(this._answers);
    if(!container) return;
    const answerNodes = container.querySelectorAll('.answer');
    if(answerNodes.length > MAX_ANSWERS) console.error("Too many assigned answers");
    let copy = [...this._shuffleAnswers(answers)];
    let toRemove = [];
    answerNodes.forEach(node => {
      const nodeId = node.getAttribute('data-id');
      let answer = copy.find(a => nodeId === this._getAnswerId(a.questionId, a.answerId));
      if(answer === undefined){
        toRemove.push(node);
      }
      copy = copy.filter(a => nodeId !== this._getAnswerId(a.questionId, a.answerId));
    });

    // todo check if too many answers?
    copy.forEach(a => {
      if(toRemove.length > 0){
        let old = toRemove.pop();
        try {
          Animate(`[data-id="${old.getAttribute('data-id')}"]`, 'fadeOut', () => {
            container.replaceChild(this._renderAnswer(a, mode), old);
            Animate(`[data-id="${this._getAnswerId(a.questionId, a.answerId)}"]`, 'fadeIn');
          });
        } catch (e) {
          container.appendChild(this._renderAnswer(a, mode));
          Animate(`[data-id="${this._getAnswerId(a.questionId, a.answerId)}"]`, 'fadeIn');
        }
      } else {
        container.appendChild(this._renderAnswer(a, mode));
        Animate(`[data-id="${this._getAnswerId(a.questionId, a.answerId)}"]`, 'fadeIn');
      }
    }); // todo animation
*/
    const container = document.querySelector(this._answers);
    if(!container) return;
    const answerNodes = container.querySelectorAll('.answer');
    if(answerNodes.length > MAX_ANSWERS) console.error("Too many assigned answers");
    let copy = [...this._shuffleAnswers(answers)];
    answerNodes.forEach(node => {
      const nodeId = node.getAttribute('data-id');
      let answer = copy.find(a => nodeId === this._getAnswerId(a.questionId, a.answerId));
      if(answer === undefined){
        Animate(`[data-id="${nodeId}"]`, 'fadeOut');
        setTimeout(container.removeChild(node), 200); // 200 ms delay for animation
      }
      copy = copy.filter(a => nodeId !== this._getAnswerId(a.questionId, a.answerId));
    });

    // todo check if too many answers?
    copy.forEach(a => {
      container.appendChild(this._renderAnswer(a, mode));
      Animate(`[data-id="${this._getAnswerId(a.questionId, a.answerId)}"]`, 'fadeIn');
    });
  }

  /**
   * Handles a state change in game.
   *
   * @param newState
   * @private
   */
  _handleStateChange(newState){
    const { state, game } = newState;
    this._renderScore(game.score);
    if(state === FINISHED){
      this.destroy();
      return;
    }
    // this._renderQuestion(game); // todo event based
    // this._renderAnswers(game); // todo event based
    this._checkJoker(game.jokersLeft);
  }

  /**
   * Stop to listen to any state change events.
   */
  destroy(){
    document.removeEventListener('stateChange', (e) => this._handleStateChange(e.detail));
    const joker = document.querySelector(this._joker);
    if(joker) joker.removeEventListener('click', this._handleJokerUse.bind(this));
  }

  /**
   * Begin to listen to state changes.
   */
  init(){
    // listen for state changes
    document.addEventListener('stateChange', (e) => this._handleStateChange(e.detail));

    document.addEventListener('assignQuestion', (e) => this._handleAssignQuestion(e.detail.game));
    document.addEventListener('updateQuestion', (e) => this._handleUpdateQuestion(e.detail));
    document.addEventListener('removeQuestion', this._handleRemoveQuestion.bind(this));

    //document.addEventListener('assignAnswer', (e) => this._renderAnswers(e.detail.game));
    //document.addEventListener('removeAnswer', (e) => this._renderAnswers(e.detail.game));

    document.addEventListener('jokerUse', this._jokerUseAnimation.bind(this));
    const joker = document.querySelector(this._joker);
    if(joker) joker.addEventListener('click', this._handleJokerUse.bind(this));
  }
}

const gc = new GameController();
gc.init();