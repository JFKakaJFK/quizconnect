"use strict";

import { FINISHED, INGAME, ANSWERTYPE_MATH, ANSWERTYPE_TEXT, ANSWERTYPE_PICTURE, MAX_ANSWERS, PREFIX_ANSWER_PICTURE } from "./Constants.js";
import Client from './Socket.js';
import {JOKER_REUSE_BUFFER} from "./Constants.js";
import { getState } from "./State.js";
import Animate from './Animate.js';
import { setLayoutText, dangerouslySetHTML, setSimpleText, hash, verify } from "./Utils.js";
import AnswerBox from './AnswerBox.js';

/**
 * Handles all game duties.
 */
class GameController {
  constructor(){
    this._questionBox = '.ingame-question';
    this._question = '#question';
    this._time = '#timer';
    this._answers = '#answers';
    this._answerBoxes = [];
    this._joker = '#joker';
    this._score = '[data-score]';
    this._allowJokerUse = true;
    this._totalTime = 0;
    this._remainingTime = 0;
    this._delta = 1000 / 60;
    this._interval = null;

    this._animation = null;
    this._start = false;

    this._raf = null;
    this._old = false;
    this._remainingTimeRAF = 0;
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
    Animate(this._questionBox, 'pulse');
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

  /**
   * Animates the timer by setting the timer progress.
   * @private
   */
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
    //if(elem) elem.style.width = remaining + '%'; // decrease width
    //if(elem) elem.style.width = 100 - remaining + '%'; // increase width
    if(elem) elem.style.transform = 'translate3d(' + (-100 + remaining) + '%,0,0)'; // shift left

    if(remaining > 0){
      requestAnimationFrame(this._animateTimerRAF.bind(this));
    } else {
      cancelAnimationFrame(this._animation);
      this._start = false;
    }
  }

