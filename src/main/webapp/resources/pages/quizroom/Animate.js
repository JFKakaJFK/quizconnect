"use strict";

/**
 * Animate an element using Daniel Eden's Animate.css.
 *
 * @param element (Selector|Node)
 * @param animationName
 * @param callback
 */
export default (element, animationName, callback) => {
  let node;
  if(element instanceof HTMLElement){
    node = element;
  } else if(typeof element === 'string'){
    node = document.querySelector(element);
    if(!node) throw new Error('Selector "' + element + '" is invalid');
  } else {
    throw new Error('Element of type "' + typeof element + '" is invalid');
  }
  node.classList.add('animated', animationName);

  function handleAnimationEnd() {
    node.classList.remove('animated', animationName);
    node.removeEventListener('animationend', handleAnimationEnd);

    if (typeof callback === 'function') callback();
  }

  node.addEventListener('animationend', handleAnimationEnd);
}