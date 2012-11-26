package models;

import java.util.ArrayList;

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
		this.username = string;
	}

	public User() {

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

	public void sendMessage( String kind , String from , String asText ) {
		ObjectNode event = Json.newObject();
		event.put( "kind" , kind );
		event.put( "user" , from );
		event.put( "message" , asText );

		outSocket.write( event );
	}

	public void sendMessage( String from , String asText ) {
		sendMessage( "talk" , from , asText );
	}

	public void sendMessage( String text ) {
		sendMessage( username , text );
	}

	public void kick() {
		ChatRoom.defaultRoom.tell( new ChatRoom.Quit( this ) );
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

		if ( Play.isDev() ) {
			System.out.println( "New user added: " + user.username + ", ID=" + user.ID + ", IP=" + user.ipAddress );
		}
	}

	public static void removeUser( User user ) {
		if ( users.contains( user ) ) {
			users.remove( user );
		}

	}

	public static ArrayList< User > getUsers() {
		return users;
	}

	public static void sendGlobalMessage( String kind , String from , String message ) {
		for ( User user : users ) {
			user.sendMessage( kind , from , message );
		}
	}

	public static void sendGlobalMessage( String from , String msg ) {
		sendGlobalMessage( "talk" , from , msg );
	}

	public static void sendGlobalMessage( String message ) {
		sendGlobalMessage( "System" , message );
	}

	public static void sendListUpdate() {
		for ( User user : users ) {
			ObjectNode event = Json.newObject();
			event.put( "kind" , "membersUpdate");
			
			//Add all members to array node
			ArrayNode mNode = event.putArray( "members" );
			for(int i = 0; i < users.size(); i++){
				mNode.add( users.get( i ).username );
			}
			
			user.outSocket.write( event );
		}

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
