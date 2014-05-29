package chatterbox.core.commands;

import models.User;

public interface ICommand {

	String getName();

	boolean hasPermission( User user );

	void onCall( CommandArgs args );

	void getHelp( CommandArgs mArgs );
}
