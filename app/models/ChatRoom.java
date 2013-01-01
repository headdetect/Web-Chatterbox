package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import chatterbox.events.EventSystem;
import chatterbox.events.talk.onTalkEvent;
import chatterbox.utils.Logger;
import chatterbox.utils.Logger.Log;


import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.util.Duration;

/**
 * A chat room is an Actor.
 */
public class ChatRoom extends UntypedActor {

	public static final Robot mRoboto;

	public static final ActorRef defaultRoom;
	
	public static final EventSystem events;

	static {
		defaultRoom = Akka.system().actorOf( new Props( ChatRoom.class ) );
		mRoboto = new Robot( defaultRoom );
		events = new EventSystem();
	}

	/**
	 * Join the default room.
	 */
	public static void join( final User mUser , WebSocket.In< JsonNode > in , WebSocket.Out< JsonNode > out ) throws Exception {
		mUser.outSocket = out;
		mUser.inSocket = in;

		// Send the Join message to the room
		String result = (String) Await.result( ask( defaultRoom , new Join( mUser.ID ) , 1000 ) , Duration.create( 1 , SECONDS ) );

		if ( "OK".equals( result ) ) {

			// For each event received on the socket,
			in.onMessage( new Callback< JsonNode >() {
				@Override
				public void invoke( JsonNode event ) {

					if ( mUser.username != null ) {
						mUser.tell( event.get( "text" ).asText() );
					} else {
						User.SYSTEM.tell( event.get( "text" ).asText() );
					}

				}
			} );

			// When the socket is closed.
			in.onClose( new Callback0() {
				@Override
				public void invoke() {

					// Send a Quit message to the room.
					mUser.kick();

				}
			} );

		} else {

			// Cannot connect, create a Json error.
			ObjectNode error = Json.newObject();
			error.put( "error" , result );

			// Send the error to the socket.
			out.write( error );

		}

	}

	@Override
	public void onReceive( Object message ) throws Exception {
		
		if( message == null ) {
			Logger.instance.log( "Recieved null message", Log.LOG_LEVEL_WARNING );
			return;
		}

		if ( message instanceof Join ) {
			System.out.println( "Recieved join" );

			// Received a Join message
			Join join = (Join) message;

			User.sendGlobalMessage( "join" , User.SYSTEM , join.getUser().username + " joined the room" );
			User.sendListUpdate();

			getSender().tell( "OK" );

		} else if ( message instanceof Talk ) {
			System.out.println( "Recieved talk" );

			// Received a Talk message
			Talk talk = (Talk) message;

			onTalkEvent event = new onTalkEvent(talk);
			events.callEvent( event );
			if ( event.isCancelled() )
				return;
			
			User.sendGlobalMessage( talk.getUser() , talk.text );

		} else if ( message instanceof Quit ) {
			System.out.println( "Recieved quit" );

			// Received a Quit message
			Quit quit = (Quit) message;

			User.removeUser( quit.getUser() );

			User.sendGlobalMessage( "quit" , User.SYSTEM , quit.getUser().username + " left the room" );
			User.sendListUpdate();

		} else {
			System.out.println( "Recieved unknown" );
			unhandled( message );
		}

	}

	// -- Messages

	public static class Join {

		public final long user;

		public Join( long username ) {
			this.user = username;
		}
		
		public User getUser (){
			return User.findUserById( user );
		}
	}

	public static class Talk {

		public final long user;
		public final String text;

		public Talk( long username , String text ) {
			this.user = username;
			this.text = text;
		}
		
		public User getUser (){
			return User.findUserById( user );
		}

	}

	public static class Quit {

		public final long user;

		public Quit( long username ) {
			this.user = username;
		}
		
		public User getUser (){
			return User.findUserById( user );
		}

	}

}
