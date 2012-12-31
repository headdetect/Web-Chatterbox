package events.chatroom;

import models.ChatRoom.Quit;
import events.Event;
import events.EventList;

public class onQuitEvent extends Event {

	private static final EventList events = new EventList();
	private Quit quit;
	
	public onQuitEvent( Quit data ) { this.quit = data; }
	
	public Quit getQuitModel() {
		return quit;
	}
	
	public static EventList getEventList() {
		return events;
	}
	
	@Override
	public EventList getEvents() {
		return events;
	}

}
