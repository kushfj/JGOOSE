/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850;


/**
 * Generic exception class to encapsulate message exceptions and to be used for
 * all message related exceptions.
 */
public abstract class MessageException extends Exception {
  
  String exceptionMessage; // the exception message

  /**
   * Default serial version ID 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor that uses an unknown exception message
   */
  public MessageException() {
    // TODO Auto-generated constructor stub
    super();
    this.exceptionMessage = ConstStrings.UNKNOWN_EXCEPTION;
  }

  /**
   * Constructor accepting the exception message
   * 
   * @param message
   */
  public MessageException(String message) {
    super(message);
    this.exceptionMessage = message;
  }

  /**
   * Constructor accepting the nested <code>Throwable</code>
   * 
   * @param cause
   */
  public MessageException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * Constructor accepting an exception message and the nested 
   * <code>Throwable</code>
   * 
   * @param message
   * @param cause
   */
  public MessageException(String message, Throwable cause) {
    super(message, cause);
    this.exceptionMessage = message;
  }
  
  
  /**
   * Returns the exception message for this <code>GOOSEMessage</code> exception
   * 
   * @return String representing the exception message
   */
  public String getMessage() {
    return this.exceptionMessage;
  }
}
