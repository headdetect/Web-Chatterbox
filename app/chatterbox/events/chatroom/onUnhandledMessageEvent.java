package chatterbox.events.chatroom;

import chatterbox.events.Cancelable;
import chatterbox.events.Event;
import chatterbox.events.EventList;


public class onUnhandledMessageEvent extends Event implements Cancelable {

	private Object message;
	private boolean cancel;
	private static final EventList events = new EventList();
	
	public onUnhandledMessageEvent( Object message ) { this.message = message; }
	
	public Object getMessage() {
		return message;
	}
	
	public boolean is( Class<?> classz ) {
		return classz.isAssignableFrom( message.getClass() );
	}
	
	public boolean is( Object type ) { 
		return is( type.getClass() );
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancel( boolean cancel ) {
		this.cancel = cancel;
	}

	@Override
	public EventList getEvents() {
		return events;
	}

}
