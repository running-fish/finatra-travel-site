define(['jquery'], function($) {
    return {
        postJson: function(url, data, handler) {
            var jsonData = JSON.stringify(data);
            $.ajax({
                type: "POST",
                url: url,
                dataType: "json",
                data: jsonData,
                contentType: "application/json; charset=utf-8",
                success: function(result) {
                    handler.success(result);
                },
                error : function(jqXHR) {
                    handler.error(jqXHR.status);
                }
            });
        }
    }
})
