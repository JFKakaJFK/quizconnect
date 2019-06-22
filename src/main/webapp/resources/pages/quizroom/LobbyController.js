"use strict";
import { LOBBY, INGAME, FINISHED, SHARE_WHATSAPP, SHARE_PIN_MESSAGE, SHARE_TWITTER, SHARE_FACEBOOK } from "./Constants.js";
import { setSimpleText, clearNode, dangerouslySetHTML } from "./Utils.js";
import Client from "./Socket.js";
import Animate from './Animate.js';
import Countdown from './Countdown.js';

const readyIcon = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="feather feather-check-circle join-icon"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>`;
const unreadyIcon = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-circle join-icon"><circle cx="12" cy="12" r="10"></circle></svg>`;
/**
 * Renders lobby consisting of game info, share modal and players in the lobby
 */
class LobbyController {
  constructor(options = {}){
    const defaults = {
      sets: '[data-lobby-sets]',
      mode: '[data-lobby-mode]',
      difficulty: '[data-lobby-difficulty]',
      pin: '[data-pin]',
      players: '[data-lobby-players]',
      share_wa: '[data-share-whatsapp]',
      share_tw: '[data-share-twitter]',
      share_fb: '[data-share-fb]',
      readyNum: '[data-ready-num]',
      playersNum: '[data-players-num]',
    };
    this._options = Object.assign(defaults, options);
    this._shareRendered = false;
    this._allReady = false;
  }

  /**
   * Handles new state by rendering the lobby accordingly
   *
   * @param newState
   * @private
   */
  _handleStateChange(newState){
    const { state, info, id } = newState;
    this._renderPin(info.pin);
    if(state !== LOBBY || this._allReady) return;
    if(state === INGAME || state === FINISHED) this.destroy();
    this._renderInfo(info);
    this._renderPlayers(info, id);
    this._renderShare(info);
  }

  /**
   * Shows a countdown before the game starts.
   *
   * @private
   */
  _showCountdown(){
    if(!this._allReady) return;
    // fade out stuff
    Animate('#lobby', 'fadeOut', () => {
      const container = document.getElementById('lobby');
      clearNode(container); // remove stuff
      const node = document.createElement('div');
      node.setAttribute('id', 'countdown');
      node.classList.add('countdown');
      container.appendChild(node); // add countdown
      const countdown = new Countdown({
        selector: '#countdown',
        values: ['ready', 'set', 'go'],
      });
      countdown.start(); // start countdown
    });
  }

  /**
   * Handles the all ready event.
   * @private
   */
  _handleAllReady(){
    if(this._allReady) return;
    this._allReady = true;
    this._showCountdown();
  }

  /**
   * Renders the game info.
   *
   * @param info
   * @private
   */
  _renderInfo(info){
    // render game mode
    const modes = document.querySelectorAll(this._options.mode);
    modes.forEach(m => setSimpleText(m, info.mode));
    // render game difficulty
    const diffs = document.querySelectorAll(this._options.difficulty);
    diffs.forEach(d => setSimpleText(d, info.difficulty));
    // render questionsets
    const qsets = document.querySelectorAll(this._options.sets);
    if(info.mode === 'mathgod'){
      qsets.forEach(set =>  set.parentNode.parentNode.removeChild(set.parentNode));
    } else {
      qsets.forEach(set => {
        dangerouslySetHTML(set, info.questionSets.map(qs => `<h3>${qs}</h3>`).join(''));
      })
    }
  }

  /**
   * Displays the confirm ready modal and sets its action.
   *
   * @private
   */
  _displayLastReadyUpModal(){
    // open modal
    let modal = $('#confirmReady');
    modal.modal({backdrop: 'static'});
    // add event listener to ready button
    let rdyBtn = document.getElementById('confirmReadyBtn');
    rdyBtn.addEventListener('click', Client.readyUp);
    // on close remove the event listener again
    modal.on('hide.bs.modal', () => {
      rdyBtn.removeEventListener('click', Client.readyUp);
    });
  }

  /**
   * Either renders the ready up action or a ready icon.
   *
   * @param parent
   * @param canReadyUp
   * @param isReady
   * @param isPlayer
   * @param lastReadyUp
   * @private
   */
  _renderReady(parent, canReadyUp = false, isReady = false, lastReadyUp = false, isPlayer = false){
    // check ready status -> compare current ready action & rendered action, if change render player
    // compare html strings & render only if necessary
    let node = parent.querySelector('[data-ready]');
    if(!node){
      if(!canReadyUp) return;
      node = document.createElement('div');
      if(isPlayer) node.setAttribute('title', 'Ready Up');
      node.setAttribute('data-ready', 'false');
      parent.appendChild(node);
    } else {
      if(!canReadyUp){
        parent.removeChild(node);
      } else {
        if(isPlayer){
          if(isReady){
            node.removeEventListener('click', Client.readyUp);
            node.removeEventListener('click', this._displayLastReadyUpModal);
            node.removeAttribute('title');
          } else {
            if(lastReadyUp) node.removeEventListener('click', Client.readyUp);
            node.addEventListener('click', lastReadyUp ? this._displayLastReadyUpModal : Client.readyUp);
          }
          node.setAttribute('data-action', (!isReady).toString());
        }
        let ready = node.getAttribute('data-ready') === 'true';
        if(ready !== isReady){
          node.setAttribute('data-ready', isReady.toString());
        }
      }
    }
    dangerouslySetHTML(node, isReady ? readyIcon : unreadyIcon);
  }

