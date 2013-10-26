/**
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 * 
 * @author Nishchal Kush
 * @version %I%, %G%
 * @since 1.0
 */
package fj.com.kush.utility.iec61850;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jnetpcap.packet.format.FormatUtils;

import fj.com.kush.utility.Converter;

/**
 * <code>GOOSEMessage</code> is a simple class to encapsulate and support Type 1
 * (Fast messages) and Type 1A (Trip) messages as defined in the IEC 61850
 * standard Part 8.1. These messages are mapped directly to distinct Ethertypes
 * to ensure optimised encoding and decoding of received messages. The T-DATA
 * service requires a multicast MAC destination and a unicast MAC as the source
 * address for GOOSE messages. For GSE management messages unicast MAC shall be
 * used for both the source and desination.
 */
public class GOOSEMessage implements IEC61850MessageInterface {
  // constants
  public static final int GOOSE_MESSAGE = 1;
  public static final int GSE_MANAGEMENT_MESSAGE = 2;
  public static final int SV_MESSAGE = 4;

  // package attributes

  /*
   * Ethertype and application ID
   */

  // Raw ethertype
  public static final int GOOSE_MSG = 0x88b8; // 35000
  public static final int GSE_MGMT_MSG = 0x88b9; // 35033
  public static final int SVM_MSG = 0x88ba; // 35002
  public static final int VLAN_MSG = 0x8100; // 33024

  // Ethertype (2 bytes) Ref.61850-8.1 Annex C : Tab.C.2 p115

  static final byte ETHTYPE = (byte) 0x88; // ethernet type
  static final byte MSG_GOOSE = (byte) 0xb8; // ethernet type for goose
  static final byte MSG_GSE_MGMT = (byte) 0xb9; // ethernet type for gse mgmt
  static final byte MSG_SV = (byte) 0xba; // ethernet type for svm

  // AppId statics (2 bytes) Ref.61850-8.2 Annex C : Tab.C.2 p115
  static final byte APPID = (byte) 0x0; // APPID preamble
  static final byte APPID_GOOSE = (byte) 0x0; // appid for goose messages
  static final byte APPID_GSE_MGMT = (byte) 0x0; // appid for gse mgmt msg
  static final byte APPID_SV = (byte) 0x1; // appid for svm

  /*
   * Ethernet MAC addressing
   */
  // MAC addresses (6 bytes)
  // See IEC 61850-9-2 Annex C
  // See IEC 61850-8-1 Annex B p112
  // first 3 octets
  static final byte[] IEEE_MCAST_MAC = { (byte) 0x01, (byte) 0x0c, (byte) 0xcd };

  static final byte GOOSE_MAC = (byte) 0x01; // 4th octet
  static final byte GSSE_MAC = (byte) 0x02; // 4th octet
  static final byte SV_MAC = (byte) 0x04; // 4th octet

  // starting 5th and 6th octet
  static final byte[] LOW_MAC = { (byte) 0x00, (byte) 0x00 };

  // ending 5th and 6th octet
  static final byte[] HIGH_MAC = { (byte) 0x01, (byte) 0xff };

  public static final byte[] GOOSE_BCAST_MAC = { (byte) 0x01, (byte) 0x0c,
      (byte) 0xcd, (byte) 0x01, (byte) 0x01, (byte) 0xff };

  static final int MAX_APDU_LEN = 1480; // IEC 61850-9.1 p13, conflicts with
  // 61850-8.1 p115

  /*
   * VLAN and priority
   */

  // generic object-oriented substation events Ref.61850-8.1 Annex C : Tab.C.1
  // p115
  static final byte GOOSE_DEFAULT_VID = (byte) 0x00; // default VLAN ID for
  // GOOSE
  static final byte GOOSE_DEFAULT_PRIORITY = (byte) 0x04; // default piority
  // for GOOSE
  // generic substation events Ref.61850-8.1 Annex C : Tab.C.1 p115
  static final byte GSE_DEFAULT_VID = (byte) 0x00; // default VLAN ID for GSE
  static final byte GSE_DEFAULT_PRIORITY = (byte) 0x01; // default piority for
  // GSE
  // sampled values Ref.61850-8.1 Annex C : Tab.C.1 p115
  static final byte SV_DEFAULT_VID = (byte) 0x00; // default VLAN ID for SV
  static final byte SV_DEFAULT_PRIORITY = (byte) 0x04; // default piority for
  // SV

