$(document).ready(function () {
    $("#passwordChange").validate({
        errorLabelContainer: "#errorcontainer",
        wrapper: "li",
        rules: {
            "password": {
                required: true,
                minlength: 5
            },

            "repeat_password": {
                required: true,
                equalTo: "#password"
            }

        },
        messages: {
            "password": {
                required: "Provide a password",
                minlength: "Enter at least 5 characters for your password"
            },

            "repeat_password": {
                required: "Repeat your password",
                equalTo: "Your passwords do not match"
            }
        }
    });
});