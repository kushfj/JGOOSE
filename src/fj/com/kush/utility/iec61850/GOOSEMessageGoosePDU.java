/**
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 * 
 * @author Nishchal Kush
 * @version %I%, %G%
 * @since 1.0
 */
package fj.com.kush.utility.iec61850;

import java.util.Arrays;
import java.util.Date;

import fj.com.kush.utility.BytesUtility;
import fj.com.kush.utility.Converter;

//import org.jnetpcap.packet.format.FormatUtils;

/**
 * The actual IEC 61850 GOOSE PDU class implementation
 */
public final class GOOSEMessageGoosePDU extends GOOSEMessageAPDU {

  // constants

  // IEC 61850 preamble to the actual GOOSE PDU
  // Ref. IEC 61850-8-1 Annex A
  // 0x61 is preamble tag for GSE or GOOSE Message
  // 0x80 is data tag for GSE Management Message
  // 0x81 is data tag for GOOSE Message
  public static final byte[] GOOSE_ASDU_HDR        = { (byte) 0x61, (byte) 0x81 };

  // IEC 61850-8-1 Specific Protocol APPLICATION 1
  public static final byte   GOOSE_PROTOCOL        = (byte) 0x41;
  public static final int    MAX_DATSET_LENGTH     = 65;
  public static final int    MAX_ID_LENGTH         = 65;

  // IEC 61860-8-1 Section 8.1.3.6 Timestamp
  public static final int    TIME_LEAP_SECOND      = 128;
  public static final int    TIME_CLOCK_FAILURE    = 64;
  public static final int    TIME_CLOCK_NOT_SYNCED = 32;
  public static final int    ACCURACY_UNSPECIFIED  = 31;

  // maximum values for attributes
  public static final long MAX_STNUM = 4294967295L;
  public static final long MAX_SQNUM = 4294967295L;
  public static final long MAX_TAL = 4294967295L;

  // attributes

  // package attributes - Ref. 61850-8.1 p111
  // GoCBRef - maximum of 65 octet reference to the GOOSE control block
  // that is controlling this GOOSE message
  private String             gocbRef               = null;                        // GOOSE
                                                                                   // control
                                                                                   // block
                                                                                   // reference
                                                                                   // Ref
                                                                                   // IEC61850
  // 7.2 p107

  // timeAllowedtoLive - int in the range 1 to 4 294 967 295, specifies the
  // time
  // in milliseconds that the message has to live
  private long timeAllocatedtoLive = -1;

  // datSet - maximum of 65 octet which is a clone of the value found at
  // the GOOSE control block specified by gocbRef
  private String             datSet                = null;                        // data
                                                                                   // set

  // goID - maximum of 65 octet which is a clone of the value found at the
  // GOOSE control block
  private String             goID                  = null;                        // GOOSE
                                                                                   // identifier

  // T - 8 octet timestamp
  private Date               t                     = null;                        // time
                                                                                   // and
                                                                                   // quality
                                                                                   // as
                                                                                   // UTC

  // stNum - int in the range 1 to 4 294 967 295
  private long               stNum                 = -1;                          // status
                                                                                   // number
                                                                                   // -
                                                                                   // increments
                                                                                   // when
                                                                                   // there
                                                                                   // is
                                                                                   // an
  // update in the data value

  // sqNum - int in the range 0 to 4 294 967 295
  private long               sqNum                 = -1;                          // sequence
                                                                                   // number,
                                                                                   // starts
                                                                                   // at
                                                                                   // 0
                                                                                   // (first
                                                                                   // transmission)
  // for each stNum and
  // increments, rolls over to 1

  private boolean            test                  = true;
  // is it a test, default false

  // confRev - int in the range 0 to 4 294 967 295
  private int                confRev               = -1;
  // configuration revision

  private boolean            ndsCom                = true;
  // needs commission, default false;
  // true
  // if
  // allData (datSet) is null

  // GOOSE control block
  // GOOSEControlBlock cb = null;

  private GOOSEAllData       allData               = null;                        // sequence
                                                                                   // of
                                                                                   // data

  private byte[]             security              = null;                        // reserved
                                                                                   // for
                                                                                   // digital
                                                                                   // signatures

  int                        length                = -1;

  // constructors

