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
 * The GOOSE message application protocol specification abstract class to be
 * extended by all GOOSE PDU classes
 */
public abstract class GOOSEMessageAPDU {

	// Tag octet settings Ref. 61850-9.2 p21
	public static final int TAG_UNIVERSAL = 0x00;
	public static final int TAG_APPLICATION = 0x40;
	public static final int TAG_CONTEXT = 0x80;
	public static final int TAG_PRIVATE = 0xc0;

	public static final int TAG_PRIMITIVE = 0x00;
	public static final int TAG_CONSTRUCTED = 0x20;

	/*
	 * Creates an instance of the GOOSEMessageGoosePDU from the byte array
	 * supplied. The byte array supplied must have been read off of the network.
	 * The offset specified where within the byte array the GOOSE APDU starts,
	 * including the app id preamble
	 * 
	 * @param bytes the array of bytes read off of the network
	 * 
	 * @param offset int representing the offset into the byte array to start
	 * processing from, normal value should be 8, for 8 byte offset
	 * 
	 * @return GOOSEMessageAPDU the instantiated GOOSE APDU
	 */
	// public abstract GOOSEMessageAPDU getInstance(byte[] bytes, int offset);

	/**
	 * Returns the byte array representation of the
	 * <code>GOOSEMessageAPDU</code>
	 * 
	 * @return byte array of the message
	 */
	public abstract byte[] toBytes();

	/**
	 * Returns the byte array representation of the
	 * <code>GOOSEMessageAPDU</code> length
	 */
	public abstract byte[] getLength();
}
