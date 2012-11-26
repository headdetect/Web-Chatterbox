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

public class Application extends Controller {

	final static Form< User > userForm = form( User.class );

	/**
	 * Display the home page.
	 */
	public static Result index() {
		return ok( index.render( userForm ) );
	}

	/**
	 * Display the chat room.
	 */
	public static Result navigateChatRoom() {
		Form< User > mForm = userForm.bindFromRequest();

		if ( mForm == null ) {
			return badRequest();
		}

		if ( mForm.hasErrors() ) {
			return badRequest( index.render( mForm ) );
		}

		User user = mForm.get();
		
		user.ipAddress = request().remoteAddress();
		
		if(User.exists(user)){
			flash( "error" , "Username is already taken." );
			return badRequest( index.render( mForm ) );
		}

		if ( user.username == null || user.username.trim().equals( "" ) ) {
			flash( "error" , "Please choose a valid username." );
			return badRequest( index.render( mForm ) );
		}
		
		User.addUser(user);
		return ok( chatRoom.render( Long.valueOf(user.ID) ) );
	}

	/**
	 * Handle the chat websocket.
	 */
	public static WebSocket< JsonNode > chat( final String ID ) {
		final User mUser = User.findUserById( Long.valueOf( ID ) ) ;
		
		return new WebSocket< JsonNode >() {

			// Called when the Websocket Handshake is done.
			@Override
			public void onReady( WebSocket.In< JsonNode > in , WebSocket.Out< JsonNode > out ) {

				// Join the chat room.
				try {
					ChatRoom.join( mUser , in , out );
				} catch ( Exception ex ) {
					ex.printStackTrace();
				}
			}
		};
	}

}
