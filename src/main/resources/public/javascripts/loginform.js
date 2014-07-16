define(['jquery', 'ajaxhelper'], function($, aj) {
    $(function () {

        var loginDiv = $(".login");
        var loginForm = $("#loginform");
        var formError = $(".formerror");
        var usernameField = $("#username");
        var passwordField = $("#password");
        var loginSpan = $("#loginlogout");
        var loginLink = $("#loginlink a");
        var logoutLink = $("#logoutlink a");
        var cancelLogin = $("#logincancel");

        loginLink.click(function() {
            usernameField.val("");
            passwordField.val("");
            formError.html("");
            loginDiv.show();
            usernameField.focus();
            return false;
        });

        logoutLink.click(function() {
            window.location.href = "/logout";
        })

        loginForm.submit(function() {
            var username = usernameField.val();
            var password = passwordField.val();
            if (username.length == 0) {
                formError.html("Missing username");
                return false;
            }
            if (password.length == 0) {
                formError.html("Missing password");
                return false;
            }
            aj.postJson("/login", {
                    username: username,
                    password: password
                }, {
                    success: function(result) {
                        formError.html("");
                        loginDiv.hide();
                        window.location.href = "/";
                    },
                    error: function(status) {
                        if (status == 404) {
                            formError.html("Invalid username or password")
                        } else {
                            formError.html("Oops, there was an error processing your login, please try again later");
                        }
                    }
                }
            );
            return false;
        });

        cancelLogin.click(function() {
            loginDiv.hide();
            return false;
        });
    });
})
