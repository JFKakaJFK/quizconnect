$(document).ready(function () {
    var validator = $("#signupform").validate({

        errorLabelContainer: "#errorcontainer",
        wrapper: "li",
        rules: {

            "signupform:institution": {
                required: true,
                minlength: 3
            },

            "signupform:reg_password": {
                required: true,
                minlength: 6
            },

            "signupform:username": {
                required: true,
                email: true,
            },

            "signupform:confirm_password": {
                required: true,
                equalTo: "#reg_password"
            }

        },
        messages: {
            "signupform:institution":  {
                required: "Enter a Institution"
            },

            "signupform:username": {
                required: "Enter a valid e-mail"
            },

            "signupform:reg_password": {
                required: "Provide a password",
                minlength: jQuery.validator.format("Enter at least {0} characters for your password")
            },

            "signupform:confirm_password": {
                required: "Repeat your password",
                //minlength: jQuery.validator.format("Enter at least {0} characters"),
                equalTo: "Your passwords do not match"
            }
        },

    });
});