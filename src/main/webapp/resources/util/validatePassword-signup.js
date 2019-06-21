$(document).ready(function () {
    $("#signupform").validate({
        onkeyup: function(element) {$(element).valid()},
        errorPlacement: function(error, element) {
            // Append error within linked label
            $( element )
                .closest( "form" )
                .find( "label[for='" + element.attr( "id" ) + "']" )
                .append( error );
        },
        errorElement: "span",


        rules: {
            "signupform:username": {
                required: true,
                email: true
            },

            "signupform:reg_password": {
                required: true,
                minlength: 5
            },

            "signupform:confirm_password": {
                required: true,
                equalTo: "#reg_password"

            }

        },
        messages: {

            "signupform:username": {
                required: " (Please provide an email)",
                email: " (Please enter a valid email)"
            },
            "signupform:reg_password": {
                required: " (Please provide a password)",
                minlength: " (Min. {0} characters necessary)"
            },

            "signupform:confirm_password": {
                required: " (Repeat your password)",
                equalTo: " (Your passwords do not match)"
            }
        }
    });

});