/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;
// UnsupportedEncodingException
// BitSet

/**
 * Generic converter class with static methods used to convert data types. The
 * primitive data types are converted into byte arrays of the respective
 * dimensions, as per below;
 * 
 * boolean: 1 bit byte: 1 byte short: 2 bytes int: 4 bytes long: 8 bytes float:
 * 4 bytes double: 8 bytes char: 2 bytes
 */
public final class Converter {

	/**
	   * 
	   */
		public static byte[] bitsetToBytes(BitSet bitstring) {
			if (bitstring == null || bitstring.size() == 0) {
				return (byte[]) null;
			}
	
			byte[] bytes = new byte[(bitstring.length() / 8) + 1];
	
			for (int i = 0; i < bitstring.length(); i++) {
				if (bitstring.get(i)) {
					bytes[(bytes.length - i / 8) - 1] |= 1 << (i % 8);
				}
			}
	
			return bytes;
		}
		
  /**
   * Converts the byte array to a boolean. Assumes that the byte array is a single byte in length. If the byte supplied is null then false is returned.
   * 
   * @param bytes the byte array to convert to float, should be a single byte in length
   * @return boolean represented by the byte in the byte array, true if non-zero, else false
   */
	public static final boolean bytesToBooleanBE(byte[] bytes) {
	  if ( bytes == null ) {
	    return false;
	  }
	  
	  byte value = bytes[0];
	  if ( value == 0 ) {
	    return false;
	  } else {
	    return true;
	  }
	}

	/**
	 * Converts a byte array to a float. Assumes the byte array is in big endian
	 * format. The bytes supplied should not exceed 8 bytes, however no
	 * validation is performed. If the bytes array supplied is null then zero
	 * (0) is returned
	 * 
	 * @param bytes
	 *            the byte array to convert to float, should be less than or
	 *            equal to 8 bytes in length
	 * @return float represented by bytes or 0 if bytes is null
	 */
	public static final float bytesToFloatBE(byte[] bytes) {
		if (bytes == null) {
			return 0;
		} else {
			return Float.intBitsToFloat(Converter.bytesToIntBE(bytes));
		}
	}

	/**
	 * Converts a byte array of length 4 to a short. Assumes the byte array is
	 * in big endian format. The bytes supplied should not exceed 4 bytes,
	 * however no validation is performed. If the bytes array supplied is null
	 * then zero (0) is returned
	 * 
	 * @param bytes
	 *            the byte array to convert to int, should be less than or equal
	 *            to 4 bytes in length
	 * @return int represented by bytes or 0 if bytes is null
	 */
	public static final int bytesToIntBE(byte[] bytes) {
		if (bytes == null) {
			return 0;
		}

		// http://stackoverflow.com/questions/1026761/how-to-convert-a-byte-array-to-its-numeric-value-java
		int num = 0;
		for (int i = 0; i < bytes.length; i++) {
			num = (num << 8) + (bytes[i] & 0xff);
		}
		return num;
	}

  /**
   * Converts a byte array of unknown length to a long.
   * 
   * @param bytes
   * @return
   */
  public static final long bytesToLongBE( byte[] bytes ) {
    if (bytes == null) {
      return 0;
    }

    // http://stackoverflow.com/questions/1026761/how-to-convert-a-byte-array-to-its-numeric-value-java
    long value = 0;
    for (int i = 0; i < bytes.length; i++) {
      // value += ( (long)bytes[i] & 0xffL ) << ( 8 * i );
      value = ( value << 8 ) + ( bytes[i] & 0xffL );
    }
    return value;
  }

	/**
	 * Converts a byte array of length 2 to a short. Assumes the byte array is
	 * in big endian format.
	 * 
	 * @param bytes
	 * @return short represented by bytes
	 */
	public static final short bytesToShortBE(byte[] bytes) {
		short num = 0;
		num = (short) (((bytes[0] << 8) | (bytes[1])) & 0xff);
		return num;
	}

	/**
	 * Converts the byte array to a <code>String</code>.
	 * 
	 * @param bytes
	 * @return String represented by bytes or <code>null</code>
	 */
	public static final String bytesToString(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}

