$( function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS(sock);


    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {text: $("#txt-chat").val()}
        ))
        $("#txt-chat").val('')
        $("#chat-container").animate( { scrollTop: $("#chat-container").height() }, "fast" );

    }

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)

        $("#prog").hide();

        // Handle errors
        if(data.error) {

            //Close socket
            chatSocket.close()

            $("#onError span").text(data.error)
            $("#onError").show()
            return
        } else {
            $("#chat-container").show();
            $("#members-col").show();
            $("#chatarea").show();

        }

        if( data.kind == "membersUpdate") {
            // Update the members list
            $("#members").html('')
            $(data.members).each(function() {
                $("#members").append('<tr><td>' + this + '</td></tr>')
            });

            return;
        }



        // Create the message element
        var el = $(
                '<table class="message-line table">' +
                ' 	<tbody>'  +
                '		<tr>' +
                '			<td class="usr"></td>' +
                '			<td class="msg"></td>' +
                '			<td class="muted time"></td>' +
                '		</tr>' +
                '	</tbody>'  +
                '</table>'
        );

        //var el = $('<div class="message-line"><span></span><p></p><small class="mute"></small></div>')

        var dng = data.message;




        $(".usr", el).text(data.user);
        $(".msg", el).text(dng);
        $(".time", el).text("12:00 AM");
        $(el).addClass(data.kind);

        if(data.user == user) {
            $(el).addClass('me')
        }

        if(data.kind == "join" || data.kind == "quit") {
            $("span", el).hide();
            $("p", el).css("padding-left", "60px");
        }

        $(data.options).each(function() {
            if(this == "decodehtml") {
                $(".msg", el).html($('<div/>').html(data.message));
            }
        });

        $('#chat-list').append(el)


    }

    var handleReturnKey = function(e) {


        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            sendMessage()
        }
    }

    $("#txt-chat").keypress(handleReturnKey)
    $("#btn-chat").click(sendMessage);

    chatSocket.onmessage = receiveEvent

})
