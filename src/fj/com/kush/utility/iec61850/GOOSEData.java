/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850;

import java.util.BitSet;

import fj.com.kush.utility.BytesUtility;
import fj.com.kush.utility.Converter;
// BytesUtility, Converter
// BitSet

/**
 * Ref IEC 61850 8.1 Annex G and GOOSE Annex A See also IEC 61850 7.1 p17 Tab.14
 * 
 * Data ::= CHOICE { -- context tag 0 is reserved for AccessResult array [1]
 * IMPLICIT SEQUENCE OF Data, structure [2] IMPLICIT SEQUENCE OF Data, boolean
 * [3] IMPLICIT BOOLEAN, bit-string [4] IMPLICIT BIT STRING, integer [5]
 * IMPLICIT INTEGER, unsigned [6] IMPLICIT INTEGER, floating-point [7] IMPLICIT
 * FloatingPoint, real [8] IMPLICIT REAL, octet-string [9] IMPLICIT OCTET
 * STRING, visible-string [10] IMPLICIT VisibleString, binary-time [12] IMPLICIT
 * TimeOfDay, bcd [13] IMPLICIT INTEGER, booleanArray [14] IMPLICIT BIT STRING,
 * objId [15] IMPLICIT OBJECT IDENTIFIER, ..., mMSString [16] IMPLICIT
 * MMSString, utc-time [17] IMPLICIT UtcTime -- added by IEC61850 8.1 G3 }
 */
public class GOOSEData {

	// constants

	public static final byte TAG = (byte) 0x80; // Ref. IEC 61850 9.2 p21
												// Fig.A.3

	// attributes

	private int type = -1; // type of data
	private Object value = null; // value of the data
	private byte[] bytes = null;

	// constructors

	/**
	 * Constructor accepting the type and value of the <code>GOOSEData</code>
	 * 
	 * @param int the data type
	 * @param Object
	 *            the data value
	 */
	public GOOSEData(int type, Object value) {
		// TODO throw exception if type or value are invalid
		this.setType(type);
		this.setValue(value);
	}

	  /**
   * TODO: complete documentation
   * 
   * @param tag
   * @param value
   * @return
   */
  public static GOOSEData getInstance( int tag, byte[] value_bytes ) {
		int type = -1;
		GOOSEData gooseData = null;

    // determine type of data to instantiate for the GOOSE data
    switch (( tag & 0xFF )) { // get rid of the sign
      case 0x83:
        type = GOOSEDataTypes.TYPE_BOOL;
        boolean boolean_value = Converter.bytesToBooleanBE( value_bytes );
        gooseData = new GOOSEData( type, new Boolean( boolean_value ));
        break;
      case 0x87:
        type = GOOSEDataTypes.TYPE_FLOAT64;
        float float_value = Converter.bytesToFloatBE( value_bytes );
        gooseData = new GOOSEData( type, new Float( float_value ) );
        break;
      case 0x86:
        type = GOOSEDataTypes.TYPE_INT32U;
        int int_value = Converter.bytesToIntBE( value_bytes );
        gooseData = new GOOSEData( type, new Integer( int_value ) );
        break;
      default:
        // TODO: should really throw and exception here to indicate an invalid
        // tag type
        return null;
    }

		return gooseData;
	}

	// accessors

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the length of bytes for this <code>GOOSEData</code>
	 */
	public int getBytesLength() {
    // attempt to convert to bytes and get length
    this.bytes = this.toBytes();
		if (this.bytes == null) {
      return 0; // something's terribly wrong
		}

		return this.bytes.length;
	}

	

	// mutators

	/**
	 * Sets the type of data
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
		this.bytes = null;
	}

	/**
	 * Sets the value and type
	 * 
	 * @param type
	 *            the type of data
	 * @param value
	 *            the value of the data
	 */
	public void setData(int type, Object value) {
		this.setType(type);
		this.setValue(value);
	}