  /**
   * Default constructor
   */
  public GOOSEMessageGoosePDU() {
    gocbRef = null;
    timeAllocatedtoLive = -1;
    datSet = null;
    goID = null;
    t = null;
    stNum = -1;
    sqNum = -1;
    test = true;
    confRev = -1;
    ndsCom = true;
    // numDatSetEntries = -1;
    allData = null;
    security = null;
    length = 0;
  }

  /**
   * Creates an instance of the GOOSEMessageGoosePDU from the byte array
   * supplied. The byte array supplied must have been read off of the network.
   * The offset specified where within the byte array the GOOSE APDU starts,
   * including the app id preamble
   * 
   * @param bytes
   *          the array of bytes read off of the network
   * @param offset
   *          int representing the offset into the byte array to start
   *          processing from, normal value should be 8, for 8 byte offset
   * @return GOOSEMessageGoosePDU the instantiated GOOSE APDU
   */
  public static GOOSEMessageGoosePDU getInstance(byte[] bytes, int offset) {
    if (bytes == null) {
      return null;
    }

    GOOSEMessageGoosePDU goosePDU = new GOOSEMessageGoosePDU();

    // This is a really bad hack to handle non-standard implementation of the
    // GOOSE message PDU
    //
    // Example start of payload byte array
    // Standards compliant with a context 0x81 tag before PDU length
    // 00 00 00 00 00 00 00 00 61 81 80 80 18 47 45 5f - Virtual
    // 00 01 00 91 00 00 00 00 61 81 86 80 1a 47 45 44 - Wireshark
    // 30 01 00 e3 00 00 00 00 61 81 d8 80 1b 41 41 31 - Sample GOOSE
    // 30 01 00 e2 00 00 00 00 61 81 d7 80 1b 41 41 31 - Sample GOOSE + MMS
    // No context tag!! - WTF!!
    // 00 00 00 86 00 00 00 00 61 7c 80 18 47 45 5f 4e - MSU
    // 20 00 00 6c 00 00 00 00 61 62 80 03 31 31 31 81 - Demo
    //
    // Initial offset supplied, usually 8 bytes, thus start with
    // preamble of 1 bytes, i.e. 0x61 then the APDU tag of 1 byte e.g. 0x80 for
    // GSE management or 0x81 for GOOSE
    //
    // Since these are set dynamically by the GOOSEMessage instance, when
    // processing we can skip these by starting our processing of the byte
    // array at a new offset, however how much of the bytes to skip depends on
    // the implementation, i.e. for standard implementation we can skip an extra
    // context tag, else we cant. So we need to calculate the offset correction
    // value by attempting to guess the type of byte array we are given

    // set the gocbref
    // check if the start is a tag, else must be dodgy non-standard
    // implementation, so calculate offset correctly. See comments above
    int offsetCorrection = 1;
    if (bytes[offset + 1] == (byte) 0x80 || bytes[offset + 1] == (byte) 0x81) {
      offsetCorrection = 2;
    } else {
      offsetCorrection = 1;
    }

    // ASDU header contains 0x61 and 0x81 for GOOSE PDU + x for gocbref tag
    int start = offset + GOOSEMessageGoosePDU.GOOSE_ASDU_HDR.length
        + offsetCorrection;
    byte[] value = getValue(bytes, start);
    goosePDU.setGocbRef(Converter.bytesToString(value));

    // set the timeAllowedToLive
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);
    // goosePDU.setTimeAllocatedtoLive(Converter.bytesToIntBE(value));
    goosePDU.setTimeAllocatedtoLive(Converter.bytesToIntBE(value));

