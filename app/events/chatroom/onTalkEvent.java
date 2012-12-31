package events.chatroom;

import models.ChatRoom.Talk;
import events.Cancelable;
import events.Event;
import events.EventList;

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
