/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850;


import java.util.*;

/**
 * Ref. IEC 61850 8.1 p36
 */
public class GOOSEDataset {
  
  // constants
  
  
  // attributes
  
  private Map<Integer, GOOSEDatasetAttribute> datSet; // data set values to be transmitted - the offset is used as key, and the member values as value
  private String name; // name identifying the data set in the logical node (LN) where it is defined
  private String desc; // the description text for the data set


  // constructors

  /**
   * Default constructor
   */
  public GOOSEDataset() {
    this.name = null;
    this.desc = null;
    this.datSet = new HashMap<Integer, GOOSEDatasetAttribute>();
  }
  
  
  /**
   * Constructor accepting the name and description of the data set
   * 
   * @param name String representing the name of the data set
   * @param desc String representing the description of the data set
   */
  public GOOSEDataset( String name, String desc ) {
    this(); // invoke default constructor
    this.setDesc(desc);
    this.setName(name);
  }
  
  
  // accessors
  
  /**
   * Gets the <code>GOOSEDataSetAttribute</code> element at offset
   * 
   * @param dse GOOSEDataSetAttribute to add to the data set
   */
  public GOOSEDatasetAttribute getDatasetAttribute( int offset ) {
    if ( offset <= 0 ) {
      return null;
    }
    
    Integer index = new Integer( offset );
    return this.datSet.get( index );
  }
  
  
  /**
   * Gets the description of the data set
   * 
   * @return the desc
   */
  public String getDesc() {
    return this.desc;
  }

  
  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }
  
  
  /**
   * Gets the number of data set attributes in the data set
   * 
   * @return the number of attributes
   */
  public int getNumElements() {
    if ( this.datSet != null ) {
      this.datSet.size();
    }
    
    return 0;
  }
  
  
  /**
   * Gets the length of the data set when converted to GOOSE message bytes
   * 
   * @return the length in bytes for the data set
   */
  public int getLength() {
    if (( this.datSet == null ) || ( this.datSet.size() == 0 )) {
      return 0;
    }
    
    // TODO calculate the length
    return 0;
  }
  
  
  /**
   * TODO
   */
  public byte[] toBytes() {
    byte[] buffer = new byte[100];
    return buffer;
    // TODO - complete implementation
  }
  
  
  // mutators
  
  /**
   * Sets the description of the data set
   * 
   * @param desc the desc to set
   */
  public void setDesc(String desc) {
    this.desc = desc;
  }

  
  /**
   * Sets the name of the data set
   * 
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * Adds a <code>GOOSEDataSetAttribute</code> element
   * 
   * @param dse GOOSEDataSetAttribute to add to the data set
   */
  public void addDatasetAttribute( GOOSEDatasetAttribute dse ) {
    if ( dse == null ) {
      return;
    }
    
    Integer offset = new Integer( dse.getOffset());
    this.datSet.put( offset, dse );
  }
  
  
  // GOOSE services
  /*
   * getDataSetValues
   * setDataSetValues
   * createDataSet
   * deleteDataSet
   * getDataSetDirectory
   */
}
