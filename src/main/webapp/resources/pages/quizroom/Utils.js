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

export {
  setSimpleText,
  setLayoutText,
  dangerouslySetHTML,
  clearNode,
}