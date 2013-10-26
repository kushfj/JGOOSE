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
 * Data holder class used to define GOOSE data type constants
 */
public final class GOOSEDataTypes {
  /* Ref. IEC 61850 7.2 p11 Tab.2 */

	public static final int TYPE_ARRAY = 0;
  public static final int TYPE_STRUCT = 1;
  public static final int TYPE_BOOL = 2; // 8-bit, 0 - false, else true
  public static final int TYPE_BIT_STR = 3; // bit string, 32-bit big endian
  public static final int TYPE_REAL = 4; 
  public static final int TYPE_BIN_TIME = 5;
  public static final int TYPE_BCD = 6;
  public static final int TYPE_BOOL_ARR = 7;
  public static final int TYPE_MMS_STR = 8;
  public static final int TYPE_UTC_TIME = 9;
  
  /* Ref. IEC 61850 8.1 pp25-27 */

  public static final int TYPE_INT8 = 10; // 8-bit big endian, -128 - 127
  public static final int TYPE_INT16 = 11; // 16-bit big endian, integer -32,768 - 32,767
  public static final int TYPE_INT32 = 12; // 32-bit big endian, integer -2,147,483,648 - 2,147,483,647
  public static final int TYPE_INT128 = 13; // 128-bit big endian, integer -2^127 - 2^127
  public static final int TYPE_INT8U = 14; // 8-bit big endian, unsigned 0 - 255
  public static final int TYPE_INT16U = 15; // 16-bit big endian, unsigned 0 - 65535
  public static final int TYPE_INT32U = 16; // 32-bit big endian, unsigned 0 - 4,294,967,295
  public static final int TYPE_FLOAT32 = 17; // 32-bit IEEE floating point
  public static final int TYPE_FLOAT64 = 18; // 64-bit IEEE floating point
  public static final int TYPE_ENUM = 19; // 32-bit big endian, integer ordered set of values (Ref. IEC 61850.8.1 8.1.2.2)
  public static final int TYPE_CODED_ENUM = 20; // 32-bit big endian, ordered set of values (Ref. IEC 61850.8.1 8.1.2.3)
  public static final int TYPE_OCTET_STR = 21; // 20-byte ASCII text, null terminated, octet string, max length to be defined (Ref. IEC 61850.8.1 8.1.2.4)
  public static final int TYPE_VISIBLE_STR = 22; // 35-byte ASCII text, null terminated, visible string, max. length to be defined (Ref. IEC 61850.8.1 8.1.2.5)
  public static final int TYPE_UNICODE_STR = 23; // 20-byte ASCII text, null terminated, MMS string, max. length to be defined (Ref. IEC 61850.8.1 8.1.3.9)
  
  /* Ref. IEC 61850 9.2 p17 Tab.14 */

  public static final int TYPE_OBJ_NAME = 24; // object name, 20-byte ASCII text, null terminated
  public static final int TYPE_OBJ_REF = 25; // object reference, 20-byte ASCII text, null terminated
  public static final int TYPE_TIMESTAMP = 26; // 64

	// constant label string

  public static final String BOOLEAN_LABEL = "boolean: ";
	public static final String FLOATING_POINT_LABEL = "floating-point: ";
	public static final String UNSIGNED_LABEL = "unsigned: ";
	public static final String UNKNOWN_LABEL = "raw: ";
	

	// constant data type tags

  public static final int    TAG_FLOAT            = 0x87;
  public static final int    TAG_UNSIGNED         = 0x86;
}
