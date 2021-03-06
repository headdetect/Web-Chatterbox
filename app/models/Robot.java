package models;

import akka.actor.ActorRef;
import chatterbox.events.EventHandler;
import chatterbox.events.Listener;
import chatterbox.events.Priority;
import chatterbox.events.chatroom.onTalkEvent;
import chatterbox.utils.Logger;
import models.ChatRoom.Talk;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonNode;
import play.libs.Json;
import play.mvc.WebSocket;

public class Robot implements Listener {

	public User mUser;
    private ActorRef mChatRoom;

	public Robot ( ActorRef chatRoom ) {
        mUser = new User( "Robot" );
        mUser.ID = 2;
        User.registerUser( mUser );

		// Join the room
		chatRoom.tell( new ChatRoom.Join( mUser.ID ) );
		mChatRoom = chatRoom;

		User.addOnlineUser(mUser);
		ChatRoom.events.registerEvents( this );
	}

	@EventHandler (priority = Priority.High)
	public void onChat ( onTalkEvent event ) {
		final Talk talk = event.getTalk();
		String message = talk.text.toLowerCase();

		/*
		 * We Don't want any echos now do we.
		 */
		if ( talk.getUser() == mUser ) {
			Logger.log("Robot recieved echo");
			return;
		}

		if ( message == null || message.trim().isEmpty() ) {
			return;
		}

		if ( message.equals( "hi mr robot" ) || message.equalsIgnoreCase( "hi robot" ) ) {
			reply( "Hi " + talk.getUser().username + "!" );
			return;
		}

		if ( message.equals( "i love you robot" ) || message.equalsIgnoreCase( "i love you mr robot" ) ) {
			reply( "How nice. Be assured the feeling is mutual" );
			return;
		}

		if ( message.startsWith( "mr robot" ) ) {
			String query = message.substring( message.indexOf( "mr robot" ), message.length() );
			handleCommand( talk, query );
		} else {
			if ( message.startsWith( "robot" ) ) {
				String query = message.substring( message.indexOf( "robot" ), message.length() );
				handleCommand( talk, query );
			} else {
				if ( message.startsWith( "hey robot" ) ) {
					String query = message.substring( message.indexOf( "hey robot" ), message.length() );
					handleCommand( talk, query );
				}
			}
		}
	}

	private void handleCommand ( Talk talk, String query ) {

		if ( query.contains( "who am i" ) ) {
			reply( "I believe you are " + talk.getUser().username );
		} else if ( query.contains( "i am your father" ) || query.contains( "i am ur father" ) ) {
				reply( "NOOOOOOOO" );
			} else {
				if ( query.contains( "what is my ip" ) ) {
					reply( "Looks like your IP is " + talk.getUser().ipAddress + ". But I could be wrong." );
				} else {
					String cleaned = StringEscapeUtils.escapeHtml4( query.substring( query.indexOf( ' ' ) + 1, query.length() ).trim().replace( "+", "%2B" ).replace( ' ', '+' ) );
					reply( "<a href=\"http://lmgtfy.com/?q=" + cleaned + "\">I don't know what to say to that </a>", "decodehtml" );
				}
			}
	}

	private void reply ( String message, Object... options ) {
		if ( mChatRoom != null && message != null ) {
			mChatRoom.tell( new ChatRoom.Talk( mUser.ID, message, options ) );
		}
	}

}