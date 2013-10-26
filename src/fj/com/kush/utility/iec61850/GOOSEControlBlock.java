/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850;


import fj.com.kush.utility.*;

import java.util.*;


/**
 * <code>GOOSEControlBlock</code> class emulates/virtualises and actual GOOSE 
 * control block as defined in IEC 61850 7.2 p99. The class provides accessors 
 * and mutators to construct representative GOOSE control blocks.
 * 
 * This class is used to provide GOOSE control block definitions for 
 * <code>GOOSEMessageGoosePDU</code> as described in IEC 61850 8.1 p55.
 */
public class GOOSEControlBlock {
  // private attributes
  
  private String goCBName; // GOOSE control block name within scope of LLN0
  private String goCBRef; // unique pathname of control block within LLN0
  private boolean goEna; // GOOSE enable if currently enabled to send GOOSE messages
  private String appID; // name of logical device in which the control block is located
  //private GOOSEControlBlockData[] datSet; // data set values to be transmitted
  private Map<Integer, GOOSEControlBlockData> datSet; // data set values to be transmitted - the offset is used as key, and the member values as value 
  private int confRev; // configuration revision
  private boolean ndsCom; // needs commissioning (datSet not set) flag
  
  
  // constructors
  
  /**
   * Default constructor
   */
  public GOOSEControlBlock() {
    goCBName = null;
    goCBRef = null;
    goEna = false;
    appID = null;
    datSet = null;
    confRev = -1;
    ndsCom = true;
  }
  
  // accessors
  
  /**
   * Gets the GOOSE application identifier
   * 
   * @return the appID
   */
  public String getAppID() {
    return appID;
  }
  

  /**
   * Gets the GOOSE control block reference
   * 
   * @return the goCBRef
   */
  public String getGoCBRef() {
    return goCBRef;
  }
  
  
  /**
   * Gets the GOOSE control block name
   * 
   * @return the goCBName
   */
  public String getGoCBName() {
    return goCBName;
  }
  
  
  /**
   * Gets the GOOSE configuration revision
   * 
   * @return the confRev
   */
  public int getConfRev() {
    return confRev;
  }

  
  /**
   * Gets the <code>GOOSEControlBlockData</code> at the specified offset
   * 
   * @param offset the offset of the control block data to get
   */
  public GOOSEControlBlockData getControlBlockData( int member ) {
    if ( member < 0 ) {
      return null;
    }
    
    Integer offset = new Integer( member );
    if ( this.datSet.containsKey(offset)) {
      return this.datSet.get(offset);
    } else {
      return null;
    }
  }
  
  
  /**
   * Gets the GOOSE reference at offset
   */
  public String getGoRef( int offset ) {
    // TODO
    return null;
  }
  
  
  /**
   * Gets the GOOSE element number for the functionally constrained data
   */
  public int getGOOSEElementNumber( byte[] fdc ) {
    // TODO
    return 0;
  }
  
  
  /**
   * Gets the GOOSE control block values 
   */
  public byte[] getGoCBValues() {
    // TODO
    return null;
  }
  
  
  /**
   * Gets the needs commissioning flag
   * 
   * @return the ndsCom
   */
  public boolean isNdsCom() {
    return ndsCom;
  }


  /**
   * Gets the number of entries in the control block
   * 
   * @return the numEntries
   */
  public int getNumEntries() {
    if ( this.datSet != null ) {
      return this.datSet.size();
    } else {
      return 0;
    }
  }


  /**
   * Gets the GOOSE enabled status
   * 
   * @return the goEna
   */
  public boolean isGOOSEEnabled() {
    return goEna;
  }
  
  
  /**
   * Gets the GOOSE control block data
   * 
   * @return the datSet
   */
  public Map<Integer, GOOSEControlBlockData> getDatSet() {
    return datSet;
  }
  
  // mutators
  
  /**
   * Sets the GOOSE control block name
   * 
   * @param goCBName the goCBName to set
   */
  public void setGoCBName(String goCBName) {
    this.goCBName = goCBName;
  }


