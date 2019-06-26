"use strict";

import { FINISHED, INGAME, ANSWERTYPE_MATH, ANSWERTYPE_PICTURE, MAX_ANSWERS, PREFIX_ANSWER_PICTURE } from "./Constants.js";
import Client from './Socket.js';
import {JOKER_REUSE_BUFFER} from "./Constants.js";
import { getState } from "./State.js";
import Animate from './Animate.js';
import { setLayoutText, setSimpleText } from "./Utils.js";
import AnswerBox from './AnswerBox.js';

/**
 * Handles all game duties.
 */
class GameController {
  constructor(){
    this._questionBox = '.ingame-question';
    this._question = '#question';
    this._answers = '#answers';
    this._answerBoxes = [];
    this._joker = '#joker';
    this._score = '[data-score]';
    this._allowJokerUse = true;

    this._time = '#timer';
    this._totalTime = 0;
    this._animation = null;
    this._oldTimeStamp = false;
    this._remainingTime = 0;

    this._DELAY = 800; // animation delay in ms
    this._removing = false;
  }

  /**
   * Handles the use of a joker.
   *
   * @private
   */
  _handleJokerUse(){
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
   * Animates the timer by setting the timer progress.
   * @private
   */
  _animateTimer(timeStamp){
    if(!this._oldTimeStamp) this._oldTimeStamp = timeStamp;
    let delta = timeStamp - this._oldTimeStamp;
    this._oldTimeStamp = timeStamp;

    this._remainingTime -= delta;

    let remaining = (this._remainingTime / this._totalTime) * 100;

    const elem = document.querySelector(this._time);
    if(elem) elem.style.transform = 'translate3d(' + (-100 + remaining) + '%,0,0)'; // shift left

    if(remaining >= 0){
      requestAnimationFrame(this._animateTimer.bind(this));
    } else {
      cancelAnimationFrame(this._animation);
      this._oldTimeStamp = false;
    }
  }

  /**
   * Handles the assignQuestion event.
   *
   * @param state
   * @private
   */
  _handleAssignQuestion(state){
    const {
      info: { mode },
      game: { question },
    } = state; // get mode and question from state

    if(question === null) throw new Error('Question cannot be null');
    const parent = document.querySelector(this._question);
    if(!parent) throw new Error('Question box not found');
    parent.parentElement.style.backgroundImage = ''; // clear picture questions
    parent.parentElement.style.opacity = '0';

    const { type, question: qtext, remaining } = question;
    // set timer
    this._remainingTime = remaining;
    this._totalTime = remaining;
    this._oldTimeStamp = false;
    this._animation = requestAnimationFrame(this._animateTimer.bind(this));
    // create _node
    let node;
    if(mode === 'reverse' && type === ANSWERTYPE_PICTURE && (qtext.toLowerCase().endsWith('.png') || qtext.toLowerCase().endsWith('.jpg'))){
      parent.parentElement.style.backgroundImage = `url('${PREFIX_ANSWER_PICTURE(qtext)}')`;
    } else {
      node = document.createElement('h2');
      node.setAttribute('class', 'text-center question question-text');
      if(type === ANSWERTYPE_MATH){
        katex.render(qtext, node, { throwOnError: false });
      } else {
        setLayoutText(node, qtext);
      }
    }

    // if fading out, wait a little
    setTimeout(() => {
      // clear parent
      parent.innerHTML = '';
      // append _node
      if(node) parent.appendChild(node);
      // fade in
      Animate(this._questionBox, 'fadeIn');
      parent.parentElement.style.opacity = '';
    }, this._removing ? this._DELAY : 0);
  }

  /**
   * Updates a question by updating the timer.
   *
   * @param question
   * @private
   */
  _handleUpdateQuestion({ remaining }){
    // update timer
    this._remainingTime = remaining;
  }

  /**
   * Removes a question by removing the timer and deleting the question.
   *
   * @private
   */
  _handleRemoveQuestion(){
    this._removing = true;
    // destroy timer
    cancelAnimationFrame(this._animation);
    // fadeout
    Animate(this._questionBox, 'fadeOut', () => this._removing = false);
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
    return questionId.toString() + answerId.toString();
  }

  /**
   * Renders a single answer.
   *
   * @param answer
   * @return {HTMLElement}
   * @private
   */
  _renderAnswer(answer){
    if(!answer) return null;
    const { questionId, type, answerId, answer: text } = answer;
    const answerNode = document.createElement('div');
    answerNode.classList.add('box', 'answer', 'fast', 'answer-' + type, 'text-center'); //, 'answer-order-' + Math.floor(Math.random() * 9));
    answerNode.setAttribute('data-id', this._getAnswerId(questionId, answerId));
    answerNode.addEventListener('click', () => this._handleAnswerQuestion(answerNode, questionId, answerId));
    let inner;
    switch (type){
      case ANSWERTYPE_MATH:
        inner = document.createElement('p');
        katex.render(text, inner, { throwOnError: false });
        break;
      case ANSWERTYPE_PICTURE:
        if(text.toLowerCase().endsWith('.png') || text.endsWith('.jpg')){
          answerNode.style.backgroundImage = `url('${PREFIX_ANSWER_PICTURE(text)}')`;
          break;
        }
        // fall through if not an image, is more tolerant and handles reverse mode well
      default:
        inner = document.createElement('p');
        inner.innerText = text;
    }
    if(inner) answerNode.appendChild(inner);
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
   * @param answers
   * @private
   */
  _handleAssignAnswers({ answers }){
    let copy = [...this._shuffleAnswers(answers)];
    let emptyBoxes = [];
    this._answerBoxes.forEach(box => {
      let answerId = box.getAnswerId();
      if(answerId === '' || copy.find(a => answerId === this._getAnswerId(a.questionId, a.answerId)) === undefined){
        box.clear();
        emptyBoxes.push(box);
      } else {
        copy = copy.filter(a => answerId !== this._getAnswerId(a.questionId, a.answerId));
      }
    });

    copy.forEach(a => {
      if(emptyBoxes.length <= 0) throw new Error('Too many Questions assigned');
      let box = emptyBoxes.pop();
      box.assign(this._renderAnswer(a));
    })
  }

  /**
   * Handles the removeAnswer event.
   *
   * @param answers
   * @private
   */
  _handleRemoveAnswers({ answers }){
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

    document.addEventListener('assignQuestion', (e) => this._handleAssignQuestion(e.detail));
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