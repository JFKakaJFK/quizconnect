$(document).ready(function () {
    $("#addPlayerForm").validate({
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

            "addPlayerForm:addPlayer:new_username": {
                required: true,
                minlength: 3
            },

            "addPlayerForm:addPlayer:new_password": {
                required: true,
                minlength: 5
            },

            "addPlayerForm:addPlayer:repeat_new_password": {
                required: true,
                equalTo: "#new_password"
            }

        },
        messages: {
            "addPlayerForm:addPlayer:new_username": {
                required: " (Please provide an username)",
                minlength: " (Min. {0} characters necessary)"
            },

            "addPlayerForm:addPlayer:new_password": {
                required: " (Please provide a password)",
                minlength: " (Min. {0} characters necessary)"
            },

            "addPlayerForm:addPlayer:repeat_new_password": {
                required: " (Repeat your password)",
                equalTo: " (Your passwords do not match)"
            }
        }
    });





    $("#editPlayerForm").validate({
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

            "editPlayerForm:editPlayer:new_password_edit": {
                required: true,
                minlength: 5
            },

            "editPlayerForm:editPlayer:repeat_new_password_edit": {
                required: true,
                equalTo: "#new_password_edit"
            }

        },
        messages: {

            "editPlayerForm:editPlayer:new_password_edit": {
                required: " (Please provide a password)",
                minlength: " (Min. {0} characters necessary)"
            },

            "editPlayerForm:editPlayer:repeat_new_password_edit": {
                required: " (Repeat your password)",
                equalTo: " (Your passwords do not match)"
            }
        }
    });
});