	/**
	 * Sets the value of data
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
		this.bytes = null;
	}

	/**
	 * Gets the <code>GOOSEData</code> as a byte array according to the IEC
	 * GOOSE PDU specification. The <code>GOOSEData</code> must be initialised
	 * prior to invoking this method.
	 * 
	 * Data ::= CHOICE { 
	 * -- context tag 0 is reserved for AccessResult 
	 * array [1] IMPLICIT SEQUENCE OF Data, 
	 * structure [2] IMPLICIT SEQUENCE OF Data,
	 * boolean [3] IMPLICIT BOOLEAN, 
	 * bit-string [4] IMPLICIT BIT STRING, 
	 * integer [5] IMPLICIT INTEGER, 
	 * unsigned [6] IMPLICIT INTEGER, 
	 * floating-point [7] IMPLICIT FloatingPoint, 
	 * real [8] IMPLICIT REAL, 
	 * octet-string [9] IMPLICIT OCTET STRING, 
	 * visible-string [10] IMPLICIT VisibleString, 
	 * binary-time [12] IMPLICIT TimeOfDay, 
	 * bcd [13] IMPLICIT INTEGER, 
	 * booleanArray [14] IMPLICIT BIT STRING, 
	 * objId [15] IMPLICIT OBJECT IDENTIFIER, ...,
	 * mMSString [16] IMPLICIT MMSString, 
	 * utc-time [17] IMPLICIT UtcTime -- added by IEC61850 8.1 G3
	 */
	public byte[] toBytes() {
		if (this.getValue() == null || this.getType() == -1) {
			return null; // TODO throw exception for uninitialised data
		}

    // if unchanged then return previous bytes
    if (this.bytes != null) {
      return bytes;
    }
	
    // TODO: fix up to use constants instead of hex byte values, e.g. 0x07
		// for floating point - below
	
		// construct the tag and length
		int tag = GOOSEMessageAPDU.TAG_CONTEXT;
		byte[] value_bytes = null;
	
		switch (this.getType()) {
    // 0x00 is reserved for access result
      case GOOSEDataTypes.TYPE_ARRAY:
        tag |= 0x01; // array
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_STRUCT:
        tag |= 0x02; // structure
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_BOOL:
        tag |= 0x03; // boolean
        if ((Boolean)this.getValue()) {
          value_bytes = new byte[] { (byte)0x01 }; // true
        } else {
          value_bytes = new byte[] { (byte)0x00 }; // false
        }
        break;
      case GOOSEDataTypes.TYPE_BIT_STR:
        tag |= 0x04; // bit string
        value_bytes = Converter.bitsetToBytes( ( (BitSet)this.getValue() ) );
        break;
      case GOOSEDataTypes.TYPE_INT8:
      case GOOSEDataTypes.TYPE_INT16:
      case GOOSEDataTypes.TYPE_INT32:
      case GOOSEDataTypes.TYPE_INT128:
        tag |= 0x05; // integer
        value_bytes = Converter.intToBytesBE( ( (Integer)this.getValue() )
            .intValue() );
        break;
      case GOOSEDataTypes.TYPE_INT8U:
      case GOOSEDataTypes.TYPE_INT16U:
      case GOOSEDataTypes.TYPE_INT32U:
        tag |= GOOSEDataTypes.TAG_UNSIGNED; // 0x06; // unsigned, cannot be
                                            // negative
        value_bytes = Converter.intToBytesBE( ( (Integer)this.getValue() )
            .intValue() );
        break;
      case GOOSEDataTypes.TYPE_FLOAT32:
      case GOOSEDataTypes.TYPE_FLOAT64:
        tag |= GOOSEDataTypes.TAG_FLOAT; // 0x07; // floating point, octet
                                         // string
        byte[] float_bytes = Converter
            .floatToBytesBE( ( (Float)this.getValue() ).floatValue() );
        value_bytes = new byte[float_bytes.length + 1];
        // The preceeding byte indicates the number of bits used for the
        // exponent
        // See also. http://tissues.iec61850.com/tissue.mspx?issueid=817
        value_bytes[0] = (byte)0x08; // the number of exponent bits
        BytesUtility.copyBytes( float_bytes, value_bytes, 0,
            float_bytes.length, 1 );
        break;
      // 0x08 is reserved
      case GOOSEDataTypes.TYPE_OCTET_STR:
        tag |= 0x09; // octet string
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_VISIBLE_STR:
        tag |= 0x0a; // 10 - visible string
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_TIMESTAMP:
        tag |= 0x0b; // 11 - generalised time
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_BIN_TIME:
        tag |= 0x0c; // 12 - binary time
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_BCD:
        tag |= 0x0d; // 13 - binary coded decimal, cannot be negative
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_BOOL_ARR:
        tag |= 0x0e; // 14 - boolean array, bit string
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_OBJ_NAME:
      case GOOSEDataTypes.TYPE_OBJ_REF:
        tag |= 0x0f; // 15 - object identifier
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_MMS_STR:
        tag |= 0x10; // 16 - MMS string, UTF8 string
        // TODO
        return null;
        // break;
      case GOOSEDataTypes.TYPE_UTC_TIME:
        tag |= 0x11; // 17 - UTC time, octet string of size 8
        // TODO
        return null;
        // break;
      default:
        // TODO throw an exception for unsupported data
        return null;
		}
	
    // buffer size is length of value in bytes + 1 byte tag + 1 byte length
    byte[] buffer = new byte[value_bytes.length + 2];
		buffer[0] = (byte) tag; // tag
		buffer[1] = (byte) value_bytes.length; // length
		BytesUtility.copyBytes(value_bytes, buffer, 0, value_bytes.length, 2); // value
	
    this.bytes = buffer; // save this for the getBytesLength method
		return buffer;
	}

	/**
	 * TODO: method description
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Data: ");
		switch( this.getType()) {
      case GOOSEDataTypes.TYPE_BOOL:
        sb.append( GOOSEDataTypes.BOOLEAN_LABEL );
        break;
      case GOOSEDataTypes.TYPE_FLOAT32:
      case GOOSEDataTypes.TYPE_FLOAT64:
        sb.append( GOOSEDataTypes.FLOATING_POINT_LABEL );
        break;
      case GOOSEDataTypes.TYPE_INT8U:
      case GOOSEDataTypes.TYPE_INT16U:
      case GOOSEDataTypes.TYPE_INT32U:
        sb.append( GOOSEDataTypes.UNSIGNED_LABEL );
        break;
      default:
        sb.append( GOOSEDataTypes.UNKNOWN_LABEL );
		}
		sb.append(this.getValue() + ConstStrings.NEWLINE );
		return sb.toString();
	}
}