  // attributes
  private byte[] src = null;
  private byte[] dst = null;
  private byte[] appid = null; // reserved 0x0000 to 0x3fff for GOOSE
  private byte[] length = null; // 8 + m, m = length of APDU
  private byte[] res1 = { (byte) 0x0, (byte) 0x0 };
  private byte[] res2 = { (byte) 0x0, (byte) 0x0 };
  private GOOSEMessageAPDU apdu = null;
  private int messageType = -1;

  // constructors

  /**
   * Default constructor
   */
  public GOOSEMessage() {
    init();
  }

  // accessors
  /**
   * Returns the APPID of the <code>GOOSEMessage</code> as a byte array
   * 
   * @return the appid
   */
  public byte[] getAppid() {
    if (appid == null) {
      byte[] buffer = new byte[2];
      buffer[0] = GOOSEMessage.ETHTYPE;

      if (this.getMessageType() != -1) {
        if ((this.getMessageType() & GOOSEMessage.GOOSE_MESSAGE) == GOOSEMessage.GOOSE_MESSAGE) {
          buffer[0] = GOOSEMessage.MSG_GOOSE;
          buffer[1] = GOOSEMessage.APPID_GOOSE;
        } else if ((this.getMessageType() & GOOSEMessage.GSE_MANAGEMENT_MESSAGE) == GOOSEMessage.GSE_MANAGEMENT_MESSAGE) {
          buffer[0] = GOOSEMessage.MSG_GSE_MGMT;
          buffer[1] = GOOSEMessage.APPID_GSE_MGMT;
        } else if ((this.getMessageType() & GOOSEMessage.SV_MESSAGE) == GOOSEMessage.SV_MESSAGE) {
          buffer[0] = GOOSEMessage.MSG_SV;
          buffer[1] = GOOSEMessage.APPID_SV;
        }
      } else {
        buffer[1] = (byte) 0x0;
      }

      appid = buffer;
    }
    return appid;
  }

  /**
   * Returns the <code>GOOSEMessageAPDU</code>
   * 
   * @return the apdu
   */
  public GOOSEMessageAPDU getAPDU() {
    return apdu;
  }

  /**
   * Returns the destination MAC address as an array of bytes
   * 
   * @return the dst mac address
   */
  public byte[] getDst() {
    return dst;
  }

  /**
   * Returns the length of the <code>GOOSEMessage</code> as a byte array
   * 
   * @return the length
   */
  public byte[] getLength() {
    return length;
  }

  /**
   * Sets the message type of the <code>GOOSEMessage</code>. Currently supported
   * message types are generic GOOSE messages, GSE management messages and SV
   * messages
   * 
   * @return the messageType
   */
  public int getMessageType() {
    return messageType;
  }

  /**
   * Returns the first reserved block, i.e. Reserved 1 of the
   * <code>GOOSEMessage</code> as a byte array.
   * 
   * @return the res1
   */
  public byte[] getRes1() {
    return res1;
  }

  /**
   * Returns the second reserved block, i.e. Reserved 2 of the
   * <code>GOOSEMessage</code> as a byte array
   * 
   * @return the res2
   */
  public byte[] getRes2() {
    return res2;
  }

  /**
   * Returns the source MAC address as an array of bytes
   * 
   * @return the src mac address
   */
  public byte[] getSrc() {
    return src;
  }

