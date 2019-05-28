document.addEventListener('DOMContentLoaded', () => {
  const inputs = document.querySelectorAll('[data-number-only]');
  inputs.forEach(e => { // remove leading zeroes and any non numeric characters BACKEND VALIDATION STILL NEEDED
    e.addEventListener('change', ({ target }) => target.value = target.value.replace(/^0+/, '').replace(/[^\d]/gi, ''));
    e.addEventListener('input', ({ target }) => target.value = target.value.replace(/^0+/, '').replace(/[^\d]/gi, ''));
  });
});