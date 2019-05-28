"use strict";

const escapeHTML = (unsafeText) => {
  let div = document.createElement('div');
  div.innerText = unsafeText;
  return div.innerHTML;
};