  /**
   * Animates the timer by setting the timer progress.
   * @private
   */
  _animateTimerRAF2(timeStamp){
    if(!this._old) this._old = timeStamp;
    let delta = timeStamp - this._old;
    this._old = timeStamp;

    this._remainingTimeRAF -= delta;

    let remaining = (this._remainingTimeRAF / this._totalTime) * 100;

    const elem = document.querySelector('#timerRAF2');
    //if(elem) elem.style.width = remaining + '%'; // decrease width
    //if(elem) elem.style.width = 100 - remaining + '%'; // increase width
    if(elem) elem.style.transform = 'translate3d(' + (-100 + remaining) + '%,0,0)'; // shift left

    if(remaining >= 0){
      requestAnimationFrame(this._animateTimerRAF2.bind(this));
    } else {
      cancelAnimationFrame(this._raf);
      this._old = false;
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
    this._remainingTimeRAF = remaining;
    this._totalTime = remaining;
    // start timer
    this._clearInterval(); // todo remove
    this._interval = setInterval(this._animateTimer.bind(this), this._delta);

    this._start = false; // todo remove
    this._animation = requestAnimationFrame(this._animateTimerRAF.bind(this));

    this._old = false; // todo refactor a little
    this._raf = requestAnimationFrame(this._animateTimerRAF2.bind(this));
    // create _node
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
    // append _node
    parent.appendChild(node);
    // fade in
    Animate(this._questionBox, 'fadeIn');
  }

  /**
   * Updates a question by updating the timer.
   *
   * @param question
   * @private
   */
  _handleUpdateQuestion({ remaining }){
    // update timer
    if(this._interval) this._clearInterval();
    this._remainingTime = remaining;
    this._remainingTimeRAF = remaining;
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
    cancelAnimationFrame(this._raf);
    if(this._interval) this._clearInterval();
    // fadeout
    Animate(this._questionBox, 'fadeOut');
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
      node.classList.add('correct', 'faster');
      Animate(selector, 'pulse', () => {
        node.classList.add('fast');
        node.classList.remove('correct', 'faster');
      })
    } else {
      node.classList.add('incorrect', 'faster');
      Animate(selector, 'shake', () => {
        node.classList.add('fast');
        node.classList.remove('incorrect', 'faster');
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
   * Handles the assignAnswer event.
   *
   * @param mode
   * @param answers
   * @private
   */
  _handleAssignAnswers({ mode, answers }){
    /*
    const container = document.querySelector(this._answers);
    if(!container) return;
    const answerNodes = container.querySelectorAll('.answer');
    if(answerNodes.length > MAX_ANSWERS) console.error("Too many assigned answers");
    let copy = [...this._shuffleAnswers(answers)];
    answerNodes.forEach(_node => {
      const nodeId = _node.getAttribute('data-id');
      copy = copy.filter(a => nodeId !== this._getAnswerId(a.questionId, a.answerId));
    });
    copy.forEach(a => {
      container.appendChild(this._renderAnswer(a, mode));
      Animate(`[data-id="${this._getAnswerId(a.questionId, a.answerId)}"]`, 'fadeIn');
    });
    */
    /*
    const container = document.querySelector(this._answers);
    if(!container) return;

    // get current answers
    let copy = [...this._shuffleAnswers(answers)];
    const answerNodes = container.querySelectorAll('.answer');
    if(answerNodes.length > MAX_ANSWERS) console.error("Too many assigned answers");

    // clear inactive boxes
    answerNodes.forEach(node => {
      const nodeId = node.getAttribute('data-id');
      let answer = copy.find(a => nodeId === this._getAnswerId(a.questionId, a.answerId));
      if(answer === undefined){
        node.parentElement.classList.add('empty');
        node.parentElement.innerHTML = '';
        console.error('cleared by assign')
      } else {
        copy = copy.filter(a => nodeId !== this._getAnswerId(a.questionId, a.answerId));
      }
    });

    // get answer boxes
    const answerBoxes = container.querySelectorAll(this._answerBoxSelector);
    if(answerBoxes.length > MAX_ANSWERS) console.error("Too many answer boxes");
    // for each box
    answerBoxes.forEach(b => {
      // check if _empty or old answer
      let answer = b.querySelector('[data-id]');
      let newAnswer = copy.pop();
      console.warn(answer, newAnswer, !answer && newAnswer, b);
      if(!answer && newAnswer){  // if yes: clear innerHTML and append one of the new children
        b.appendChild(this._renderAnswer(newAnswer, mode));
        b.classList.remove('empty');
        Animate(`[data-id="${this._getAnswerId(newAnswer.questionId, newAnswer.answerId)}"]`, 'fadeIn');
      }
    });

    // if new answers left: error too many
    if(copy.length > 0) console.error("Too many assigned answers");
    */

    console.log('called assign');

    let copy = [...this._shuffleAnswers(answers)];
    let emptyBoxes = [];
    this._answerBoxes.forEach(box => {
      let answerId = box.getAnswerId();
      console.warn(answerId, JSON.stringify(copy));
      if(answerId === '' || copy.find(a => answerId === this._getAnswerId(a.questionId, a.answerId)) === undefined){
        console.warn('EMPTY')
        box.clear();
        emptyBoxes.push(box);
      } else {
        console.warn('NON-EMPTY')
        copy = copy.filter(a => answerId !== this._getAnswerId(a.questionId, a.answerId));
      }
    });

    copy.forEach(a => {
      console.warn('trying to add answer', a);
      if(emptyBoxes.length <= 0) throw new Error('Too many Questions assigned');
      let box = emptyBoxes.pop();
      box.assign(this._renderAnswer(a, mode));
      console.warn('answer should be added');
    })
  }

  /**
   * Handles the removeAnswer event.
   *
   * @param answers
   * @private
   */
  _handleRemoveAnswers({ answers }){
    /*
    const container = document.querySelector(this._answers);
    if(!container) return;
    const answerNodes = container.querySelectorAll('.answer');
    if(answerNodes.length > MAX_ANSWERS) console.error("Too many assigned answers");
    let copy = [...answers];
    answerNodes.forEach(_node => {
      const nodeId = _node.getAttribute('data-id');
      let answer = copy.find(a => nodeId === this._getAnswerId(a.questionId, a.answerId));
      if(answer === undefined){
        _node.style.pointerEvents = 'none';
        Animate(`[data-id="${nodeId}"]`, 'fadeOut', () => {
          container.removeChild(_node);
        });

      } else {
        copy = copy.filter(a => nodeId !== this._getAnswerId(a.questionId, a.answerId));
      }
    });
    */
    /*
    const container = document.querySelector(this._answers);
    if(!container) return;
    const answerNodes = container.querySelectorAll('.answer');
    if(answerNodes.length > MAX_ANSWERS) console.error("Too many assigned answers");
    let copy = [...answers];
    answerNodes.forEach(node => {
      const nodeId = node.getAttribute('data-id');
      let answer = copy.find(a => nodeId === this._getAnswerId(a.questionId, a.answerId));
      if(answer === undefined){ // check if still active
        node.style.pointerEvents = 'none';
        Animate(`[data-id="${nodeId}"]`, 'fadeOut', () => {
          let oldNode = container.querySelector(`[data-id="${nodeId}"]`);
          if(oldNode) {
            oldNode.parentElement.classList.add('empty');
            oldNode.parentElement.innerHTML = '';
          }
          console.error('cleared by remove')
        });

      } else {
        copy = copy.filter(a => nodeId !== this._getAnswerId(a.questionId, a.answerId));
      }
    });
    // get current answers
    // for each answer

        // if no, animate + callback: if _node still exists, delete _node
    */
    let copy = [...answers];
    this._answerBoxes.forEach(box => {
      let answerId = box.getAnswerId();
      if(answerId === '' || copy.find(a => answerId === this._getAnswerId(a.questionId, a.answerId)) === undefined){
        box.clear();
      } else {
        copy = copy.filter(a => answerId !== this._getAnswerId(a.questionId, a.answerId));
      }
    })
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
    answerNodes.forEach(_node => {
      const nodeId = _node.getAttribute('data-id');
      let answer = copy.find(a => nodeId === this._getAnswerId(a.questionId, a.answerId));
      if(answer === undefined){
        toRemove.push(_node);
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

    document.addEventListener('assignAnswer', (e) => this._handleAssignAnswers(e.detail.game));
    document.addEventListener('removeAnswer', (e) => this._handleRemoveAnswers(e.detail.game));

    document.addEventListener('jokerUse', this._jokerUseAnimation.bind(this));
    const joker = document.querySelector(this._joker);
    if(joker) joker.addEventListener('click', this._handleJokerUse.bind(this));

    const container = document.querySelector(this._answers);
    if(container) container.innerHTML = '';
    for(let i = 0; i < MAX_ANSWERS; i++){
      this._answerBoxes.push(new AnswerBox(container));
    }
  }
}

const gc = new GameController();
gc.init();