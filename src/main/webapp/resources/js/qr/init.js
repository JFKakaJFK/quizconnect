"use strict";

/* ======================== INIT ========================= */

connect();

// TODO only send if in timeout? or add client activity event?
document.addEventListener('click', cancelTimeout);
document.addEventListener('touchstart', cancelTimeout);
document.addEventListener('mousemove', cancelTimeout);


// 1. getGameInfo

// 2. setAlivePings

/* IF STATE IS LOBBY */

// 4. get and show Players

//getRoomPlayers();

// Wait for game start, update view on ready | player join

/* IF STATE IS INGAME */

// 4. Wait for question & answer assignment

// Wait for answer events | update view on server events

// Based on states

// Wrap state change -> diff old & newstate & rerender differences

// install event listeners for all player actions at given time