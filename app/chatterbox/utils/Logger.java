package chatterbox.utils;

import org.fusesource.jansi.AnsiConsole;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Logger implements Runnable {
	
	// ===========================================================
	// Constants
	// ===========================================================
	
	public static final String ANSI_RESET 	= "\u001B[0m";
	public static final String ANSI_BLACK 	= "\u001B[30m";
	public static final String ANSI_RED 	= "\u001B[31m";
	public static final String ANSI_GREEN 	= "\u001B[32m";
	public static final String ANSI_YELLOW	= "\u001B[33m";
	public static final String ANSI_BLUE 	= "\u001B[34m";
	public static final String ANSI_PURPLE 	= "\u001B[35m";
	public static final String ANSI_CYAN 	= "\u001B[36m";
	public static final String ANSI_WHITE 	= "\u001B[37m";

	// ===========================================================
	// Fields
	// ===========================================================
	
	private static final DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );

	private static String filePath;

	private static PrintWriter out;

	private static Queue< Log > queue = new LinkedList< Log >();

	private static boolean Running;



	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	/**
	 * Start writing logs
	 * 
	 * @throws FileNotFoundException
	 *             If the file to log to cant be found
	 */
	public void run() {
		Running = true;

        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        String filename = cal.getTime().toString().replace(" ", "-");
        String finalName = filename.split("-")[0] + "-" + filename.split("-")[1] + "-" + filename.split("-")[2];
        try {
            Logger.changeFilePath("logs/" + finalName + ".txt");
        } catch (IOException e) {
            System.out.println("logs/" + finalName + ".txt");
            e.printStackTrace();
        }
		
		Iterator< Log > it;
		while ( Running ) {
            synchronized (queue) {
				if ( out == null ) {
					try {
						Thread.sleep( 10 );
					} catch ( InterruptedException e ) {
					}
					continue;
				}
				it = queue.iterator();
				if ( it == null )
					continue;
				while ( it.hasNext() ) {
					Log log = it.next();
					
					String message = log.message;
					
					switch( log.logLevel ) {
						case Log.LOG_LEVEL_ERROR:
							message = ANSI_RED + log.message + ANSI_RESET;
							break;
						case Log.LOG_LEVEL_INFO:
							message = ANSI_BLUE + log.message + ANSI_RESET;
							break;
						case Log.LOG_LEVEL_WARNING:
							message = ANSI_YELLOW + log.message + ANSI_RESET;
							break;
						case Log.LOG_LEVEL_SUCCESS:
							message = ANSI_GREEN + log.message + ANSI_RESET;
							break;
						default:
							break;
							
					}
					
					AnsiConsole.out.println( message + ANSI_RESET );


					if ( out != null )
						out.append(log.message + "\n");
				}
				queue.clear();
				out.flush();
            }
			try {
				Thread.sleep( 10 );
			} catch ( InterruptedException e ) {
			}
		}
	
	}

	// ===========================================================
	// Methods
	// ===========================================================

	
	/**
	 * Stop writing logs This code will block until all logs have been written
	 * to the file
	 * 
	 * @throws InterruptedException
	 */
	public void kill() throws InterruptedException {
		Running = false;
		out.close();
	}

	/**
	 * Add a log to the logger
	 * 
	 * @param message
	 *            The message to add
	 */
	public static void log( String message ) {
        log(message, Log.LOG_LEVEL_NORMAL);
	}
	
	/**
	 * Add a log to the logger
	 * 
	 * @param message
	 *            The message to add
	 */
	public static void log( String message, int logLevel ) {


        synchronized (queue) {
			if ( !Running )
				return;
			Calendar cal = Calendar.getInstance();
			String date = dateFormat.format( cal.getTime() );
			String finalmessage = "[" + date + "] " + message;
			queue.add( new Log ( finalmessage, logLevel ) );
        }
	}

	/**
	 * Change the filePath of the log file
	 * 
	 * @param filename
	 *            The new filePath
	 * @throws IOException
	 */
	public static void changeFilePath( String filename ) throws IOException {
		Logger.filePath = filename;
		if ( out != null )
			out.close();
		FileUtils.createIfNotExist(filePath);
		out = new PrintWriter(filename);
	}


    // ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static class Log {
		
		public static final int LOG_LEVEL_NORMAL 	= 0;
		public static final int LOG_LEVEL_INFO 		= 1;
		public static final int LOG_LEVEL_SUCCESS 	= 2;
		public static final int LOG_LEVEL_WARNING 	= 3;
		public static final int LOG_LEVEL_ERROR 	= 4;
		
		
		public String message;
		
		public int logLevel;
		
		public Log(String message, int logLevel) {
			this.message = message;
			this.logLevel = logLevel;
		}
		
	}

}
