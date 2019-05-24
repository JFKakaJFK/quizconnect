"use strict";

/* ======================== CONSTANTS ========================= */

const DOMAIN = 'localhost:8080'; // TODO
// TODO change url destinations if routes change

const LOBBY = 0;
const INGAME = 1;
const FINISHED = 2;
const JOIN = 3;

const JOIN_ENDPOINT = '/qr/join/';
const URL_JOIN = '/quizroom/join.html';
const URL_FINISH = '/quizroom/final.html';
const URL_KICKED = '/player/home.xhtml?kicked=true';
const URL_LEAVE = '/player/home.xhtml?leave=true';

const MAX_ANSWERS = 6;

const ANSWERTYPE_TEXT = 'text';
const ANSWERTYPE_PICTURE = 'picture';

const PREFIX_ANSWER_PICTURE = (path) => `/answers/${path}`;

const SHARE_PIN_WHATSAPP = (pin) =>  `Your QuizConnect pin is ${pin.toString().padStart(6, '0')}:

http://www.${DOMAIN}/quizroom/join.html?pin=${pin.toString().padStart(6, '0')}`; // TODO www subdomain doesnt work for localhost (of couse) but WA needs it to detect it as link

// TODO share links for twitter, tinder, hooli, piedpiper, email, ...