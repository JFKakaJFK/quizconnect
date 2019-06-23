window.onload=function() {
  const strength = {
      0: "Worst",
      1: "Bad",
      2: "Weak",
      3: "Good",
      4: "Strong"
  };


  const password = document.querySelector('[type=password]'); //selects the first input-field of type password
  const password_two = document.getElementById('new_password_edit');

  alert("ONE:" + password.getAttribute("id"));
    alert("TWO:" + password_two.getAttribute("id"));
  const meter = document.getElementById('password-strength-meter');
  const text = document.getElementById('password-strength-text');
  const meter_two = document.getElementById('password-strength-meter_two');
  const text_two = document.getElementById('password-strength-text_two');

  text.innerHTML = "Please start entering a password to show some hints regarding the strength of it";
  text_two.innerHTML = "Please start entering a password to show some hints regarding the strength of it";


  password.addEventListener('input', function () {
      alert("One");
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

    password_two.addEventListener('input', function () {
        alert("Two");
        let val= password_two.value;
        let result = zxcvbn(val);
        // Update the meter
        meter_two.value = result.score;
        // Update the text indicator
        if (val !== "") {
            text_two.innerHTML = "Strength: " + "<strong>" + strength[result.score] + "</strong>" + "<br/>" + (result.feedback.warning ? result.feedback.warning + "<br/>" : '') + (result.feedback.suggestions ? result.feedback.suggestions : '') + "</span>";
        } else {
            text_two.innerHTML = "";
        }
    });
};