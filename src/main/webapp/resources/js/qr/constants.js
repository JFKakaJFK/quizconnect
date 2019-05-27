"use strict";

/* ======================== CONSTANTS ========================= */

const DOMAIN = 'quizconnect.rocks'; // TODO
// TODO change url destinations if routes change

const INFO = "INFO";

// States
const LOBBY = 0;
const INGAME = 1;
const FINISHED = 2;
const JOIN = 3;

// needed URLS
const JOIN_ENDPOINT = '/qr/join/';
const URL_JOIN = '/quizroom/join.html';
const URL_FINISH = '/quizroom/final.html';
const URL_KICKED = '/player/home.xhtml?kicked=true';
const URL_LEAVE = '/player/home.xhtml?leave=true';

const MAX_ANSWERS = 6;

const ANSWERTYPE_TEXT = 'text';
const ANSWERTYPE_PICTURE = 'picture';
const ANSWERTYPE_MATH = 'math';

const PREFIX_ANSWER_PICTURE = (path) => `/answers/${path}`;

const SHARE_PIN_WHATSAPP = (pin) =>  `Your QuizConnect pin is ${pin.toString().padStart(6, '0')}:

const SHARE_WHATSAPP = (text) => {
  return `https://wa.me/?text=${encodeURIComponent(text)}`;
};

const SHARE_TWITTER = (text) => {
  return `https://twitter.com/intent/tweet?hashtags=quiz,quizconnect&related=bitconnect&text=${encodeURIComponent(text)}`;
};

const SHARE_FACEBOOK = (url) => {
  return `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}&t=QuizConnect`;
};

const SHARE_JOIN_URL = (pin) => {
  return `http://www.${DOMAIN}/quizroom/join.html?pin=${pin.toString().padStart(6, '0')}`; // TODO www subdomain // pls https
};

const SHARE_PIN_MESSAGE = (pin) =>  `Your QuizConnect pin is ${pin.toString().padStart(6, '0')}:

${SHARE_JOIN_URL(pin)}`;