  /**
   * Sets the GOOSE control block reference
   * 
   * @param goCBRef the goCBRef to set
   */
  public void setGoCBRef(String goCBRef) {
    this.goCBRef = goCBRef;
  }


  /**
   * Sets the control block to enabled to be able to send GOOSE messages
   * 
   * @param goEna the goEna to set
   */
  public void enableGOOSE( boolean goEna ) {
    this.goEna = goEna;
  }


  /**
   * Sets the GOOSE application identifier
   * 
   * @param appID the appID to set
   */
  public void setAppID(String appID) {
    this.appID = appID;
  }


  /**
   * Sets the GOOSE control block data
   * 
   * @param datSet the datSet to set
   */
  public void setDatSet(Map<Integer, GOOSEControlBlockData> datSet) {
    if ( datSet == null ) {
      return;
    }
    
    this.datSet = datSet;
    this.setNdsCom(false);
  }
  
  
  /**
   * Adds a <code>GOOSEControlBlockData</code> element
   */
  public void addControlBlockData( GOOSEControlBlockData cbd ) {
    if ( cbd == null ) {
      return;
    }
    
    Integer offset = new Integer( cbd.getMemberOffset() );
    this.datSet.put( offset, cbd );
  }


  /**
   * Sets the GOOSE configuration revision
   * 
   * @param confRev the confRev to set
   */
  public void setConfRev(int confRev) {
    this.confRev = confRev;
  }


  /**
   * Sets the needs commissioning flag. Needs to be set to <code>true</code>
   * as long as the data set is not specified.
   * 
   * @param ndsCom the ndsCom to set
   */
  public void setNdsCom(boolean ndsCom) {
    this.ndsCom = ndsCom;
  }


  /**
   * Sends the GOOSE message
   */
  public void sendGOOSEMessage() {
    // TODO
  }
  
  
  /**
   * 
   */
  public void setGoCBValues( byte[] bytes ) {
    // TODO  
  }
  
  
  /**
   * Gets the <code>GOOSEControlBlock</code> as a byte array according to
   * the IEC GOOSE PDU. There is a preceeding 3 byte preamble of 0x61, 0x81 
   * and the length of the actual PDU. Prior to invoking this method, the  
   * 
   * IECGoosePdu ::= SEQUENCE {
   *    gocbRef           [0] IMPLICIT VisibleString,
   *    timeAllowedtoLive [1] IMPLICIT INTEGER,
   *    datSet            [2] IMPLICIT VisibleString,
   *    goID              [3] IMPLICIT VisibleString OPTIONAL,
   *    t                 [4] IMPLICIT UtcTime,
   *    stNum             [5] IMPLICIT INTEGER,
   *    sqNum             [6] IMPLICIT INTEGER,
   *    test              [7] IMPLICIT BOOLEAN DEFAULT FALSE,
   *    confRev           [8] IMPLICIT INTEGER,
   *    ndsCom            [9] IMPLICIT BOOLEAN DEFAULT FALSE,
   *    numDatSetEntries  [10] IMPLICIT INTEGER,
   *    allData           [11] IMPLICIT SEQUENCE,
   *    -- security       [12] ANY OPTIONAL
   * }
   * 
   * @see fj.com.kush.utility.iec61850.GOOSEMessageAPDU#toBytes()
   */
  public byte[] toBytes() {
    if ( this.datSet == null ) {
      return null;
    }
    
    byte[] buffer = new byte[100];
    int buffOffset = 0;
    buffer[buffOffset++] = 0x0a; // control block preamble
    
    TreeSet<Integer> keys = new TreeSet<Integer>( this.datSet.keySet());
    Iterator<Integer> iter = keys.iterator();
    while( iter.hasNext() ) {
      Integer key = (Integer)iter.next();
      GOOSEControlBlockData value = this.datSet.get( key );
      byte[] fcda = value.getMemberReference();
      BytesUtility.copyBytes(fcda, buffer, 0, fcda.length, buffOffset);
    }
    
    // TODO - complete this implementation
    
    return buffer;
  }
}
