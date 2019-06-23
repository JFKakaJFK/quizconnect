/**
 * Helper function for indicating password strength.
 *
 * @param inputSelector
 * @param meterSelector
 * @param strengthSelector
 * @param modalSelector
 */
function _pwhelp(inputSelector, meterSelector, strengthSelector, modalSelector){
  const STRENGTH = {
    0: "Worst",
    1: "Bad",
    2: "Weak",
    3: "Good",
    4: "Strong"
  };

  const modal = document.querySelector(modalSelector);
  const elem = modal.querySelector(inputSelector);
  const text = modal.querySelector(strengthSelector);
  if(!elem || !text) throw new Error('Selector(s) invalid');

  text.innerText = "Please start entering a password to show some hints regarding the strength of it";

  elem.addEventListener('input', function (e) {
    const modal = document.querySelector(modalSelector);
    const inputElement = modal.querySelector(inputSelector);
    const meterElement = modal.querySelector(meterSelector);
    const strengthElement = modal.querySelector(strengthSelector);
    if(!inputElement || !meterElement || !strengthElement) throw new Error('Selector(s) invalid');

    let value = e.target.value;
    let result = zxcvbn(value);
    // Update the meter
    meterElement.value = result.score;
    // Update the text indicator
    if (value !== "") {
      strengthElement.innerHTML = `<span>Strength: <strong>${STRENGTH[result.score]}</strong><br/>${result.feedback.warning ? result.feedback.warning + '<br/>' : ''}${result.feedback.suggestions ? result.feedback.suggestions : ''}</span>`;
    } else {
      strengthElement.innerText = '';
    }
  });
}

/**
 * Helper function for indicating password strength.
 *
 * @param inputSelector
 * @param meterSelector
 * @param strengthSelector
 * @param modalSelector
 */
function pwhelp(modalSelector, inputSelector = '[data-js-new-password]', meterSelector = '[data-js-meter]', strengthSelector = '[data-js-strength]') {
  _pwhelp(inputSelector, meterSelector, strengthSelector, modalSelector);
  $(modalSelector).on('shown.bs.modal', function () {
    _pwhelp(inputSelector, meterSelector, strengthSelector, modalSelector);
  });
}