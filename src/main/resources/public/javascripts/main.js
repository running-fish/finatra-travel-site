(function() {
    $(function () {

        $("#loginlink a").click(function(event) {
            $("#username").val("");
            $("#password").val("");
            $(".formerror").html("");
            $("#login").show();
            $("#username").focus();
            return false;
        });

        $("#logoutlink a").click(function(event) {
            window.location.href = "/logout";
        })

        $("#loginform").submit(function() {
            var username = $("#username").val();
            var password = $("#password").val();
            if (username.length == 0) {
                $(".formerror").html("Missing username");
                return false;
            }
            if (password.length == 0) {
                $(".formerror").html("Missing password");
                return false;
            }
            var url = "/loginJson";
            var data = {
                username: username,
                password: password
            };
            var jsonData = JSON.stringify(data);
            $.ajax({
                type: "POST",
                url: url,
                dataType: "json",
                data: jsonData,
                contentType: "application/json; charset=utf-8",
                statusCode: {
                    200: function() {
                        $(".formerror").html("");
                        window.location.href = "/";
                    },
                    400: function(result) {
                        $(".formerror").html("Oops, there was an error processing your login, please try again later");
                    },
                    404: function() {
                        $(".formerror").html("Invalid username or password")
                    },
                    500: function() {
                        $(".formerror").html("Oops, there was an error processing your login, please try again later");
                    }
                }
            });
            return false;
        });

        $("#logincancel").click(function(event) {
            $("#login").hide();
            return false;
        });
    });
})();

