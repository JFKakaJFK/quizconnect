"use strict";

export default (element, animationName, callback) => {
  const node = document.querySelector(element);
  if(!node) throw new Error("Selector is invalid");
  node.classList.add('animated', animationName);

  function handleAnimationEnd() {
    node.classList.remove('animated', animationName);
    node.removeEventListener('animationend', handleAnimationEnd);

    if (typeof callback === 'function') callback();
  }

  node.addEventListener('animationend', handleAnimationEnd);
}