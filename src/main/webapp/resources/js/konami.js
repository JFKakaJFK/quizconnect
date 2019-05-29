var konami = [38, 38, 40, 40, 37, 39, 37, 39, 66, 65, 13];
var konamiCounter = 0;
document.addEventListener('keydown', function(e) {
    var key = e.which;
    if (key === konami[konamiCounter]) {
        var currentKonami = konami[konamiCounter];
        setTimeout(function() {
            if (konamiCounter <= currentKonami) konamiCounter = 0;
        }, 3000);
        console.log(konamiCounter);
        konamiCounter++;
        if (konamiCounter >= konami.length) {
            //TODO make it do something
        }
    } else {
        konamiCounter = 0;
    }
}, false);