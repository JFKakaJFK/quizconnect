"use strict";

// on every change to the childlist of message container, scroll to bottom
const MESSAGES = document.getElementById('messages');
let chatObserver = new MutationObserver(() => {MESSAGES.scrollTo(0, MESSAGES.scrollHeight)});
chatObserver.observe(MESSAGES, {childList: true});

const showChatMessage = (message) => {
  setState({
    messages: state.messages.concat([{
      message,
      from: INFO,
      id: -state.messages.length,
      timestamp: new Date().valueOf(),
    }]),
  })
};