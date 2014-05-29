package chatterbox.utils;

public class StringUtils {
	
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	
	/**
	 * Compares two strings, ignoring all casing.
	 *
	 * @param from 
	 * 			the source of the string
	 * @param compare the string to compare it to
	 * @return true, if the source contains the string being compared to it
	 */
	public static boolean containsIgnoreCase( String from , String compare ) {
		return from.toLowerCase().contains( compare.toLowerCase() );
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================




}
