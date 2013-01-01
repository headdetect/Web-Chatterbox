package chatterbox.core.commands;

import models.User;

public class CommandHelp implements ICommand {

	@Override
	public String getName() {
		return "Help";
	}

	@Override
	public boolean hasPermission( User user ) {
		return true;
	}

	@Override
	public void onCall( CommandArgs args ) {
		String[] mArgs = args.getArgs();
		User mUser = args.getUser();
		
		if(mArgs.length == 0) {
			mUser.sendMessage("/help <command_name>");
			return;
		}
		
		

	}

	@Override
	public void getHelp( CommandArgs mArgs ) {
		mArgs.getUser().sendMessage( "It...um...hmm. It can't solve all your problems." );
	}

}