  /**
   * Sets the APPID for the <code>GOOSEMessage</code>. The byte array is NOT
   * copied! The APPID does not need to be set explicitly, instead use the
   * setMessageType method.
   * 
   * @param byte[] 2 bytes representing the APPID
   * @throws GOOSEMessageException
   *           if an invalid APPID is supplied
   * @return
   */
  public void setAppid(byte[] appid) throws GOOSEMessageException {
    if (appid == null || appid.length != 2) {
      throw new GOOSEMessageException(ConstStrings.INVALID_APPID);
    } else {
      this.appid = appid;
    }
  }

  // mutators

  /**
   * Sets the <code>GOOSEMessageAPDU</code>. The GOOSE message length is also
   * calculated in this method, this if the APDU is modified after being set for
   * the GOOSE message, then the APDU should be reset by invoking this method
   * 
   * @param apdu
   *          the apdu to set
   * @throws GOOSEMessageException
   *           if the APDU is invalid
   */
  public void setAPDU(GOOSEMessageAPDU apdu) throws GOOSEMessageException {
    this.apdu = apdu;
    try {
      this.setLength(this.calcLength());
    } catch (GOOSEMessageException gme) {
      throw new GOOSEMessageException(ConstStrings.INVALID_APDU, gme);
    }
  }

  /**
   * Sets the destination MAC address for the <code>GOOSEMessage</code> link
   * layer. The byte array is NOT copied!
   * 
   * @param byte[] 6 bytes representing the MAC address
   * @throws GOOSEMessageException
   *           if an invalid MAC address is supplied
   * @return
   */
  public void setDst(byte[] mac) throws GOOSEMessageException {
    if (mac == null || mac.length != 6) {
      throw new GOOSEMessageException(ConstStrings.INVALID_MAC_ADDR);
    } else {
      this.dst = mac;
    }
  }

  /**
   * Sets the length of the <code>GOOSEMessage</code>. The length is set as the
   * length of the APDU + 8 bytes. The value of the length is stored as a byte
   * array 2 bytes in length.
   * 
   * @param length
   *          the length to set
   * @throws GOOSEMessageException
   *           if the length is invalid
   */
  public void setLength(byte[] length) throws GOOSEMessageException {
    if (length == null || length.length != 2) {
      throw new GOOSEMessageException(ConstStrings.INVALID_LENGTH);
    } else {
      short len = Converter.bytesToShortBE(length);
      if (len > (GOOSEMessage.MAX_APDU_LEN + 8)) { // IEC 61850-8.1 p115
        throw new GOOSEMessageException(ConstStrings.INVALID_LENGTH);
      }
      this.length = length;
    }
  }

  /**
   * Sets the message type of the <code>GOOSEMessage</code>. Currently supported
   * message types are generic GOOSE messages, GSE management messages and SV
   * messages
   * 
   * @param messageType
   *          the messageType to set
   */
  public void setMessageType(int messageType) {
    this.messageType = messageType;
  }

  /**
   * Sets the first reserved block, i.e. Reserver 1 of the <code>GOOSEMessage
   * </code>. The default value is { (byte)0x00, (byte)0x00 }. This method
   * should not be used, but is provided for forward compatibility
   * 
   * @param res1
   *          the res1 to set
   * @throws GOOSEMessageException
   *           if the reserved values are invalid
   */
  public void setRes1(byte[] res1) throws GOOSEMessageException {
    if (res1 == null || res1.length != 2) {
      throw new GOOSEMessageException(ConstStrings.INVALID_VALUE);
    } else {
      this.res1 = res1;
    }
  }

  /**
   * Sets the second reserved block, i.e. Reserver 2 of the
   * <code>GOOSEMessage</code>. The default value is { (byte)0x00, (byte)0x00 }.
   * This method should not be used, but is provided for forward compatibility
   * 
   * @param res2
   *          the res2 to set
   * @throws GOOSEMessageException
   *           if the reserved values are invalid
   */
  public void setRes2(byte[] res2) throws GOOSEMessageException {
    if (res1 == null || res1.length != 2) {
      throw new GOOSEMessageException(ConstStrings.INVALID_VALUE);
    } else {
      this.res2 = res2;
    }
  }

