package chatterbox.events.chatroom;

import chatterbox.events.Cancelable;
import chatterbox.events.Event;
import chatterbox.events.EventList;
import models.ChatRoom.Join;

public class onJoinEvent extends Event implements Cancelable {
	
	private static final EventList events = new EventList();
	private Join join;
	private boolean cancel;
	
	public onJoinEvent( Join join ) { this.join = join; }
	
	public Join getJoinModel() {
		return join;
	}
	
	public static EventList getEventList() {
		return events;
	}
	
	@Override
	public EventList getEvents() {
		return events;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancel( boolean cancel ) {
		this.cancel = cancel;
	}

}
