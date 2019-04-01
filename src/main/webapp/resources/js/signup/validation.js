$(document).ready(function () {
    var validator = $("#signupform").validate({

        errorLabelContainer: "#errorcontainer",
        wrapper: "li",
        rules: {
            reg_password: {
                required: true,
                minlength: 6
            },

            reg_repeatpassword: {
                required: true,
                minlength: 6,
                equalTo: "#reg_password"
            }

        },
        messages: {
            reg_password: {
                required: "Provide a password",
                minlength: jQuery.validator.format("Enter at least {0} characters")
            },
            reg_repeatpassword: {
                required: "Repeat your password",
                minlength: jQuery.validator.format("Enter at least {0} characters"),
                equalTo: "Your passwords do not match"
            }
        },

    });
});