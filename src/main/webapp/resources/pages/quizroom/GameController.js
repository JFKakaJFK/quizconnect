"use strict";

import { FINISHED, INGAME, ANSWERTYPE_MATH, ANSWERTYPE_TEXT, ANSWERTYPE_PICTURE, MAX_ANSWERS, PREFIX_ANSWER_PICTURE } from "./Constants.js";
import Client from './Socket.js';
import {JOKER_REUSE_BUFFER} from "./Constants.js";
import { getState } from "./State.js";
import Animate from './Animate.js';
import { setLayoutText, dangerouslySetHTML, setSimpleText, hash, verify } from "./Utils.js";

let animRunning = false;
const animateScore = (node, oldScore, newScore) => {
  if(animRunning) return;
  animRunning = true;
  let change = oldScore > newScore ? 'decrease' : 'increase';

  let score = {
    score: oldScore,
  };

  anime({
    targets: score,
    score: newScore,
    round: 1,
    easing: 'linear',
    update: function() {
      node.textContent = score.score;
    },
    begin: () => {
      node.classList.add(change);
    },
    complete: () => {
      animRunning = false;
      node.classList.remove(change);
    },
  });
};

/**
 * Handles all game duties.
 */
class GameController {
  constructor(){
    this._question = '#question';  // only works with id
    this._answers = '#answers';
    this._joker = '#joker';
    this._score = '[data-score]';
    this._allowJokerUse = true;
    this._updating = false;
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
      Client.useJoker(); // todo fadeout + fadein of question + answers (but for all users)
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
   * Displays the current question text.
   *
   * @param question
   * @param mode
   * @private
   */
  _renderQuestion({ mode, question }){ // todo timer
    let elem = document.querySelector(this._question);
    if(!elem || question === null) return;
    const { questionId, type, question: qtext } = question;
    if(elem.getAttribute('data-qid') !== questionId.toString()){
      Animate(this._question, 'fadeOut', () => {
        // check if the node type is correct
        if(mode === 'reverse' && type === ANSWERTYPE_PICTURE && elem.nodeName.toLowerCase() !== 'img'){
          let picture = document.createElement('img');
          picture.alt = 'question';
          elem.parentNode.replaceChild(picture, elem);
          elem = picture;
          elem.id = this._question;
          elem.setAttribute('class', 'fast text-center question');
        } else if(elem.nodeName.toLowerCase() === 'h2'){
          let temp = document.createElement('h2');
          elem.parentNode.replaceChild(temp, elem);
          elem = temp;
          elem.id = this._question;
          elem.setAttribute('class', 'fast text-center question');
        }

        if(type === ANSWERTYPE_MATH){
          const temp = document.createElement('span');
          katex.render(qtext, temp, { throwOnError: false });
          dangerouslySetHTML(elem, temp.innerHTML);
        } else if(mode === 'reverse' && type === ANSWERTYPE_PICTURE) {
          elem.src = qtext;
        } else {
          setLayoutText(elem, qtext);
        }
        elem.setAttribute('data-qid', questionId.toString());
        Animate(this._question, 'fadeIn');
      });
    }
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


        //console.warn('prev',  prev);
        //let obj = { score: 0 };
        /*
        anime({
          target: SCORE,
          score: score,
          round: 1,
          easing: 'linear',
          autoplay: true,
          update: () => {
            setSimpleText(s, SCORE.score);
            console.warn(SCORE.score, score);
          },
          begin: () => s.classList.add(change),
          complete: () => s.classList.remove(change),
        });
        */
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

    /*
    const container = document.querySelector(this._answers);
    if(!container) return;
    const answerNodes = container.querySelectorAll('.answer');
    if(answerNodes.length > MAX_ANSWERS) console.error("Too many assigned answers");
    let copy = [...answers];
    answerNodes.forEach(node => {
      const nodeId = node.getAttribute('data-id');
      let answer = copy.find(a => nodeId === this._getAnswerId(a.questionId, a.answerId));
      if(answer === undefined){
        container.removeChild(node);
      }
      copy = copy.filter(a => nodeId !== this._getAnswerId(a.questionId, a.answerId));
    });

    copy.forEach(a => {
      if(Math.random() > 0.5){
        container.appendChild(this._renderAnswer(a, mode));
      } else {
        container.insertBefore(this._renderAnswer(a, mode), container.firstChild);
      }
      Animate(`[data-id="${this._getAnswerId(a.questionId, a.answerId)}"]`, 'fadeIn');
    });
     */
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
    this._renderQuestion(game);
    this._renderAnswers(game);
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
    document.addEventListener('jokerUse', this._jokerUseAnimation.bind(this));
    const joker = document.querySelector(this._joker);
    if(joker) joker.addEventListener('click', this._handleJokerUse.bind(this));
  }
}

const gc = new GameController();
gc.init();