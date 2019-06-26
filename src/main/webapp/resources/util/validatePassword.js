$(document).ready(function () {
    $("#passwordForm").validate({
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
            "passwordForm:editPassword:new_password": {
                required: true,
                minlength: 5
            },

            "passwordForm:editPassword:repeat_new_password": {
                required: true,
                equalTo: "#new_password"
            }

        },
        messages: {
            "passwordForm:editPassword:new_password": {
                required: " (Please provide a password)",
                minlength: " (Min. {0} characters necessary)"
            },

            "passwordForm:editPassword:repeat_new_password": {
                required: " (Repeat your password)",
                equalTo: " (Your passwords do not match)"
            }
        }
    });
});