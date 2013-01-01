
package chatterbox.core.commands;

import models.ChatRoom.Talk;
import models.User;

public class CommandArgs {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	
	private String[] mArgs;
	
	private User mUser;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	public CommandArgs(Talk talkData){
		mUser = talkData.getUser();
		mArgs = split( talkData.text );
	}
	
	public CommandArgs(User mUser, String args){
		this.mUser = mUser;
		this.mArgs = split( args );
	}
	
	public CommandArgs(User mUser, String... args){
		this.mUser = mUser;
		this.mArgs = args;
	}


	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public User getUser() {
		return mUser;
	}

	public void setUser( User mUser ) {
		this.mUser = mUser;
	}

	public String[] getArgs() {
		return mArgs;
	}

	public void setArgs( String[] mArgs ) {
		this.mArgs = mArgs;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	private String[] split(String string){
		String[] split = string.split( " " );
		
		for(int i = 0; i < split.length; i++){
			String index = split[i];
			index = index.trim();
		}
		
		return split;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
}
