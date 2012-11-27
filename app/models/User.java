package models;

import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Play;
import play.data.validation.Constraints.Required;
import play.libs.Json;
import play.mvc.WebSocket;
import core.User.Permission;

public class User {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private static ArrayList< User > users = new ArrayList< User >();

	// TODO: Replace with dynamically adjusting value based on user's past IDs
	private static long currentID = 0;

	public static User SYSTEM = new User( "System" );

	static {
		User.addUser( SYSTEM );
	}

	@Required
	public String username;

	public long ID;

	public String ipAddress;

	public Permission permission = Permission.Member;

	public WebSocket.Out< JsonNode > outSocket;

	public WebSocket.In< JsonNode > inSocket;

	// ===========================================================
	// Constructors
	// ===========================================================

	public User( String string ) {
		this();
		this.username = string;
	}

	public User() {
		this.ID = currentID++;
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

	public void sendMessage( String kind , User from , String asText ) {
		if ( this == SYSTEM || outSocket == null ) {
			// Just log the messages sent to the system

			System.out.println( kind + " -> (" + from.username + "): " + asText );
			return;
		}

		ObjectNode event = Json.newObject();
		event.put( "kind" , kind );
		event.put( "user" , from.username );
		event.put( "message" , asText );

		outSocket.write( event );
	}

	public void sendMessage( User from , String asText ) {
		sendMessage( "talk" , from , asText );
	}

	public void sendMessage( String text ) {
		sendMessage( this , text );
	}

	public void kick() {
		ChatRoom.defaultRoom.tell( new ChatRoom.Quit( ID ) );
	}

	public void tell( String message ) {
		ChatRoom.defaultRoom.tell( new ChatRoom.Talk( ID , message ) );
	}

	// -------- Utils ------------

	public static User findUserById( long ID ) {
		for ( int i = 0; i < users.size(); i++ ) {
			if ( users.get( i ).ID == ID )
				return users.get( i );
		}
		return null;
	}

	public static User findUserByUsername( String username ) {
		for ( int i = 0; i < users.size(); i++ ) {
			if ( users.get( i ).username.equals( username ) )
				return users.get( i );
		}
		return null;
	}

	public static boolean exists( User user ) {
		return users.contains( user );
	}

	public static void addUser( User user ) {
		users.add( user );

		System.out.println( "New user added: " + user.username + ", ID=" + user.ID + ", IP=" + user.ipAddress );

	}

	public static void removeUser( User user ) {
		if ( users.contains( user ) ) {
			users.remove( user );
		}

	}

	public static ArrayList< User > getUsers() {
		return users;
	}

	public static void sendGlobalMessage( String kind , User from , String message ) {
		for ( User user : users ) {
			user.sendMessage( kind , from , message );
		}
	}

	public static void sendGlobalMessage( User from , String msg ) {
		sendGlobalMessage( "talk" , from , msg );
	}

	public static void sendGlobalMessage( String message ) {
		sendGlobalMessage( User.SYSTEM , message );
	}

	public static void sendListUpdate() {
		for ( User user : users ) {

			if ( user == SYSTEM || user == Robot.mUser || user.outSocket == null ) {
				// we don't care about the user list. k?
				continue;
			}

			ObjectNode event = Json.newObject();
			event.put( "kind" , "membersUpdate" );

			// Add all members to array node
			ArrayNode mNode = event.putArray( "members" );
			for ( int i = 0; i < users.size(); i++ ) {
				mNode.add( StringEscapeUtils.escapeHtml( users.get( i ).username ) );
			}

			user.outSocket.write( event );
		}

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
