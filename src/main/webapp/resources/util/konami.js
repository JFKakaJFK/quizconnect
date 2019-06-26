const konami = ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight', 'b', 'a'];
let konamiCounter = 0;
document.addEventListener('keydown', function(e) {
  if (e.key.toLowerCase() === konami[konamiCounter].toLowerCase()) {
      let currentKonami = konami[konamiCounter];
      setTimeout(function() {
          if (konamiCounter <= currentKonami) konamiCounter = 0;
      }, 3000);
      konamiCounter++;
      if (konamiCounter >= konami.length) { changeTheme('outrun'); }
  } else {
      konamiCounter = 0;
  }
}, false);