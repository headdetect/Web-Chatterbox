!function ($) {

    "use strict"; // jshint ;_;


    $(function () {


        var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket;
        var chatSocket = new WS(sock);

        var sendMessage = function () {
            chatSocket.send(JSON.stringify(
                {text: $("#txt-chat").val()}
            ));
            $("#txt-chat").val('');
            

        };


        var count = 0;

        var receiveEvent = function (event) {
            var data = JSON.parse(event.data);

            $("#prog").hide();

            // Handle errors
            if (data.error) {

                //Close socket
                chatSocket.close();

                $("#onError span").text(data.error);
                $("#onError").show();
            } else {
                $("#chat-container").show();
                $("#members-col").show();
                $("#chatarea").show();
            }

            if (data.kind === "membersUpdate") {
                // Update the members list
                $("#members").html('');

                $(data.members).each(function () {
                    $("#members").append('<tr><td>' + this + '</td></tr>');

                });

                return;
            }


            // Create the message element
            var el = $(
                    '<table id="tbl' + count + '" class="message-line table">' +
                    ' 	<tbody>' +
                    '		<tr>' +
                    '			<td class="usr"></td>' +
                    '			<td class="msg"></td>' +
                    '			<td class="time"></td>' +
                    '		</tr>' +
                    '	</tbody>' +
                    '</table>'
            );

            $(".usr", el).text(data.user);

            if (data.user === "") {
                $(".usr", el).remove();
                $(".msg", el).css({
                    'left': '0',
                    'padding-left': '160px'
                });
                $(".msg", el).addClass("muted");
            }

            handleMessage($(".msg", el), data.message);

            $(".time", el).text(useFullTime === true ? Date.parse('now').toString("HH:mm") : Date.parse('now').toString("hh:mm tt"));
            $(el).addClass(data.kind);

            if (data.user === user) {
                $(el).addClass('me');
            }

            if (data.kind === "join" || data.kind === "quit") {
                $("span", el).hide();
                $("p", el).css("padding-left", "60px");
            }

            $(data.options).each(function () {
                if (this === "decodehtml") {
                    $(".msg", el).html($('<div/>').html(data.message));
                }
            });

            $('#chat-list').append(el);
			$("#chat-container").animate({ scrollTop: $("#chat-container")[0].scrollHeight }, "fast");
            //Fix the crap stuff k?

            var tbl = $("#tbl" + count);
            var biggestHeight = $(".msg", tbl).innerHeight();

            tbl.height(biggestHeight);
            $(".usr", tbl).height(biggestHeight - 15 /*padding*/ - 1 /* border-top */);
            count++;
        };

        var handleReturnKey = function (e) {


            if (e.charCode === 13 || e.keyCode === 13) {
                e.preventDefault();
                sendMessage();
            }
        };

        var regex = new RegExp("\\([a-zA-Z]{1,25}\\)");

        var handleMessage = function (dom, msg) {

            var pending = msg.split(' ');

            for (var i in pending) {
                var element = $("<div/>").text(pending[i]).html();

                //isnt emote
                if (!element.match(regex)) {
                    dom.append(element + " ");
                    continue;
                }

                var domImage = getImg(element);
                if (domImage === false) {
                    dom.append(element);
                    return;
                }

                dom.append(domImage);
            }

        };

        var getImg = function (src) {

            src = src.slice(1, -1);

            src = $('<div/>').text(src).html();

            if (urlExists(imgur + src + ".png")) {
                return "<img src='" + imgur + src + ".png' />&nbsp;";
            }

            if (urlExists(imgur + src + ".gif")) {
                return "<img src='" + imgur + src + ".gif' />&nbsp;";
            }

            return false;
        }

        $("#txt-chat").keypress(handleReturnKey);
        $("#btn-chat").click(sendMessage);

        chatSocket.onmessage = receiveEvent;


        // ---------------
        // - Utils.js
        // --------------

        function urlExists(testUrl) {
            var http = jQuery.ajax({
                type: "GET",
                url: testUrl,
                async: false
            })
            return http.status != 404;
        }

    });

}(window.jQuery);


