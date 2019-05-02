"use strict";

/* ======================== CONSTANTS ========================= */

const LOBBY = 0;
const INGAME = 1;
const FINISHED = 2;

const URL_JOIN = '/quizroom/join.html';
const URL_FINISH = '/quizroom/final.html';
const URL_KICKED = '/player/home.xhtml?kicked=true';

const MAX_ANSWERS = 6;

const ANSWERTYPE_TEXT = 'text';
const ANSWERTYPE_PICTURE = 'picture';