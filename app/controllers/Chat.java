package controllers;

import models.ChatRoom;
import models.User;
import org.codehaus.jackson.JsonNode;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.chatRoom;
import views.html.index;

public class Chat extends Controller {

	final static Form<User> userForm = form( User.class );

	/**
	 * Display the chat room.
	 */
	public static Result verify () {
		Form<User> mForm = userForm.bindFromRequest();

		if ( mForm == null ) {
			return badRequest();
		}

		if ( mForm.hasErrors() ) {
			return badRequest( index.render( mForm ) );
		}

        //Only username and pass are not null
		User user = mForm.get();

		user.ipAddress = request().remoteAddress();

        if ( user.username == null || user.username.trim().equals( "" ) ) {
            flash( "error", "Please enter a valid username." );
            return badRequest( index.render( mForm ) );
        }

        if ( user.password == null || user.password.trim().equals( "" ) ) {
            flash( "error", "Please enter a valid password." );
            return badRequest( index.render( mForm ) );
        }

        if(User.find.where().eq("username", user.username).findList().size() == 0){
            //User no exist. so make them

            User.registerUser(user);
        } else {
            //This is very stupid. We need to hash. k? k.
            if(User.find.where().eq("username", user.username).eq("password", user.password).findList().size() == 0) {
                flash( "error", "Password is incorrect" );
                return badRequest( index.render( mForm ) );
            }

        }

		User.addOnlineUser( user );
		return ok( chatRoom.render( Long.valueOf( user.ID ) ) );
	}


	/**
	 * Handle the chat websocket.
	 */
	public static WebSocket<JsonNode> chat ( final String ID ) {
		final User mUser = User.getOnlineUserById(Long.valueOf(ID));

		if ( mUser == null ) {
			return null;
		}

		return new WebSocket<JsonNode>() {

			// Called when the Websocket Handshake is done.
			@Override
			public void onReady ( WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out ) {

				// Join the chat room.
				try {
					ChatRoom.join( mUser, in, out );
				} catch ( Exception ex ) {
					ex.printStackTrace();
				}
			}
		};
	}

}
