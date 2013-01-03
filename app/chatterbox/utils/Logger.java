package chatterbox.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.fusesource.jansi.AnsiConsole;

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
	
	private final DateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );

	private String filePath;

	private PrintWriter out;

	private Queue< Log > queue = new LinkedList< Log >();

	private boolean Running;
	
	public static Logger instance;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * Create a new instance of Logger.
	 *
	 * @param filePath The filePath of the log file
	 */
	Logger( String filePath) {
		this.filePath = filePath;
	}

	static {
		Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.HOUR);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        instance = new Logger("logs/" + cal.getTime() + ".txt");
        String filename = cal.getTime().toString().replace(" ", "-");
        String finalName = filename.split("-")[0] + "-" + filename.split("-")[1] + "-" + filename.split("-")[2];
        try {
            instance.changeFilePath("logs/" + finalName + ".txt");
        } catch (IOException e2) {
            System.out.println("logs/" + finalName + ".txt");
            e2.printStackTrace();
        }
        
        instance.run();
        
        instance.log( "Logger started" );
	}
	

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	/**
	 * Gets the writer.
	 *
	 * @return the writer
	 */
	public PrintWriter getWriter() {
		return out;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	
	/**
	 * Start writing logs
	 * 
	 * @throws FileNotFoundException
	 *             If the file to log to cant be found
	 */
	@Override
	public void run() {
		AnsiConsole.systemInstall();
		
		Running = true;
		
		
		Iterator< Log > it = null;
		while ( Running ) {
			synchronized ( queue ) {
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
					Log log = (Log) it.next();
					
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
					
					AnsiConsole.out.println( message );


					if ( out != null )
						out.println( log.message );
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
	public void stop() throws InterruptedException {
		AnsiConsole.systemUninstall();
		
		Running = false;
		out.close();
	}

	/**
	 * Add a log to the logger
	 * 
	 * @param message
	 *            The message to add
	 */
	public void log( String message ) {
		synchronized ( queue ) {
			if ( !Running )
				return;
			Calendar cal = Calendar.getInstance();
			String date = dateFormat.format( cal.getTime() );
			String finalmessage = "[" + date + "] " + message;
			queue.add( new Log ( finalmessage, Log.LOG_LEVEL_NORMAL ) );
		}
	}
	
	/**
	 * Add a log to the logger
	 * 
	 * @param message
	 *            The message to add
	 */
	public void log( String message, int logLevel ) {
		synchronized ( queue ) {
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
	 * @param newpath
	 *            The new filePath
	 * @throws IOException
	 */
	public void changeFilePath( String filename ) throws IOException {
		this.filePath = filename;
		if ( out != null )
			out.close();
		FileUtils.createIfNotExist(filePath);
		out = new PrintWriter(filePath);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public class Log {
		
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
