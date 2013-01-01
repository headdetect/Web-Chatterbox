package chatterbox.events.talk;

import chatterbox.events.Cancelable;
import chatterbox.events.Event;
import chatterbox.events.EventList;
import models.ChatRoom.Talk;

public class onTalkEvent extends Event implements Cancelable {

	private Talk talk;
	private boolean cancel;
	private static final EventList events = new EventList();
	
	public onTalkEvent( Talk talk ) {
		this.talk = talk;
	}
	
	public Talk getTalk() {
		return talk;
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