		return new String(bytes);
	}

	/**
	 * Converts the <code>int</code> to a 4-byte array. The byte array is
	 * returned as big endian
	 * 
	 * @param num
	 * @return byte[] representing the big endian format of num of size 4 bytes
	 */
	public static final byte[] intToBytesBE(int num) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((num >> 24) & 0xff);
		bytes[1] = (byte) ((num >> 16) & 0xff);
		bytes[2] = (byte) ((num >> 8) & 0xff);
		bytes[3] = (byte) (num & 0xff);
		return bytes;
	}

	/**
	 * Converts the <code>int</code> to a byte array. The size of the byte array
	 * is optimised thus that the smallest number of bytes is used to represents
	 * the <code>int</code>
	 * 
	 * @param num
	 * @return byte[] representing the big endian format of num
	 */
	public static final byte[] intToMinBytesBE(int num) {
		// determine the minimal size
		int size = 1;
		if (num < 255) { // 8 bit num
			size = 1;
		} else if (num >= 255 && num < 65535) { // 16 bit num
			size = 2;
		} else if (num >= 65535 && num < 16777215) { // 24 bit num
			size = 3;
		} else { // 32 bit num
			size = 4;
		}

		// encode into bytes
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) ((num >> ((size - (i + 1)) * 8)) & 0xff);
		}
		return bytes;
	}

	/**
	 * Converts the <code>float</code> to a 4-byte array. The byte array is
	 * returned as big endian
	 * 
	 * @param num
	 * @return byte[] representing the big endian format of num
	 */
	public static final byte[] floatToBytesBE(float num) {
		return Converter.intToBytesBE(Float.floatToIntBits(num));
	}

  /**
   * Converts the <code>long</code> to a 4-byte array. The byte array is
   * returned as big endian
   * 
   * @param num
   * @return byte[] representing the big endian format of num of size 4 bytes
   */
  public static final byte[] longTo4Bytes( long num ) {
    /*
     * byte[] bytes = new byte[4]; for (int i = 3; i >= 0; i--) { bytes[i] =
     * (byte)( num % 0xFFL ); num = num / 0xFFL; } return bytes;
     */
    return Converter.intToBytesBE( (int)num );
  }

  /**
   * Converts the <code>long</code> to a 8-byte array. The byte array is
   * returned as big endian
   * 
   * @param num
   * @return byte[] representing the big endian format of num
   */
	public static final byte[] longToBytesBE(long num) {
		byte[] bytes = new byte[8];
    bytes[0] = (byte)( ( num >> 64 ) & 0xFFL );
    bytes[1] = (byte)( ( num >> 48 ) & 0xFFL );
    bytes[2] = (byte)( ( num >> 40 ) & 0xFFL );
    bytes[3] = (byte)( ( num >> 32 ) & 0xFFL );
    bytes[4] = (byte)( ( num >> 24 ) & 0xFFL );
    bytes[5] = (byte)( ( num >> 16 ) & 0xFFL );
    bytes[6] = (byte)( ( num >> 8 ) & 0xFFL );
    bytes[7] = (byte)( num & 0xFFL );
		return bytes;
	}

	    /**
   * Converts the <code>long</code> to a byte array. The size of the byte array
   * is optimised thus that the smallest number of bytes is used to represents
   * the <code>long</code>
   * 
   * @param num
   * @return byte[] representing the num
   */
  public static final byte[] longToMinBytesBE( long num ) {
    if (num <= Integer.MAX_VALUE) { // 2147483647
      return Converter.intToMinBytesBE( (int)num );
    } else {
      // determine the minimal size
      int size = 4;
      if (num < Math.pow( 2, 32 )) { // 32 bit num
        size = 4;
      } else if (num < Math.pow( 2, 40 )) { // 40 bit num
        size = 5;
      } else if (num < Math.pow( 2, 48 )) { // 48 bit num
        size = 6;
      } else if (num < Math.pow( 2, 56 )) { // 56 bit num
        size = 7;
      } else { // 64 bit num
        size = 8;
      }

      // encode into bytes
      byte[] bytes = new byte[size];
      for (int i = 0; i < size; i++) {
        bytes[i] = (byte)( ( num >> ( ( size - ( i + 1 ) ) * 8 ) ) & 0xff );
      }
      return bytes;
    }
  }

  /**
   * Converts the short to a 2-byte array. The byte array is returned as a big
   * endian
   * 
   * @param num
   * @return byte[] representing the big endian format of num
   */
	public static final byte[] shortToBytesBE(short num) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) ((num >> 8) & 0xff);
		bytes[1] = (byte) (num & 0xff);
		return bytes;
	}

	/**
	 * Converts the <code>String</code> to a <code>byte</code> array. If the
	 * string supplied is null or is an empty string then a null value is
	 * returned
	 * 
	 * @param str
	 * @return byte[] representing the str or <code>null</code> if str is
	 *         invalid
	 */
	public static final byte[] stringToBytesBE(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		byte[] bytes = null;
		try {
			bytes = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException uee) {
			bytes = null;
		}
		return bytes;
	}
}
