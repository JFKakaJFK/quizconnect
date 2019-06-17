"use strict";

/**
 * The fastest way to change the textContent of an element.
 *
 * @param node
 * @param text
 */
const setSimpleText = (node, text) => {
  if(node) node.textContent = text;
};

/**
 * The fastest way to change escaped text with layout.
 *
 * @param node
 * @param text
 */
const setLayoutText = (node, text) => {
  if(node && node.innerText !== text) node.innerText = text;
};

/**
 * The fastest way to replace the html content of a node.
 *
 * @param node
 * @param html
 */
const dangerouslySetHTML = (node, html) => {
  if(node && node.innerHTML !== html) node.innerHTML = html;
};

/**
 * Removes all children from the node.
 *
 * @param node
 */
const clearNode = (node) => {
  let lc = node.lastChild;
  while(lc){
    node.removeChild(lc);
    lc = node.lastChild;
  }
};

/**
 * A simple hash function, not cryptographically sound, but enough to scramble strings a bit.
 *
 * @param s
 * @return {Number}
 */
const hash = (string) => {
  for(var i = 0, h = 0xdeadbeef; i < s.length; i++)
    h = Math.imul(h ^ s.charCodeAt(i), 2654435761);
  return (h ^ h >>> 16) >>> 0;
};

/**
 * A simple function for verifying hashes.
 *
 * @param string
 * @param hashed
 * @return {boolean}
 */
const verify = (string, hashed) => {
  return hash(string) === hashed;
};

/**
 * Returns a random number within min/max
 *
 * @param min
 * @param max
 * @return {Number}
 * @private
 */
const random = (min, max) => {
  return Math.random() * (max - min) + min;
};

export {
  setSimpleText,
  setLayoutText,
  dangerouslySetHTML,
  clearNode,
  hash,
  verify,
  random,
}