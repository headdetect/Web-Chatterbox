package models;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

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

	static {
		defaultRoom = Akka.system().actorOf( new Props( ChatRoom.class ) );
		mRoboto = new Robot( defaultRoom );
	}

	/**
	 * Join the default room.
	 */
	public static void join( final User mUser , WebSocket.In< JsonNode > in , WebSocket.Out< JsonNode > out ) throws Exception {
		mUser.outSocket = out;
		mUser.inSocket = in;

		// Send the Join message to the room
		String result = (String) Await.result( ask( defaultRoom , new Join( mUser ) , 1000 ) , Duration.create( 1 , SECONDS ) );

		if ( "OK".equals( result ) ) {

			// For each event received on the socket,
			in.onMessage( new Callback< JsonNode >() {
				@Override
				public void invoke( JsonNode event ) {

					mUser.sendMessage( event.get( "text" ).asText() );

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

		if ( message instanceof Join ) {

			// Received a Join message
			Join join = (Join) message;

			User.sendGlobalMessage( "join" , "System" , join.user.username + " joined the room" );
			User.sendListUpdate();
			
			getSender().tell( "OK" );

		} else if ( message instanceof Talk ) {

			// Received a Talk message
			Talk talk = (Talk) message;

			// TODO: Async Event calls

			mRoboto.onChat( talk );
			User.sendGlobalMessage( talk.user.username , talk.text );

		} else if ( message instanceof Quit ) {

			// Received a Quit message
			Quit quit = (Quit) message;

			User.removeUser( quit.user );

			User.sendGlobalMessage( "quit" , "System" , quit.user.username + " left the room" );
			User.sendListUpdate();

		} else {
			unhandled( message );
		}

	}

	// -- Messages

	public static class Join {

		public final User user;

		public Join( User username ) {
			this.user = username;
		}

	}

	public static class Talk {

		public final User user;
		public final String text;

		public Talk( User username , String text ) {
			this.user = username;
			this.text = text;
		}

	}

	public static class Quit {

		public final User user;

		public Quit( User username ) {
			this.user = username;
		}

	}

}
