package models;

import chatterbox.core.user.Permission;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.ArrayList;

public class User {

	// ===========================================================
	// Constants
	// ===========================================================

	public static User SYSTEM = new User( "System" );
	// ===========================================================
	// Fields
	// ===========================================================
	private static ArrayList<User> users = new ArrayList<User>();
	// TODO: Replace with dynamically adjusting value based on user's past IDs
	private static long currentID = 0;
	public String username;
	public String password;
	public long ID;
	public String ipAddress;
	public Permission permission = Permission.Member;
	public WebSocket.Out<JsonNode> outSocket;
	public WebSocket.In<JsonNode> inSocket;

	// ===========================================================
	// Constructors
	// ===========================================================

	public User ( String string ) {
		this();
		this.username = string;
	}

	public User () {
		this.ID = currentID++;
	}

	static {
		User.addUser( SYSTEM );
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public static User findUserById ( long ID ) {
		for ( int i = 0; i < users.size(); i++ ) {
			if ( users.get( i ).ID == ID )
				return users.get( i );
		}
		return null;
	}

	public static User findUserByUsername ( String username ) {
		for ( int i = 0; i < users.size(); i++ ) {
			if ( users.get( i ).username.equals( username ) )
				return users.get( i );
		}
		return null;
	}

	public static boolean exists ( User user ) {
		return users.contains( user );
	}

	public static void addUser ( User user ) {
		users.add( user );

		System.out.println( "New user added: " + user.username + ", ID=" + user.ID + ", IP=" + user.ipAddress );

	}

	public static void removeUser ( User user ) {
		if ( users.contains( user ) ) {
			users.remove( user );
		}

	}

	public static ArrayList<User> getUsers () {
		return users;
	}

	// -------- Utils ------------

	public static void sendGlobalMessage ( String kind, User from, String message, Object... options ) {
		for ( User user : users ) {
			user.sendMessage( kind, from, message, options );
		}
	}

	public static void sendGlobalMessage ( User from, String msg ) {
		sendGlobalMessage( "talk", from, msg );
	}

	public static void sendGlobalMessage ( String message ) {
		sendGlobalMessage( User.SYSTEM, message );
	}

	public static void sendListUpdate () {
		for ( User user : users ) {

			if ( user == SYSTEM || user == Robot.mUser || user.outSocket == null ) {
				// we don't care about the user list. k?
				continue;
			}

			ObjectNode event = Json.newObject();
			event.put( "kind", "membersUpdate" );

			// Add all members to array node
			ArrayNode mNode = event.putArray( "members" );
			for ( int i = 0; i < users.size(); i++ ) {
				mNode.add( StringEscapeUtils.escapeHtml4( users.get( i ).username ) );
			}

			user.outSocket.write( event );
		}

	}

	public void sendMessage ( String kind, User from, String asText, Object... options ) {
		if ( this == SYSTEM || outSocket == null ) {
			// Just log the messages sent to the system

			System.out.println( kind + " -> (" + from.username + "): " + asText );
			return;
		}

		ObjectNode event = Json.newObject();
		event.put( "kind", kind );
		event.put( "user", from.username );
		event.put( "message", asText );


		ArrayNode mNode = event.putArray( "options" );
		if ( options != null ) {
			for ( int i = 0; i < options.length; i++ ) {
				mNode.add( options[ i ].toString() );
			}
		}

		outSocket.write( event );
	}

	public void sendMessage ( User from, String asText ) {
		sendMessage( "talk", from, asText );
	}

	public void sendMessage ( String text ) {
		sendMessage( this, text );
	}

	public void kick () {
		ChatRoom.defaultRoom.tell( new ChatRoom.Quit( ID ) );
	}

	public void tell ( String message ) {
		ChatRoom.defaultRoom.tell( new ChatRoom.Talk( ID, message ) );
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
