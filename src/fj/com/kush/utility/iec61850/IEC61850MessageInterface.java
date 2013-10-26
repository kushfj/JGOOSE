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
 * @author core
 * 
 */
public interface IEC61850MessageInterface {

	/**
	 * Converts the message to a byte array
	 */
	public byte[] toBytes() throws MessageException;

	/**
	 * Converts the message to a string
	 */
	public String toString();
}
