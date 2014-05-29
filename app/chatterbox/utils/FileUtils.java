/*******************************************************************************
 * Copyright (c) 2012 MCForge - Modified for use with Chatterbox
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package chatterbox.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * An abstract class used for easier file interaction. Almost all of the methods
 * in the class throw {@link IOException}
 */
public abstract class FileUtils {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	/**
	 * The directory for the log files
	 */
	public static final String LOG_DIR = "logs" + File.separator;

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
	 * Creates the directory/file if it doesn't exist.
	 * 
	 * @param path
	 *            - The directory to create.
	 * @param fileName
	 *            - The file to create.
	 * @param contents
	 *            - The contents inside the file.
	 * @throws IOException
	 *             - Signals that an I/O exception has occurred.
	 */
	public static void createIfNotExist( String path , String fileName , String contents ) throws IOException {
		File filePath = new File( path );
		File fileFile = new File( path , fileName );

		filePath.mkdirs();

		if ( !fileFile.exists() ) {
			fileFile.createNewFile();

			PrintWriter writer = new PrintWriter( fileFile );
			writer.write( contents );

			try {
				writer.close();
				writer.flush();
			} finally {
				writer = null;
			}
		}
	}

	/**
	 * Create all child directories contained in <b>filepath</b> if they do not
	 * exist
	 * 
	 * @param filepath
	 *            The full filepath to create child directories for
	 */
	public static void createChildDirectories( String filepath ) {
		String[] dirs = filepath.split( "\\/" );
		String path = "";
		for ( String directory : dirs ) {
			path += ( path.equals( "" ) ? directory : "/" + directory );
			if ( directory.indexOf( "." ) == -1 && !new File( path ).exists() )
				new File( path ).mkdir();
		}
	}

	/**
	 * Creates a file if it does not exists.
	 * 
	 * @param path
	 *            - The directory of the file
	 * @param fileName
	 *            - The name of the file
	 * 
	 * @throws IOException
	 *             - If there's a problem writing the file
	 */
	public static void createIfNotExist( String path , String fileName ) throws IOException {
		createIfNotExist( path , fileName , "" );
	}

	/**
	 * Creates a file if it does not exists.
	 * 
	 * @param fileName
	 *            - The name of the file
	 * 
	 * @throws IOException
	 *             - If there's a problem writing the file
	 */
	public static void createIfNotExist( String fileName ) throws IOException {
		File file = new File( fileName );
		if ( !file.exists() ) {
			file.createNewFile();
		}
	}

	/**
	 * Delete the file/directory if exist.
	 * 
	 * @param filePath
	 *            - The path of the file/directory to delete.
	 */
	public static void deleteIfExist( String filePath ) {
		File file = new File( filePath );
		if ( file.exists() ) {
			file.delete();
		}
	}

	/**
	 * Writes the specified line to the specified file, creating the file if
	 * doesn't exist
	 * 
	 * @param filePath
	 *            - The path of the file to write to
	 * @param text
	 *            - The text to write to the specified file
	 * 
	 * @throws IOException
	 *             If there's an error while writing to the file
	 */
	public static void writeText( String filePath , String text ) throws IOException {
		createIfNotExist( filePath );
		Formatter formatter = new Formatter( new FileWriter( new File( filePath ) , true ) );
		formatter.out().append( text );
		formatter.close();
	}

	/**
	 * Writes the specified string array to the specified file, creating the
	 * file if it doesn't exist
	 * 
	 * @param filePath
	 *            - The path of the file to write to
	 * @param lines
	 *            - The string array to write to the specified file
	 * 
	 * @throws IOException
	 *             If there's an error while writing to the file
	 */
	public static void writeLines( String filePath , String... lines ) throws IOException {
		createIfNotExist( filePath );
		Formatter formatter = new Formatter( new FileWriter( new File( filePath ) , true ) );
		for ( int i = 0; i < lines.length; i++ ) {
			formatter.out().append( lines[ i ] );
		}
		formatter.close();
	}

	/**
	 * Reads the contents of the specified file
	 * 
	 * @param filePath
	 *            - The path of the file to read from
	 * 
	 * @return A string array with the contents of the read file
	 * @throws IOException
	 *             If there's an error while reading from the file
	 */
	public static String[] readAllLines( String filePath ) throws IOException {
		List< String > lines = fileToStringList( filePath );
		return lines.toArray( new String[ lines.size() ] );
	}

	/**
	 * Reads the contents of the specified file
	 * 
	 * @param filePath
	 *            - The path of the file to read from
	 * 
	 * @return A string list with the contents of the read file
	 * @throws IOException
	 *             If there's an error while reading from the file
	 */
	public static List< String > fileToStringList( String filePath ) throws IOException {
		LineNumberReader reader = new LineNumberReader( new FileReader( filePath ) );
		List< String > lines = new ArrayList< String >();
		String line;
		while ( ( line = reader.readLine() ) != null )
			lines.add( line );
		reader.close();
		return lines;
	}

	/**
	 * Gets the total number of lines the specified file has
	 * 
	 * @param filePath
	 *            - The path of the file to check for
	 * 
	 * @return An integer representing the number of lines the file has
	 * @throws IOException
	 *             If there's an error while reading from the file
	 */
	public static int getLineCount( String filePath ) throws IOException {
		return fileToStringList( filePath ).size();
	}

	/**
	 * Checks whether the specified file exists
	 * 
	 * @param filePath
	 *            - the file path to check
	 */
	public static boolean exists( String filePath ) {
		return new File( filePath ).exists();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================



}
