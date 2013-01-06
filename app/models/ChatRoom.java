package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.util.Duration;
import chatterbox.events.EventSystem;
import chatterbox.events.chatroom.onJoinEvent;
import chatterbox.events.chatroom.onQuitEvent;
import chatterbox.events.chatroom.onTalkEvent;
import chatterbox.events.chatroom.onUnhandledMessageEvent;
import chatterbox.utils.Logger;
import chatterbox.utils.Logger.Log;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A chat room is an Actor.
 */
public class ChatRoom extends UntypedActor {

	public static final Robot mRoboto;
	public static final ActorRef defaultRoom;
	public static final EventSystem events;

	static {
		defaultRoom = Akka.system().actorOf( new Props( ChatRoom.class ) );
		events = new EventSystem();

		//Anything else that uses event system must be created after event system.

		mRoboto = new Robot( defaultRoom );
	}

	/**
	 * Join the default room.
	 */
	public static void join ( final User mUser, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out ) throws Exception {
		mUser.outSocket = out;
		mUser.inSocket = in;

		// Send the Join message to the room
		String result = ( String ) Await.result( ask( defaultRoom, new Join( mUser.ID ), 1000 ), Duration.create( 1, SECONDS ) );

		if ( "OK".equals( result ) ) {

			// For each event received on the socket,
			in.onMessage( new Callback<JsonNode>() {
				@Override
				public void invoke ( JsonNode event ) {

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
				public void invoke () {

					// Send a Quit message to the room.
					mUser.kick();

				}
			} );

		} else {

			// Cannot connect, create a Json error.
			ObjectNode error = Json.newObject();
			error.put( "error", result );

			// Send the error to the socket.
			out.write( error );

		}

	}

	@Override
	public void onReceive ( Object message ) throws Exception {

		if ( message == null ) {
			Logger.instance.log( "Recieved null message", Log.LOG_LEVEL_WARNING );
			return;
		}

		if ( message instanceof Join ) {
			System.out.println( "Recieved join" );

			// Received a Join message
			Join join = ( Join ) message;

			onJoinEvent event = new onJoinEvent( join );
			events.callEvent( event );

			if ( event.isCancelled() ) {
				( ( Join ) message ).getUser().kick();
				return;
			}

			User.sendGlobalMessage( "join", User.SYSTEM, join.getUser().username + " joined the room");
			User.sendListUpdate();

			getSender().tell( "OK" );

		} else
			if ( message instanceof Talk ) {
				System.out.println( "Recieved talk" );

				// Received a Talk message
				Talk talk = ( Talk ) message;

				onTalkEvent event = new onTalkEvent( talk );
				events.callEvent( event );
				if ( event.isCancelled() )
					return;

				User.sendGlobalMessage( "talk", talk.getUser(), talk.text, talk.options );

			} else
				if ( message instanceof Quit ) {
					System.out.println( "Recieved quit" );

					// Received a Quit message
					Quit quit = ( Quit ) message;

					onQuitEvent event = new onQuitEvent( quit );
					events.callEvent( event );

					User.removeUser( quit.getUser() );

					User.sendGlobalMessage( "quit", User.SYSTEM, quit.getUser().username + " left the room" );
					User.sendListUpdate();

				} else {
					System.out.println( "Recieved unknown" );

					onUnhandledMessageEvent event = new onUnhandledMessageEvent( message );
					events.callEvent( event );
					if ( event.isCancelled() )
						return;

					unhandled( message );
				}

	}

	// -- Messages

	public static class Join {

		public final long user;

		public Join ( long username ) {
			this.user = username;
		}

		public User getUser () {
			return User.findUserById( user );
		}
	}

	public static class Talk {

		public long user;
		public String text;
		public Object[] options = new Object[0];

		public Talk ( long username, String text, Object... options ) {
			this.user = username;
			this.text = text;
			this.options = options;
		}

		public User getUser () {
			return User.findUserById( user );
		}

	}

	public static class Quit {

		public final long user;

		public Quit ( long username ) {
			this.user = username;
		}

		public User getUser () {
			return User.findUserById( user );
		}

	}

}