  /**
   * Sets the source MAC address for the <code>GOOSEMessage</code> link layer.
   * The byte array is NOT copied!
   * 
   * @param byte[] 6 bytes representing the MAC address
   * @throws GOOSEMessageException
   *           if an invalid MAC address is supplied
   * @return
   */
  public void setSrc(byte[] mac) throws GOOSEMessageException {
    if (mac == null || mac.length != 6) {
      throw new GOOSEMessageException(ConstStrings.INVALID_MAC_ADDR);
    } else {
      this.src = mac;
    }
  }

  // auxillary methods

  /**
   * TODO: update comments
   * 
   * @return
   */
  private byte[] calcLength() {
    // length = 8 bytes + m
    // 8 bytes from 2 byte APPID, 2 byte Length, 2 byte Res1, and 2 byte Res2
    // m is length of APDU in bytes
    byte[] messageLen = new byte[2];
    messageLen[0] = (byte) 0x0;
    messageLen[1] = (byte) 0x8;

    // allow null to be set
    if (this.apdu == null) {   	
      return messageLen;
    }

    // get the apdu length
    byte[] len = this.apdu.getLength();
    if (len == null || len.length < 1 || len.length > 2) {
      return messageLen;
    } else if ( len.length == 1 ) {
      messageLen[1] = len[0];    	
    }

    // set the length
    short lengthValue = Converter.bytesToShortBE(messageLen);
    lengthValue = (short) (lengthValue + 8);
    messageLen = Converter.shortToBytesBE(lengthValue);
    return messageLen;
  }

  /**
   * Initialises the attributes of the instance.
   */
  public void init() {
    src = null;
    dst = null;
    appid = null; // reserved 0x0000 to 0x3fff for GOOSE
    length = null; // 8 + m, m = length of APDU
    Arrays.fill(res1, (byte) 0x0);
    Arrays.fill(res2, (byte) 0x0);
    apdu = null;
    messageType = -1;
  }

