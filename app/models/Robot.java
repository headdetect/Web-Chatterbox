package models;

import models.ChatRoom.Talk;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Json;
import play.mvc.WebSocket;
import akka.actor.ActorRef;

public class Robot {

	private ActorRef mChatRoom;

	private final static User mUser = new User( "Robot" );

	public Robot( ActorRef chatRoom ) {

		// Create a Fake socket out for the robot that log events to the
		// console.
		WebSocket.Out< JsonNode > robotChannel = new WebSocket.Out< JsonNode >() {

			@Override
			public void write( JsonNode frame ) {
				Logger.of( "robot" ).info( Json.stringify( frame ) );
			}

			@Override
			public void close() {
			}

		};
		
		mUser.outSocket = robotChannel;
		
		// Join the room
		chatRoom.tell( new ChatRoom.Join( mUser ) );
		mChatRoom = chatRoom;
	}

	public void onChat( Talk talk ) {
		String message = talk.text.toLowerCase();

		/*
		 * We Don't want any echos now do we.
		 */
		if ( talk.user == mUser ) {
			return;
		}

		if ( message == null || message.trim().isEmpty() ) {
			return;
		}

		if ( message.equals( "hi mr robot" ) || message.equalsIgnoreCase( "hi robot" ) ) {
			reply( "Hi " + talk.user.username + "!" );
			return;
		}

		if ( message.equals( "i love you robot" ) || message.equalsIgnoreCase( "i love you mr robot" ) ) {
			reply( "How nice. Be assured the feeling is mutual" );
			return;
		}

		if ( message.startsWith( "mr robot" ) ) {
			String query = message.substring( message.indexOf( "mr robot" ) , message.length() );
			handleCommand( talk , query );
		} else if ( message.startsWith( "robot" ) ) {
			String query = message.substring( message.indexOf( "robot" ) , message.length() );
			handleCommand( talk , query );
		} else if ( message.startsWith( "hey robot" ) ) {
			String query = message.substring( message.indexOf( "hey robot" ) , message.length() );
			handleCommand( talk , query );
		}

	}

	private void handleCommand( Talk talk , String query ) {

		if ( query.contains( "who am i" ) ) {
			reply( "I believe you are " + talk.user.username );
		} else if ( query.contains( "i am your father" ) || query.contains( "i am ur father" ) ) {
			reply( "NOOOOOOOO" );
		} else if ( query.contains( "what is my ip" ) ) {
			reply( "Looks like your IP is " + talk.user.ipAddress + ". But I could be wrong." );
		} else {
			String cleaned = query.replace( "+" , "%2B" ).replace( ' ' , '+' );
			reply( "<a href=\"http://lmgtfy.com/?q=" + cleaned + "\">I don't know what to say to that </a>" );
		}
	}

	private void reply( String message ) {
		if ( mChatRoom != null && message != null ) {
			mChatRoom.tell( new ChatRoom.Talk( mUser , message ) );
		}
	}

}