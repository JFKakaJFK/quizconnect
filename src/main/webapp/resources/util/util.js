"use strict";

/**
 * Helper function to escape HTML code in strings
 * 
 * @param unsafeText
 * @returns {string}
 */
const escapeHTML = (unsafeText) => {
  let div = document.createElement('div');
  div.innerText = unsafeText;
  return div.innerHTML;
};