  /**
   * Returns an array of bytes representing the IEC 61850 message
   * 
   * @throws GOOSEMessageException
   *           if this GOOSEMessage is not initialised
   * @return array of bytes representing this <code>GOOSEMessage</code>
   */
  public byte[] toBytes() throws GOOSEMessageException {
    // check if enough of the GOOSEMessage is initialised
    if ((dst == null || dst.length != 6) || (src == null || src.length != 6)
        || (this.getMessageType() == -1)) {
      throw new GOOSEMessageException(ConstStrings.INVALID_GOOSE_MSG);
    }

    // TODO: implement VLAN and priority tagging in frame

    // get the APDU
    byte[] apdu_bytes = (this.apdu != null ? this.apdu.toBytes() : null);

    // initialise buffer to hold the bytes
    // 6 - destination mac - 6 bytes
    // 6 - source mac - 6 bytes
    // 2 - ether type - 2 bytes
    // 2 - appid - 2 bytes
    // 2 - length - 2 bytes
    // 2 - res1 - 2 bytes
    // 2 - res2 - 2 bytes
    // 22 = subtotal
    // TODO: 26 = subtotal (w/4-bytes priority tags)
    // int size = 22 + (apdu_bytes != null ? apdu_bytes.length : 0);
    int size = 22 + ( apdu_bytes != null ? apdu_bytes.length : 0 );
    byte[] buffer = new byte[size]; // create the buffer
    Arrays.fill(buffer, (byte) 0x0); // zero fill the array

    // (6 bytes) set the destination address
    int index = 0; // index into the byte array
    if (dst != null) {
      for (int i = 0; i < dst.length; i++) {
        buffer[i] = (byte) dst[i];
      }
    }
    index = 5;

    // (6 bytes) set the source address
    if (src != null) {
      for (int i = 0; i < src.length; i++) {
        buffer[++index] = src[i];
      }
    } else {
      index += 5;
    }

    // (2 bytes) set the ethernet type and goose message
    buffer[++index] = GOOSEMessage.ETHTYPE;
    byte appid_tag = (byte) 0x0;

    if (this.getMessageType() != -1) {
      if ((this.getMessageType() & GOOSEMessage.GOOSE_MESSAGE) == GOOSEMessage.GOOSE_MESSAGE) {
        buffer[++index] = GOOSEMessage.MSG_GOOSE;
        appid_tag = GOOSEMessage.APPID_GOOSE;
      } else if ((this.getMessageType() & GOOSEMessage.GSE_MANAGEMENT_MESSAGE) == GOOSEMessage.GSE_MANAGEMENT_MESSAGE) {
        buffer[++index] = GOOSEMessage.MSG_GSE_MGMT;
        appid_tag = GOOSEMessage.APPID_GSE_MGMT;
      } else if ((this.getMessageType() & GOOSEMessage.SV_MESSAGE) == GOOSEMessage.SV_MESSAGE) {
        buffer[++index] = GOOSEMessage.MSG_SV;
        appid_tag = GOOSEMessage.APPID_SV;
      }
    } else {
      ++index;
    }

    // (2 bytes) set the APPID preamble and tag
    buffer[++index] = GOOSEMessage.APPID;
    buffer[++index] = appid_tag;

    // (2 bytes) set the length
    this.setLength(this.calcLength());
    if (this.length != null) {
      buffer[++index] = this.length[0];
      buffer[++index] = this.length[1];
    } else {
      index += 2;
    }

    // (2 bytes) set reserved 1
    if (this.res1 != null) {
      buffer[++index] = this.res1[0];
      buffer[++index] = this.res1[1];
    } else {
      index += 2;
    }

    // (2 bytes) set reserved 2
    if (this.res2 != null) {
      buffer[++index] = this.res2[0];
      buffer[++index] = this.res2[1];
    } else {
      index += 2;
    }

    // set the APDU
    if (apdu_bytes != null) {
      for (int i = 0; i < apdu_bytes.length; i++) {
        buffer[++index] = apdu_bytes[i];
      }
    }

    // return a reference to the buffer
    return buffer;
  }

  /**
   * TODO - complete description
   */
  public String toString() {
    // readable dump
    StringBuilder sb = new StringBuilder();
    sb.append("Ethernet" + ConstStrings.NEWLINE);
    // TODO: fix to use constants for labels and newline
    sb.append("Destination: "
        + (this.getDst() != null ? FormatUtils.mac(this.getDst()) : "null")
        + ConstStrings.NEWLINE);
    sb.append("Source: "
        + (this.getSrc() != null ? FormatUtils.mac(this.getSrc()) : "null")
        + ConstStrings.NEWLINE);
    sb.append("Type: " + this.getMessageType() + ConstStrings.NEWLINE);

    sb.append("GOOSE" + ConstStrings.NEWLINE);
    sb.append("APPID: "
        + (this.getAppid() != null ? FormatUtils.asString(this.getAppid())
            : "null") + ConstStrings.NEWLINE);
    sb.append("Length: "
        + (this.getLength() != null ? FormatUtils.asString(this.getLength())
            : "null") + ConstStrings.NEWLINE);
    sb.append("Reserved 1: "
        + (this.getRes1() != null ? FormatUtils.asString(this.getRes1())
            : "null") + ConstStrings.NEWLINE);
    sb.append("Reserved 2: "
        + (this.getRes2() != null ? FormatUtils.asString(this.getRes2())
            : "null") + ConstStrings.NEWLINE);
    sb.append(this.getAPDU().toString());
    sb.append("\n\n");
    // TODO : complete implemention to include allDatat

    // raw dump
    byte[] bytes;
    try {
      bytes = this.toBytes();
    } catch (GOOSEMessageException e) {
      return ConstStrings.EMPTY_STRING;
    }

    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    StringBuilder buffer = new StringBuilder(FormatUtils.hexdump(byteBuffer
        .array()));
    // return buffer.toString();

    sb.append(buffer);
    return sb.toString();
  }
}