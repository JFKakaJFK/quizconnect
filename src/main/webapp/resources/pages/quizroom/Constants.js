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
const URL_HOME = '/login.xhtml';

/* --------- GAME STUFF -------- */
const MAX_ANSWERS = 6;
const JOKER_REUSE_BUFFER = 1000; // time in ms until joker can be used again
const SERVER_REFRESH_RATE = 1000; // timers are synced every 1000ms
const ANSWERTYPE_TEXT = 'text';
const ANSWERTYPE_PICTURE = 'picture';
const ANSWERTYPE_MATH = 'math';

const PREFIX_ANSWER_PICTURE = (path) => `/answers/${path}`;

/* --------- SOCIAL -------- */
const SHARE_WHATSAPP = (text) => `https://wa.me/?text=${encodeURIComponent(text)}`;
const SHARE_TWITTER = (text) => `https://twitter.com/intent/tweet?hashtags=quiz,quizconnect&related=bitconnect&text=${encodeURIComponent(text)}`;
const SHARE_FACEBOOK = (url) => `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(url)}&t=QuizConnect`;
const SHARE_JOIN_URL = (pin) => `https://www.${DOMAIN}/quizroom/join.html?pin=${pin.toString().padStart(6, '0')}`;
const SHARE_PIN_MESSAGE = (pin) =>  `Your QuizConnect pin is ${pin.toString().padStart(6, '0')}:

${SHARE_JOIN_URL(pin)}`;

/* --------- CHAT -------- */
const INFO_MSG = "INFO";

/* --------- SOCKET ------- */
const WS_FALLBACK = '/ws';
const WS_SOURCE = '/server/events';
const WS_TARGET = '/qc/events';
const ALIVE_PING_PERIOD = 450; // interval for alivePings in ms
const ACTIVITY_CHECK_PERIOD = 10000; // interval for checking any activity in ms
const PERIODIC_ACTIVITY_PINGS = false; // periodically send cancelTimeout if any activiy is detected?

export {
  DOMAIN,
  LOBBY,
  INGAME,
  FINISHED,
  JOIN,
  JOIN_ENDPOINT,
  WS_SOURCE,
  WS_FALLBACK,
  WS_TARGET,
  ALIVE_PING_PERIOD,
  ACTIVITY_CHECK_PERIOD,
  PERIODIC_ACTIVITY_PINGS,
  JOKER_REUSE_BUFFER,
  URL_HOME,
  INFO_MSG,
  SHARE_FACEBOOK,
  SHARE_TWITTER,
  SHARE_WHATSAPP,
  SHARE_PIN_MESSAGE,
  SHARE_JOIN_URL,
  MAX_ANSWERS,
  ANSWERTYPE_MATH,
  ANSWERTYPE_PICTURE,
  ANSWERTYPE_TEXT,
  PREFIX_ANSWER_PICTURE,
  SERVER_REFRESH_RATE,
}