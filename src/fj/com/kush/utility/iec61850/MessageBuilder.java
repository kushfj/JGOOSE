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
 * Generic builder pattern interface to specify message builder.
 */
public interface MessageBuilder {

	/**
	 * Builds the <code>Message</code> from the source
	 */
	public IEC61850MessageInterface buildMessage(Object source);
}
