/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import fj.com.kush.app.*;

/**
 * A wrapper for any primary logger. Intended to provide a level of abstration
 * so that the primary logger may be changed transparently without affecting
 * any application level logging code. 
 * 
 * Log is implemented as a Singleton
 */
public final class Log {
  private static Log _log;
  private Logger logger;
  private int logLevel = 0;

  public static final int FATAL = 0;
  public static final int WARN = 1;
  public static final int INFO = 2;
  public static final int DEBUG = 4;
  public static final int TRACE = 8;

  private Log() {
    try {
      logger = Logger.getLogger( ConstStrings.APP_NAME );
      Properties logConfig = null;
      String logFileConfig = null;
      String logLevelConfig = null;
      InputStream fileStream 
        = this.getClass().getClassLoader().getResourceAsStream("Log.properties");

      if ( fileStream != null ) {
      	logConfig = new Properties();
        logConfig.load( fileStream  );
        logFileConfig = logConfig.getProperty( "log" );
        logLevelConfig = logConfig.getProperty( "level" );
      } else {
        logFileConfig = "messages.log";
        logLevelConfig = "8";
      }

      FileHandler logFileHandler = new FileHandler( logFileConfig );
      logger.addHandler( logFileHandler );
      logLevel = Integer.parseInt( logLevelConfig );
    } catch ( IOException ioe ) {
      // todo - load if an error occurred when reading from the input stream. 
    } catch ( IllegalArgumentException iae ) {
      // todo - parseIntif the string does not contain a parsable integer.
      // todo - load if the input stream contains a malformed Unicode escape sequence.
    } catch ( SecurityException se ) {
      // todo - addHandler if a security manager exists and if the caller does not have LoggingPermission("control"). 
    } catch ( NullPointerException npe ) {
      // todo - getLogger if the name is null.
    }
  }

  public static final Log getInstance() {
    if  ( _log == null ) {
      _log = new Log();
    }

    return _log;
  }

  public void write( String strMessage ) {
    write( logLevel, strMessage );
  }

  public void write( int intMode, String strMessage ) {
    if ( intMode <= logLevel ) {
      switch ( intMode ) {
        case FATAL:
          logger.severe( strMessage );
          break;
        case WARN:
  	  logger.warning( strMessage );
          break;
        case INFO:
 	  logger.info( strMessage );
          break;
        case DEBUG:
 	  logger.finer( strMessage );
          break;
        case TRACE:
	  logger.finest( strMessage );
          break;
      }
    }
  }
}
