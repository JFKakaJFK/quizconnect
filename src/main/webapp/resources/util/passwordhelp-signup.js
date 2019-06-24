document.addEventListener('DOMContentLoaded', function() {
    const strength = {
        0: "Worst",
        1: "Bad",
        2: "Weak",
        3: "Good",
        4: "Strong"
    };


    const password = document.querySelector('[type=password]'); //selects the first input-field of type password
    const meter = document.getElementById('password-strength-meter');
    const text = document.getElementById('password-strength-text');

    text.innerHTML = "Please start entering a password to show some hints regarding the strength of it";

    password.addEventListener('input', function () {
        let val = password.value;
        let result = zxcvbn(val);

        // Update the meter
        meter.value = result.score;
        // Update the text indicator
        if (val !== "") {
            text.innerHTML = "Strength: " + "<strong>" + strength[result.score] + "</strong>" + "<br/>" + (result.feedback.warning ? result.feedback.warning + "<br/>" : '') + (result.feedback.suggestions ? result.feedback.suggestions : '') + "</span>";
        } else {
            text.innerHTML = "";
        }
    });
});
