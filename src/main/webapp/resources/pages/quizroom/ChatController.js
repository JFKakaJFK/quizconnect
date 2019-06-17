"use strict";

// on every change to the childlist of message container, scroll to bottom
const MESSAGES = document.getElementById('messages');
let chatObserver = new MutationObserver(() => {MESSAGES.scrollTo(0, MESSAGES.scrollHeight)});
chatObserver.observe(MESSAGES, {childList: true}); // TODO

class ChatController {
  constructor(){

  }

  init(){

  }
}