    // set datSet
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);
    goosePDU.setDatSet(Converter.bytesToString(value));

    // set goID
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);
    goosePDU.setGoID(Converter.bytesToString(value));

    // set time
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);

    byte[] quotient_bytes = new byte[4];
    BytesUtility.copyBytes(value, quotient_bytes, 0, 4);
    // int quotient = Converter.bytesToIntBE(quotient_bytes);
    long quotient = (long) Converter.bytesToIntBE(quotient_bytes);

    byte[] remainder_bytes = new byte[4];
    BytesUtility.copyBytes(value, remainder_bytes, 4, 4);

    remainder_bytes[3] = 0; // reset the sensitivity byte
    int remainder_reversed = Converter.bytesToIntBE(remainder_bytes);
    int remainder = Integer.reverse(remainder_reversed);

    long epoch = (quotient * 1000) + remainder;
    goosePDU.setT(new Date(epoch));

    // set stNum
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);
    goosePDU.setStNum(Converter.bytesToLongBE(value));

    // set sqNum
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);
    goosePDU.setSqNum(Converter.bytesToLongBE(value));

    // set test
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);
    if (Converter.bytesToIntBE(value) != 0) {
      goosePDU.setTest(true);
    } else {
      goosePDU.setTest(false);
    }

    // set confRev
    start += value.length + 2; // skip current tag and previous length bytes
    value = getValue(bytes, start);
    goosePDU.setConfRev(Converter.bytesToIntBE(value));

    // setndsCom
    start += value.length + 2; // skip current tag, previous length and
    // value bytes
    value = getValue(bytes, start);
    if (Converter.bytesToIntBE(value) != 0) {
      goosePDU.setNdsCom(true);
    } else {
      goosePDU.setNdsCom(false);
    }

    // chomp numDatSetEnties
    // start += value.length + 1; // skip previous length and value bytes
    // value = getValue(bytes, start);
    // int numEntries = Converter.bytesToIntBE(value);

    // set allData
    start += value.length + 1; // skip previous length and value bytes
    GOOSEAllData allData = GOOSEAllData.getInstance(bytes, start);
    goosePDU.setAllData(allData);

    return goosePDU;
  }

  /**
   * Parses the byte array supplied for the length tag, and clones the byte
   * array for values and returns it. The next byte value following the start
   * offset should specify the length of the value to be obtained in bytes
   * 
   * @param buffer
   *          the byte array containing the value
   * @param start
   *          the start offset into the byte array supplied
   * @return byte[] representing the value of length specified at the start
   *         position
   */
  static byte[] getValue(byte[] buffer, int start) {
    if (buffer == null) {
      return null;
    }

    int length = buffer[start++];
    byte[] value = new byte[length];
    BytesUtility.copyBytes(buffer, value, start, length);

    return value;
  }

  // accessors

  /**
   * Gets the datSet for this PDU
   * 
   * @return String of the datSet
   */
  public String getDatSet() {
    return this.datSet;
  }

  /**
   * Gets the gocbRef for this PDU
   * 
   * @return the GOOSE control block string
   */
  public String getGocbRef() {
    return this.gocbRef;
  }

  /**
   * @return the stNum
   */
  public long getStNum() {
    return stNum;
  }

  /**
   * @return the sqNum
   */
  public long getSqNum() {
    return sqNum;
  }

  /**
   * @param numDatSetEntries
   *          the numDatSetEntries to set
   */
  // public void setNumDatSetEntries(int numDatSetEntries) {
  // this.numDatSetEntries = numDatSetEntries;
  // }

  /**
   * Gets the data set for this GOOSE message PDU
   * 
   * @return the allData
   */
  public GOOSEAllData getAllData() {
    return this.allData;
  }

  /**
   * @return the confRev
   */
  public int getConfRev() {
    return confRev;
  }

  /**
   * Gets the GOOSE ID for this PDU
   * 
   * @return the goID
   */
  public String getGoID() {
    return this.goID;
  }

  /**
   * Gets the length as a byte array. This method also calculates and sets the
   * internal length attribute.
   * 
   * @see fj.com.kush.utility.iec61850.GOOSEMessageAPDU#getLength()
   */
  @Override
  public byte[] getLength() {
    // length based on fixed length attributes
    // 1 - preamble (0x61)
    // 1 - context 0x80 for goosePdu - Annex A
    // n - pdu length
    // 1 - context
    // n - length
    // n - gocbRef
    // 1 - context
    // 1 - length
    // n - timeAllowedtoLive
    // n - datSet
    // n - goID
    // 10 - T 8 + 2 bytes
    // n - stNum
    // n - sqNum
    // 3 - test 1 + 2 bytes
    // n - confRev
    // 3 - ndsConf 1 + 2 bytes
    // n - numElements
    // 2 - preamble (0xab 0x0a)
    // n - allData
    this.length = 21; // fixed length

    // get gocbref length
    String value = null; // used to reduce local method invocations
    value = this.getGocbRef();
    if (value != null) {
      this.length += value.length() + 2; // 2 bytes for tag and length
    }
    value = null;

    // get timeAllowedToLive length
    byte[] byte_value = Converter.longToMinBytesBE( this
        .getTimeAllocatedtoLive() );
    this.length += byte_value.length + 2; // 2 bytes for tag and length
    byte_value = null;

    // get dat set length
    value = this.getDatSet();
    if (value != null) {
      this.length += value.length() + 2; // 2 bytes for tag and length
    }
    value = null;

    // get goID length
    value = this.getGoID();
    if (value != null) {
      this.length += value.length() + 2; // 2 bytes for tag and length
    }
    value = null;

    // get stNum length
    byte_value = Converter.longToMinBytesBE( this.getStNum() );
    this.length += byte_value.length + 2;
    byte_value = null;

    // get sqNum length
    byte_value = Converter.longToMinBytesBE( this.getSqNum() );
    this.length += byte_value.length + 2;
    byte_value = null;

    // get confRev length
    byte_value = Converter.intToMinBytesBE( this.getConfRev() );
    this.length += byte_value.length + 2;
    byte_value = null;

    // get all data length and numEntries length
    if (this.getAllData() != null) {
      byte_value = Converter
          .intToMinBytesBE( this.getAllData().getNumEntries() );
      this.length += byte_value.length + 2; // tag + length + num entries
      this.length += getAllData().getBytesLength(); // get all data length
      byte_value = null;
    }

    // return length as a byte array
    return ( Converter.intToMinBytesBE( this.length ) );
  }

  // mutators

  /**
   * @return the numDatSetEntries
   */
  public int getNumDatSetEntries() {
    if (this.allData != null) {
      return this.allData.getNumEntries();
    } else {
      return 0;
    }
  }

  /**
   * Gets the security bytes
   * 
   * @return the security
   */
  public byte[] getSecurityBytes() {
    return security;
  }

  /**
   * Gets the time for the PDU
   * 
   * @return the goID
   */
  public Date getT() {
    return this.t;
  }

  /**
   * Gets the time allowed to live for this PDU
   * 
   * @return int the timeAllocatedtoLive
   */
  public long getTimeAllocatedtoLive() {
    return this.timeAllocatedtoLive;
  }

  /**
   * @return the ndsCom
   */
  public boolean isNdsCom() {
    return ndsCom;
  }

  /**
   * @return the test
   */
  public boolean isTest() {
    return test;
  }

  // mutators

  /**
   * Sets the data set for this GOOSE message PDU
   * 
   * @param allData
   *          the allData to set
   */
  public void setAllData(GOOSEAllData allData) {
    this.allData = allData;
  }

  /**
   * @param confRev
   *          the confRev to set
   */
  public void setConfRev(int confRev) {
    this.confRev = confRev;
  }

  /**
   * Sets the datSet which is a clone of the control block reference. Maximum of
   * 65 octet. If the datSet supplied is invalid, then the datSet is set to an
   * empty string. If the datSet is valid, but too long, then it is truncated.
   * 
   * @param datSet
   *          the datSet to set
   */
  public void setDatSet(String datSet) {
    if (datSet == null || datSet.length() == 0) {
      this.datSet = null; // ConstStrings.EMPTY_STRING;
    } else if (datSet.length() > GOOSEMessageGoosePDU.MAX_DATSET_LENGTH) {
      this.datSet = datSet.substring(0, GOOSEMessageGoosePDU.MAX_DATSET_LENGTH);
    } else {
      this.datSet = datSet;
    }
  }

  /**
   * Sets the gocbRef to the <code>String</code>
   * 
   * @param gocbRef
   *          the GOOSE control block string
   */
  public void setGocbRef(String gocbRef) {
    this.gocbRef = gocbRef;
  }

  /**
   * Sets the GOOSE ID for this PDU. If the ID is invalid, then the ID is set to
   * an empty string. The maximum allowed ID string is 65 octet. If the ID is
   * longer than the allowed length, then it is truncated.
   * 
   * @param goID
   *          the goID to set
   */
  public void setGoID(String goID) {
    if (goID == null || goID.length() == 0) {
      this.goID = ConstStrings.EMPTY_STRING;
    } else if (goID.length() > GOOSEMessageGoosePDU.MAX_ID_LENGTH) {
      this.goID = goID.substring(0, GOOSEMessageGoosePDU.MAX_ID_LENGTH);
    } else {
      this.goID = goID;
    }
  }

  /**
   * @param ndsCom
   *          the ndsCom to set
   */
  public void setNdsCom(boolean ndsCom) {
    this.ndsCom = ndsCom;
  }

  /**
   * Sets the security bytes
   * 
   * @param security
   *          the security to set
   */
  public void setSecurityBytes(byte[] security) {
    this.security = security;
  }

  // auxillary methods

  /**
   * @param sqNum
   *          the sqNum to set
   */
  public void setSqNum(long sqNum) {
    if (sqNum < 0) {
      this.sqNum = 0;
    } else if (stNum > Math.pow(2, 32)) { // max should be 4294967295
      this.stNum = 1;
    } else {
      this.sqNum = sqNum;
    }
  }

  /**
   * @param stNum
   *          the stNum to set
   */
  public void setStNum(long stNum) {
    if (stNum <= 0) {
      this.stNum = 1;
    } else if (stNum > Math.pow(2, 32)) { // max should be 4294967295
      this.stNum = 1;
    } else {
      this.stNum = stNum;
    }
  }

  /**
   * Sets the time for the PDU. If the Date specified is invalid or null then
   * the current system time is set.
   * 
   * @param t
   *          the Date to set as the timestamp for this PDU
   */
  public void setT(Date t) {
    if (t == null) {
      this.t = new Date(System.currentTimeMillis());
    } else {
      this.t = t;
    }
  }

  /**
   * Sets the time allowed to live for this PDU
   * 
   * @param timeAllocatedtoLive
   *          the timeAllocatedtoLive to set
   */
  public void setTimeAllocatedtoLive(int timeAllocatedtoLive) {
    this.timeAllocatedtoLive = timeAllocatedtoLive;
  }

  /**
   * @param test
   *          the test to set
   */
  public void setTest(boolean test) {
    this.test = test;
  }

  /**
   * Gets the <code>GOOSEMessageGoosePDU</code> as a byte array according to the
   * IEC GOOSE PDU. There is a preceeding 3 byte preamble of 0x61, 0x81 and the
   * length of the actual PDU.
   * 
   * @see fj.com.kush.utility.iec61850.GOOSEMessageAPDU#toBytes()
   */
  // IECGoosePdu ::= SEQUENCE {
  // gocbRef [0] IMPLICIT VisibleString,
  // timeAllowedtoLive [1] IMPLICIT INTEGER,
  // datSet [2] IMPLICIT
  // VisibleString, goID [3] IMPLICIT VisibleString OPTIONAL,
  // t [4] IMPLICIT UtcTime,
  // stNum [5] IMPLICIT INTEGER,
  // sqNum [6] IMPLICIT INTEGER,
  // test [7] IMPLICIT BOOLEAN DEFAULT FALSE,
  // confRev [8] IMPLICIT INTEGER,
  // ndsCom [9] IMPLICIT BOOLEAN DEFAULT FALSE,
  // numDatSetEntries [10] IMPLICIT INTEGER,
  // allData [11] IMPLICIT SEQUENCE,
  // -- security [12] ANY OPTIONAL
  // }
  @Override
  public byte[] toBytes() {
    // FIXME: check attributes and throw exception in case of error

    // calculated required buffer size and initialise buffer
    this.getLength(); // sets the length attribute, this.length
    byte[] buffer = new byte[this.length];
    Arrays.fill(buffer, (byte) 0x0); // zero fill the array

    // initialise local variables
    int buffer_offset = 0;
    byte tag;
    int numTags = 0;
    String tmpStr = null; // used to reduce local method invocation

    // set preamble (1 byte) and length of entire PDU
    BytesUtility.copyBytes(GOOSEMessageGoosePDU.GOOSE_ASDU_HDR, buffer, 0,
        GOOSEMessageGoosePDU.GOOSE_ASDU_HDR.length, buffer_offset);
    buffer_offset += GOOSEMessageGoosePDU.GOOSE_ASDU_HDR.length;
    buffer[buffer_offset++] = (byte) (this.length - 2);
    // buffer[buffer_offset++] = (byte)( this.length - 1 );

    // set tag (1 byte) Ref. 61850-9.2 p21, length of gocbref (1 byte) and
    // then gocbref
    tmpStr = this.getGocbRef();
    if (tmpStr != null) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      buffer[buffer_offset++] = tag; // tag
      buffer[buffer_offset++] = (byte) tmpStr.length(); // length
      byte[] gocbref_bytes = Converter.stringToBytesBE(tmpStr);
      BytesUtility.copyBytes(gocbref_bytes, buffer, 0, gocbref_bytes.length,
          buffer_offset); // value
      buffer_offset += gocbref_bytes.length;
      tmpStr = null;
    }

    // set tag, length and timeallowedtolive (4 bytes max)
    if (this.timeAllocatedtoLive != -1) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      buffer[buffer_offset++] = tag; // tag
      byte[] timeallowedtolive_bytes = Converter
          .longToMinBytesBE( this.timeAllocatedtoLive );
      buffer[buffer_offset++] = (byte) (timeallowedtolive_bytes.length); // length
      BytesUtility.copyBytes(timeallowedtolive_bytes, buffer, 0,
          timeallowedtolive_bytes.length, buffer_offset); // value
      buffer_offset += timeallowedtolive_bytes.length;
    }

    // set tag, length and datSet (65 bytes max)
    tmpStr = this.getDatSet();
    if (tmpStr != null) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      buffer[buffer_offset++] = tag; // tag
      buffer[buffer_offset++] = (byte) tmpStr.length(); // length
      byte[] datSet_bytes = Converter.stringToBytesBE(tmpStr);
      BytesUtility.copyBytes(datSet_bytes, buffer, 0, datSet_bytes.length,
          buffer_offset); // value
      buffer_offset += datSet_bytes.length;
      tmpStr = null;
    }

    // set tag, length and goID (65 bytes max)
    tmpStr = this.getGoID();
    if (tmpStr != null) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      buffer[buffer_offset++] = tag; // tag
      buffer[buffer_offset++] = (byte) tmpStr.length(); // length
      byte[] goid_bytes = Converter.stringToBytesBE(tmpStr);
      BytesUtility.copyBytes(goid_bytes, buffer, 0, goid_bytes.length,
          buffer_offset); // value
      buffer_offset += goid_bytes.length;
      tmpStr = null;
    }

    // set tag, length and utc time (8 bytes)
    if (this.t != null) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      buffer[buffer_offset++] = tag; // tag
      buffer[buffer_offset++] = 0x08; // length

      // encode as per Annex G (Ref.61850-8.1 p.132)
      long epoch = this.getT().getTime();
      // int quotient = (int)( epoch / 1000 ); // convert millisecs to secs
      long quotient = epoch / 1000;
      // byte[] quotient_bytes = Converter.intToBytesBE(quotient);
      byte[] quotient_bytes = Converter.longTo4Bytes(quotient);
      BytesUtility.copyBytes(quotient_bytes, buffer, 0, quotient_bytes.length,
          buffer_offset);
      buffer_offset += quotient_bytes.length;

      int remainder = (int) (epoch % 1000); // get milliseconds
      int reversed_remainder = Integer.reverse(remainder); // LSB needed
      byte[] remainder_bytes = Converter.intToBytesBE(reversed_remainder);

      // clear last octet and set sensitivity (Ref.61850-8.1 p.28)
      remainder_bytes[3] = (byte) GOOSEMessageGoosePDU.TIME_CLOCK_NOT_SYNCED
          | GOOSEMessageGoosePDU.ACCURACY_UNSPECIFIED;
      BytesUtility.copyBytes(remainder_bytes, buffer, 0,
          remainder_bytes.length, buffer_offset);
      buffer_offset += remainder_bytes.length;
    }

    // set tag, length and stNum (n byte)
    if (this.stNum != -1) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      // buffer[buffer_offset++] = tag; // tag
      // buffer[buffer_offset++] = 0x01; // length
      // buffer[buffer_offset++] = (byte)this.getStNum(); // value
      byte[] stNum_bytes = Converter.longToMinBytesBE(this.getStNum());
      buffer[buffer_offset++] = tag; // tag
      buffer[buffer_offset++] = (byte) stNum_bytes.length; // length
      BytesUtility.copyBytes(stNum_bytes, buffer, 0, stNum_bytes.length,
          buffer_offset); // value
      buffer_offset += stNum_bytes.length;
    }

    // set tag, length and sqNum (n byte)
    if (this.sqNum != -1) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      // buffer[buffer_offset++] = tag; // tag
      // buffer[buffer_offset++] = 0x01; // length
      // buffer[buffer_offset++] = (byte)this.getSqNum(); // value
      byte[] sqNum_bytes = Converter.longToMinBytesBE(this.getSqNum());
      buffer[buffer_offset++] = tag; // tag
      buffer[buffer_offset++] = (byte) sqNum_bytes.length; // length
      BytesUtility.copyBytes(sqNum_bytes, buffer, 0, sqNum_bytes.length,
          buffer_offset); // value
      buffer_offset += sqNum_bytes.length;
    }

    // set tag, length and test (1 byte)
    tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
    buffer[buffer_offset++] = tag; // tag
    buffer[buffer_offset++] = 0x01; // length
    buffer[buffer_offset++] = (byte) (this.isTest() ? 1 : 0); // value

    // set tag, length and confRev (1 byte)
    if (this.confRev != -1) {
      tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
      // buffer[buffer_offset++] = tag; // tag
      // buffer[buffer_offset++] = 0x01; // length
      // buffer[buffer_offset++] = (byte)this.getConfRev(); // value
      byte[] confRev_bytes = Converter.intToMinBytesBE(this.getConfRev());
      buffer[buffer_offset++] = tag; // tag
      buffer[buffer_offset++] = (byte) confRev_bytes.length; // length
      BytesUtility.copyBytes(confRev_bytes, buffer, 0, confRev_bytes.length,
          buffer_offset); // value
      buffer_offset += confRev_bytes.length;
    }

    // set tag, length and ndsCom (1 byte)
    tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
    buffer[buffer_offset++] = tag; // tag
    buffer[buffer_offset++] = 0x01; // length
    buffer[buffer_offset++] = (byte) (this.isNdsCom() ? 1 : 0); // value

    // set tag, length and numDatSetEntires (n byte)
    tag = (byte) (GOOSEMessageAPDU.TAG_CONTEXT | numTags++);
    buffer[buffer_offset++] = tag; // tag
    buffer[buffer_offset++] = 0x01; // length
    buffer[buffer_offset++] = (byte) this.getNumDatSetEntries(); // value

    // encode data set
    if (this.allData != null) {
      byte[] dataset_bytes = this.allData.toBytes();

      if (dataset_bytes != null) {
        BytesUtility.copyBytes(dataset_bytes, buffer, 0, dataset_bytes.length,
            buffer_offset);
        buffer_offset += dataset_bytes.length;
      }
    }

    return buffer;
  }

  /**
   * Returns a <sode>String</code> representation of the
   * <code>GOOSEMessageGoosePDU</code> instance
   * 
   * @return String - preresenting the PDU
   */
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("goosePDU" + ConstStrings.NEWLINE);
    buffer.append("gocbRef: " + this.getGocbRef() + ConstStrings.NEWLINE);
    buffer.append("timeAllowedtoLive: " + this.getTimeAllocatedtoLive()
        + ConstStrings.NEWLINE);
    buffer.append("datSet: " + this.getDatSet() + ConstStrings.NEWLINE);
    buffer.append("goID: " + this.getGoID() + ConstStrings.NEWLINE);
    buffer.append("t: " + this.getT().getTime() + ConstStrings.NEWLINE);
    buffer.append("stNum: " + this.getStNum() + ConstStrings.NEWLINE);
    buffer.append("sqNum: " + this.getSqNum() + ConstStrings.NEWLINE);
    buffer.append("test: " + this.isTest() + ConstStrings.NEWLINE);
    buffer.append("confRev: " + this.getConfRev() + ConstStrings.NEWLINE);
    buffer.append("ndsCom: " + this.isNdsCom() + ConstStrings.NEWLINE);
    buffer.append("numDatSetEntries: " + this.getNumDatSetEntries()
        + ConstStrings.NEWLINE);
    if (this.allData != null) {
      buffer.append(allData.toString());
    }
    return buffer.toString();
  }
}
