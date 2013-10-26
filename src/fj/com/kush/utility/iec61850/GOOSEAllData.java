/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import fj.com.kush.utility.BytesUtility;
import fj.com.kush.utility.Converter;

// Collection

/**
 * <code>GOOSEAllData</code> class encapsulates the <code>GOOSEData</code>
 * elements to represent the allData field in the
 * <code>GOOSEMessageGoosePDU</code> PDU for <code>GOOSEMessage</code> messages.
 */
public class GOOSEAllData {

	// constants

  public static final byte TAG = (byte)0xab; // context tag for data set,
  // followed by the length in bytes of the all data

	// attributes

	private byte[] bytes = null;
	private Collection<GOOSEData> gooseData = null;

	// constructors

	/**
	 * Default constructor
	 */
	public GOOSEAllData() {
		this.gooseData = new ArrayList<GOOSEData>();
	}

	/**
	 * Creates and instance of GOOSEAllData after parsing the byte array buffer
	 * supplied after reading off of the network.
	 * 
	 * @param buffer
	 *            the bytes array buffer
	 * @param offset
	 *            the offset into the bytes array to start processing from
	 * @return GOOSEALLData represeting the parsed bytes or null
	 */
	public static GOOSEAllData getInstance(byte[] buffer, int offset) {
		// get num elements
		int start = offset + 1; // skip the current tag byte
		byte[] value = GOOSEMessageGoosePDU.getValue(buffer, start);
		int numEntries = Converter.bytesToIntBE(value);
		start += value.length + 1; // skip the value and length

		GOOSEAllData allData = new GOOSEAllData();

		// if there are data bytes then process them
		if (numEntries > 0) {
      start += 2; // skip preamble 0xab, see this.TAG and overall length

			for (int i = 0; i < numEntries; i++) {
				// get data type tag (1 byte)
        int tag = buffer[start++]; // get the tag and skip it (++)
 
        // get the data value
				value = GOOSEMessageGoosePDU.getValue(buffer, start);

				// get the goose data
        GOOSEData data = GOOSEData.getInstance( tag, value );
        // allData.addGOOSEData( data );
        allData.setDataAt( i, data );
				start += value.length + 1; // skip the length and value bytes
			}
		}

		return allData;
	}

	// accessors

	/**
	 * Gets the collection of all <code>GOOSEData</code>
	 * 
	 * @return collection of all GOOSEData
	 */
	public Collection<GOOSEData> getAllData() {
	  synchronized(gooseData) {
		  return this.gooseData;
	  }
	}

	/**
	 * Gets the length of bytes for all data
	 * 
	 * @return int the number of bytes
	 */
	public int getBytesLength() {
		this.bytes = this.toBytes();
		if (this.bytes == null) {
			return 0;
		} else {
			return this.bytes.length;
		}
	}

  /**
   * Gets the <code>GOOSEData</code> at the offset specified
   * 
   * @param offset
   *          the offset into the all data to get the GOOSE data at
   * @returns GOOSEData at the offset specified
   */
  public GOOSEData getDataAt( int offset ) {
    synchronized(gooseData) {
      return ( (ArrayList<GOOSEData>)this.gooseData ).get( offset );
    }
  }

	/**
	 * Gets the number of GOOSEData elements
	 * 
	 * @return the number of elements
	 */
	public int getNumEntries() {
	  synchronized(gooseData) {
		  return this.gooseData.size();
	  }
	}

	// mutators

	/**
	 * Adds the <code>GOOSEData</code> to the collection
	 * 
	 * @param data
	 *            GOOSEData to add
	 */
	public void addGOOSEData(GOOSEData data) {
		if (data == null) {
			return;
		}
		this.bytes = null;
		synchronized(gooseData) {
		  this.gooseData.add(data);
		}
	}
	
	  /**
   * Sets the <code>GOOSEData</code> at the offset specified. If the specified
   * offset is invalid then the data is appended to the collection
   * 
   * @param offset
   *          the offset into the <code>ArrayList</code>
   * @param data
   *          the <code>GOOSEData</code> to set
   */
	public void setDataAt(int offset, GOOSEData data) {
    this.bytes = null;
    boolean offset_error = false;

    if (offset < 0 || offset >= gooseData.size()) {
      offset_error = true;
    }

	  synchronized(gooseData) {
	    //((ArrayList<GOOSEData>)this.gooseData).add(offset, data);
      if (gooseData.isEmpty() || offset_error) {
        // ( (ArrayList<GOOSEData>)this.gooseData ).add( data );
        this.addGOOSEData( data );
      } else {
        ( (ArrayList<GOOSEData>)this.gooseData ).set( offset, data );
      }
	  }
	}

	// auxillary methods

  /**
   * Converts the <code>GOOSEAllData<code> instance to an array of bytes. 
   * The method should only be invoked once the instance has been completely set
   * 
   * @return byte[] representing the instance
   */
	public byte[] toBytes() {
		if (this.getAllData().size() == 0) {
			return null;
		}

		// try to be efficient if multiple calls are made without adding new
		// data, i.e. be lazy and return the previous bytes array
		if (this.bytes != null) {
			return bytes;
		}

		// calculate the length
		int length = 0;
    Iterator<GOOSEData> iter = null;
		synchronized(gooseData) {
		  iter = this.gooseData.iterator();
		  while (iter.hasNext()) {
			  GOOSEData data = (GOOSEData) iter.next();
        if (data != null) {
          length += data.getBytesLength();
        }
		  }
		}
	  iter = null;

		// construct the bytes array
    byte[] buffer = new byte[length + 2]; // 1 byte tag (0xab) + 1 bytes length
    buffer[0] = (byte)GOOSEAllData.TAG;
		buffer[1] = (byte) length; // length

    // FIXME: refactor to see if can be used with calculate length iteration
    // go through all GOOSE data
		synchronized(gooseData) {
  		iter = this.gooseData.iterator();
  		int buffOffset = 2;
  		while (iter.hasNext()) {
  			GOOSEData data = (GOOSEData) iter.next();
  			byte[] data_bytes = data.toBytes();
  			if (data_bytes != null) {
  				BytesUtility.copyBytes(data_bytes, buffer, 0,
  						data_bytes.length, buffOffset);
  				buffOffset += data_bytes.length;
  			}
  		}
		}

		this.bytes = buffer;
		return buffer;
	}

	    /**
   * Returns a string representing the <code>GOOSEAllData</code> instance. This
   * method invokes the <code>toString</code> method of the encapsulated
   * <code>GOOSEData</code> objects.
   * 
   * @return String representing the instance
   */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("allData" + ConstStrings.NEWLINE);
		synchronized(gooseData) {
  		Iterator<GOOSEData> iter = gooseData.iterator();
  		while (iter.hasNext()) {
  			GOOSEData data = (GOOSEData) iter.next();
  			sb.append(data.toString());
  		}
		}
		return sb.toString();
	}
}
