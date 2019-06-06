window.onload=function() {
    var strength = {
        0: "Worst",
        1: "Bad",
        2: "Weak",
        3: "Good",
        4: "Strong"
    }


    var password = document.querySelector('[type=password]'); //selects the first input-field of type password
    var meter = document.getElementById('password-strength-meter');
    var text = document.getElementById('password-strength-text');

    password.addEventListener('input', function () {
        var val = password.value;
        var result = zxcvbn(val);

        // Update the meter
        meter.value = result.score;
        // Update the text indicator
        if (val !== "") {
            text.innerHTML = "Strength: " + "<strong>" + strength[result.score] + "</strong>" + "<br/>" + (result.feedback.warning ? result.feedback.warning + "<br/>" : '') + (result.feedback.suggestions ? result.feedback.suggestions : '') + "</span>";
        } else {
            text.innerHTML = "";
        }
    });
}