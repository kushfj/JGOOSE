/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

  // constants
  private static final String LINE_SEPARATOR = System
      .getProperty( "line.separator" );
  private static final char TAB = '\t';

  @Override
  public String format( LogRecord record ) {
    StringBuilder sb = new StringBuilder();

    // sb.append( new Date( record.getMillis() ) );
    sb.append( record.getMillis() );
    sb.append( TAB );
    sb.append( record.getLevel().getLocalizedName() );
    sb.append( ":" );
    sb.append( TAB );
    sb.append( formatMessage( record ) );
    sb.append( LINE_SEPARATOR );

    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        record.getThrown().printStackTrace( pw );
        pw.close();
        sb.append( sw.toString() );
      } catch (Exception ex) {
        // ignore
      }
    }

    return sb.toString();
  }
}