"use strict";

const VOLUME_STEP_SIZE = 1 / 6; // 6 volume levels steps
const VOLUME_ICON_LOW = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-volume lg"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon></svg>`;
const VOLUME_ICON_MED = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-volume-1 lg"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><path d="M15.54 8.46a5 5 0 0 1 0 7.07"></path></svg>`;
const VOLUME_ICON_HIGH = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-volume-2 lg"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><path d="M19.07 4.93a10 10 0 0 1 0 14.14M15.54 8.46a5 5 0 0 1 0 7.07"></path></svg>`;
const VOLUME_ICON_MUTE = `<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-volume-x lg"><polygon points="11 5 6 9 2 9 2 15 6 15 11 19 11 5"></polygon><line x1="23" y1="9" x2="17" y2="15"></line><line x1="17" y1="9" x2="23" y2="15"></line></svg>`;

/**
 * Sets the volume for all pages
 */
const setVolume = (vol) => {
  const sounds = document.querySelectorAll('audio');
  // clamp the volume between 0 and 1
  vol = Math.max(0, vol);
  vol = Math.min(1, vol);
  sounds.forEach(s => s.volume = vol);
  localStorage.setItem('volume', `${vol}`);
  updateIcons();
};

/**
 * Gets the current volume
 */
const getVolume = () => {
  const volume = localStorage.getItem('volume');
  // return 50% as default
  return !volume ? .5 : parseFloat(volume);
};

/**
 * Increases the volume by {@link VOLUME_STEP_SIZE}
 */
const increaseVolume = () => {
  setVolume(getVolume() + VOLUME_STEP_SIZE);
};

/**
 * Decreases the volume by {@link VOLUME_STEP_SIZE}
 */
const decreaseVolume = () => {
  setVolume(getVolume() - VOLUME_STEP_SIZE);
};

/**
 * Mutes all sounds for all pages
 */
const mute = () => {
  localStorage.setItem('muted', 'true');
  const sounds = document.querySelectorAll('audio');
  sounds.forEach(s => s.muted = true);
  updateIcons();
};

/**
 * Unmutes all sounds for all pages
 */
const unmute = () => {
  localStorage.setItem('muted', 'false');
  const sounds = document.querySelectorAll('audio');
  sounds.forEach(s => s.muted = false);
  updateIcons();
};

/**
 * Checks the muted state
 *
 * @returns {boolean}
 */
const isMuted = () => {
  const muted = localStorage.getItem('muted');
  // muted per default
  if(muted === null){
    mute();
    return true;
  }
  return muted === 'true';
};

/**
 * Toggles the muted state for all pages
 */
const toggleMuted = () => {
  isMuted() ? unmute() : mute();
};

/**
 * Plays a sound
 *
 * @param selector
 */
const playSound = (selector) => {
  const sound = document.querySelector(selector);
  if(!sound || isMuted()){
    return;
  }
  sound.volume = getVolume();
  sound.currentTime = 0;
  sound.play();
};

const updateIcons = () => {
  const icons = document.querySelectorAll('[data-volume-icon]');
  let vol = getVolume();
  icons.forEach(i => {
    if(isMuted()){
      i.innerHTML = VOLUME_ICON_MUTE;
    } else {
      if(vol < 0.3){
        i.innerHTML = VOLUME_ICON_LOW;
      } else if(vol < 0.6){
        i.innerHTML = VOLUME_ICON_MED;
      } else {
        i.innerHTML = VOLUME_ICON_HIGH;
      }
    }
  })
};

// init volume slider in right position
const volumeSliders = document.querySelectorAll('[data-volume-icon]');
volumeSliders.forEach(s => s.value = getVolume());
const volumeIcons = document.querySelectorAll('[data-volume-icon]');
volumeIcons.forEach(i => i.addEventListener('click', toggleMuted));
updateIcons();