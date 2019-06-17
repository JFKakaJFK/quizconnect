$(document).ready(function () {
    $("#passwordForm").validate({
        errorLabelContainer: "#errorcontainer",
        wrapper: "li",
        rules: {
            "passwordForm:editManager:password": {
                required: true,
                minlength: 5
            },

            "passwordForm:editManager:repeat_password": {
                required: true,
                equalTo: "#passwordForm:editManager:password"
            }

        },
        messages: {
            "passwordForm:editManager:password": {
                required: "Provide a password",
                minlength: "Enter at least 5 characters for your password"
            },

            "passwordForm:editManager:repeat_password": {
                required: "Repeat your password",
                equalTo: "Your passwords do not match"
            }
        }
    });
});