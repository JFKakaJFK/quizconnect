"use strict";

import { INFO_MSG } from "./Constants.js";
import setState, { getState } from "./State.js";
import Client from './Socket.js';
import Animate from "./Animate.js";

/**
 * Handles everything regarding the chat.
 */
class ChatController {
  constructor(){
    this._chatSelector = '#messages';
    this._chatInput = '[data-chat-input]';
    this._observer = null;
  }

  /**
   * Helper function to escape HTML code in strings
   *
   * @param unsafeText
   * @returns {string}
   */
  _escapeHTML(unsafeText){
    let div = document.createElement('div');
    div.innerText = unsafeText;
    return div.innerHTML;
  };

  /**
   * Handles a single chat message.
   *
   * @param message
   * @private
   */
  _handleMessage(message){
    const messages = document.querySelector(this._chatSelector);
    if(!messages) return;
    const { id: pid } = getState();
    const { message: text, from, playerId, id, timestamp } = message;
    let ts = new Date(timestamp);
    let msg = document.createElement('div');
    msg.classList.add('chat-message');
    msg.setAttribute('data-id', id.toString());

    if(playerId === pid) msg.classList.add('chat-outgoing');
    if(from === INFO_MSG) msg.classList.add('chat-info');

    let escapedMessage = this._escapeHTML(text);
    // markdown style formatting for bold, italics, strikethrough and links
    escapedMessage = escapedMessage.replace(/[\*\_]{2}([^\*\_]+)[\*\_]{2}/g, '<b>$1</b>');
    escapedMessage = escapedMessage.replace(/[\*\_]{1}([^\*\_]+)[\*\_]{1}/g, '<i>$1</i>');
    escapedMessage = escapedMessage.replace(/[\~]{2}([^\~]+)[\~]{2}/g, '<del>$1</del>');
    escapedMessage = escapedMessage.replace(/[\[]{1}([^\]]+)[\]]{1}[\(]{1}([^\)\"]+)(\"(.+)\")?[\)]{1}/g, '<a href="https://$2" title="$4">$1</a>');

    msg.innerHTML = `<p class="chat-message-content">${escapedMessage}</p><p class="chat-meta">${from === INFO_MSG ? '' : `<span class="chat-from">${this._escapeHTML(from)}</span> `}
    <span class="chat-timestamp">${ts.getHours().toString().padStart(2, '0')}:${ts.getMinutes().toString().padStart(2, '0')}:${ts.getSeconds().toString().padStart(2, '0')}</span></p>`;

    messages.appendChild(msg);
    Animate(`${this._chatSelector} [data-id="${id.toString()}"]`, 'fadeIn');
  }

  /**
   * Handles state changes.
   *
   * @param newState
   * @private
   */
  _handleStateChange(newState){
    const { messageQueue } = newState;
    messageQueue.forEach(msg => this._handleMessage(msg));
    if(messageQueue.length > 0) setState({messageQueue:[]});
  }

  /**
   * Handles chat input.
   *
   * @private
   */
  _handleChatInput(e){
    if(e.key === 'Enter') {
      e.target.value = e.target.value.trim();
      if(e.target.value !== '') Client.sendChatMessage(e.target.value.substring(0, 100));
      e.target.value = '';
    }
  }

  /**
   * Initializes the chat controller
   */
  init(){
    // listen for state changes
    document.addEventListener('stateChange', (e) => this._handleStateChange(e.detail));
    // listen for chat input
    const input = document.querySelector(this._chatInput);
    if(input) input.addEventListener('keyup', (e) => this._handleChatInput(e));

    const messages = document.querySelector(this._chatSelector);
    if(!messages) return;
    // on every change to the childlist of message container, scroll to bottom
    this._observer = new MutationObserver(() => {messages.scrollTo(0, messages.scrollHeight)});
    this._observer.observe(messages, {childList: true});
  }
}

const cc = new ChatController();
cc.init();
