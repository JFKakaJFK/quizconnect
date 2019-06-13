"use strict";

/* --------- GENERAL -------- */
const DOMAIN = 'quizconnect.rocks';

/* --------- GAME STATES -------- */
const LOBBY = 0;
const INGAME = 1;
const FINISHED = 2;
const JOIN = 3;

/* --------- URLS & ENDPOINTS --------- */
const JOIN_ENDPOINT = '/qr/join/';
const URL_JOIN = '/quizroom/join.html';
const URL_FINISH = '/quizroom/final.html';
const URL_KICKED = '/player/home.xhtml?kicked=true';
const URL_LEAVE = '/player/home.xhtml?leave=true';

/* --------- GAME STATES -------- */
const MAX_ANSWERS = 6;

const ANSWERTYPE_TEXT = 'text';
const ANSWERTYPE_PICTURE = 'picture';
const ANSWERTYPE_MATH = 'math';

const PREFIX_ANSWER_PICTURE = (path) => `/answers/${path}`;

/* --------- SOCIAL -------- */
const SHARE_WHATSAPP = (text) => `https://wa.me/?text=${encodeURIComponent(text)}`;
const SHARE_TWITTER = (text) => `https://twitter.com/intent/tweet?hashtags=quiz,quizconnect&related=bitconnect&text=${encodeURIComponent(text)}`;
const SHARE_FACEBOOK = (url) => `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}&t=QuizConnect`;
const SHARE_JOIN_URL = (pin) => `https://www.${DOMAIN}/quizroom/join.html?pin=${pin.toString().padStart(6, '0')}`; // TODO www subdomain // pls https
const SHARE_PIN_MESSAGE = (pin) =>  `Your QuizConnect pin is ${pin.toString().padStart(6, '0')}:

${SHARE_JOIN_URL(pin)}`;

/* --------- CHAT -------- */
const INFO_MSG = "INFO"; // TODo renamed

export {
  DOMAIN,
  LOBBY,
  INGAME,
  FINISHED,
  JOIN,
  JOIN_ENDPOINT,
}