  /**
   * Renders a single new Player.
   *
   * @param player
   * @param canReadyUp
   * @param lastReadyUp
   * @param isPlayer
   * @return {string}
   * @private
   */
  _renderPlayer(player, canReadyUp = false, lastReadyUp = false, isPlayer = false){
    const temp = document.createElement('div');
    this._renderReady(temp, canReadyUp, player.ready, lastReadyUp, isPlayer);

    return `<div class="box collection-box" data-id="${player.id}">
        <div class="collection-box-icon "><img src="${player.avatar}" alt="avatar"></div>
        <div class="box-actions">${temp.innerHTML}</div>
        <span class="collection-box-name">${player.username}</span>
        <span class="collection-box-sub">username</span>
    </div>`;
  }

  /**
   * Updates an already rendered player.
   *
   * @param node
   * @param player
   * @param canReadyUp
   * @param lastReadyUp
   * @param isPlayer
   * @private
   */
  _updatePlayer(node, player, canReadyUp = false, lastReadyUp = false, isPlayer = false){
    let parent = node.querySelector('.box-actions');
    this._renderReady(parent, canReadyUp, player.ready, lastReadyUp, isPlayer);
  }

  /**
   * Renders all players in the game.
   *
   * @param info
   * @param id
   * @private
   */
  _renderPlayers(info, id){
    if(info.players.length === 0) return;
    const pss = document.querySelectorAll(this._options.players);
    let canReadyUp = info.players.length > 1;
    let lastReadyUp = canReadyUp && info.players.filter(p => !p.ready).length === 1;

    pss.forEach(ps => { // doing the work for each container (there is only 1) allows for fine grained rendering
      let copy = [ ...info.players ];
      let playerNodes = ps.querySelectorAll('[data-id]');

      playerNodes.forEach(node => {
        let pid = parseInt(node.getAttribute('data-id'));
        let player = copy.find(p => p.id === pid);

        if(player){ // check if anything to do
          this._updatePlayer(node, player, canReadyUp, lastReadyUp, player.id === id);
        } else { // delete players who left
          Animate(`${this._options.players} [data-id="${pid}"]`, 'fadeOut', () =>  ps.removeChild(node));
        }
        // remove player from copy (make search space smaller)
        copy = copy.filter(p => p.id !== pid);
      });

      // add new players
      if(copy.length > 0){
        copy.forEach(p => {
          ps.innerHTML += this._renderPlayer(p, canReadyUp, lastReadyUp, p.id === id);
          Animate(`${this._options.players} [data-id="${p.id}"]`, 'fadeIn');
        });
      }
    });
    // animate change in number of ready players
    const readys = document.querySelectorAll(this._options.readyNum);
    let readyn = info.players.filter(p => p.ready).length.toString();
    readys.forEach(r => {
      if(r.textContent !== readyn){
        Animate(this._options.readyNum, 'fadeOut', () => {
          r.textContent = readyn;
          Animate(this._options.readyNum, 'fadeIn');
        })
      }
    });
    // animate change in total number of players
    const players = document.querySelectorAll(this._options.playersNum);
    let text = info.players.length.toString();
    players.forEach(p => {
      if(p.textContent !== text){
        Animate(this._options.readyNum, 'fadeOut', () => {
          p.textContent = text;
          Animate(this._options.readyNum, 'fadeIn');
        })
      }
    });
  }

  /**
   * Renders the rooms pin.
   *
   * @param pin
   * @private
   */
  _renderPin(pin){
    if(!pin) return;
    const pins = document.querySelectorAll(this._options.pin);
    pins.forEach(p => setSimpleText(p, pin.toString().padStart(6, '0')));
  }

  /**
   * Renders the share actions for the lobby.
   *
   * @param info
   * @private
   */
  _renderShare(info){
    if(this._shareRendered) return;
    // fb share links
    const sfbs = document.querySelectorAll(this._options.share_fb);
    sfbs.forEach(a => a.href = SHARE_FACEBOOK(SHARE_JOIN_URL(info.pin.toString().padStart(6, '0'))));
    // twitter share links
    const stws = document.querySelectorAll(this._options.share_tw);
    stws.forEach(a => a.href = SHARE_TWITTER(SHARE_PIN_MESSAGE(info.pin.toString().padStart(6, '0'))));
    // whats app share links
    const swas = document.querySelectorAll(this._options.share_wa);
    swas.forEach(a => a.href = SHARE_WHATSAPP(SHARE_PIN_MESSAGE(info.pin.toString().padStart(6, '0'))));
    this._shareRendered = true;
  }

  /**
   * Stop to listen to any state change events.
   */
  destroy(){
    document.removeEventListener('stateChange', (e) => this._handleStateChange(e.detail));
    document.removeEventListener('allReady', this._handleAllReady.bind(this));
  }

  /**
   * Begin to listen to state changes.
   */
  init(){
    // listen for state changes
    document.addEventListener('stateChange', (e) => this._handleStateChange(e.detail));
    document.addEventListener('allReady', this._handleAllReady.bind(this));
  }
}

const renderer = new LobbyController();
renderer.init();