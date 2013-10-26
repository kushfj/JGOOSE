/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility;

/**
 * BytesUtility is a helper class to provide methods to perform byte 
 * operations on byte arrays
 */
public class BytesUtility {
  
  /**
   * Copy number of bytes from source byte array to destination byte array 
   * starting at offset. The content of destination array will be overwritten.
   * If any of the parameters are invalid then no bytes are copied.
   * 
   * @param src the source byte array
   * @param dst the destination byte array
   * @param src_offset the offset into the src array to start copying 
   * @param num the number of bytes to copy
   */
  public static final void copyBytes( byte[] src, byte[] dst, int src_offset, 
    int num ) 
  {
    if ( src == null || src.length == 0 
      || dst == null || dst.length == 0 
      || src_offset < 0 || src_offset > src.length
      || (src_offset + num) > src.length
      || num < 1 ) 
    {
      return;
    }
    
    BytesUtility.copyBytes(src, dst, src_offset, num, 0);
  }
  
  
  /**
   * Copy number of bytes from source byte array to destination byte array 
   * starting at offset, and writing to destination starting at the destination
   * offset. The contents of the destination array will be overwritten. If any
   * of the parameters are invalid then no bytes are copied.
   */
  public static final void copyBytes( byte[] src, byte[] dst, int src_offset, 
    int num, int dst_offset ) 
  {
    if ( src == null || src.length == 0 
        || dst == null || dst.length == 0 
        || src_offset < 0 || src_offset > src.length
        || dst_offset < 0 || dst_offset > dst.length
        || (src_offset + num) > src.length
        || (dst_offset + num) > dst.length
        || num < 1 ) 
    {
      return; // should throw an exception, really
    }
    
    int dst_index = dst_offset;
    for ( int i = src_offset; i < (src_offset + num); i++ ) {
      dst[dst_index++] = src[i];
    }
  }
  
  /**
   * Copy all bytes from source byte array to destination byte array. The 
   * contents of the destination array will be overwritten. If any of the 
   * parameters are invalud then no bytes are copied, e.g. If the 
   * destination byte array if not the same size or larger than the source
   * byte array then the copy is not performed.
   * 
   *  @param src the source byte array
   *  @param dst the destination byte array
   */
  public static final void copyBytes( byte[] src, byte[] dst ) {
    if ( dst.length < src.length ) {
      return;
    }
    
    BytesUtility.copyBytes(src, dst, 0, src.length, 0);
  }
  
  /**
   * Returns a string representings the hexadecimal values of the bytes. If the
   * bytes supplied is null or an empty array, then an empty string in returned.
   * 
   * @param bytes the bytes array
   * @return String of hexadecimal representing the bytes
   */
  public static final String toHexString( byte[] bytes ) {
    if ( bytes == null || bytes.length == 0 ) {
      return "";
    }
    
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < bytes.length; i++ ) {
      if(((int) bytes[i] & 0xff) < 0x10) {
        buffer.append( "0" );
      }
      
      buffer.append( Long.toString((int) bytes[i] & 0xff, 16 ));
    }

    return buffer.toString();
  }
}
