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
 * The <code>GOOSEDatasetAttribute</code> is used to encapsulate the data
 * attribute. This class is used in both the allData and datSet implementations
 */
public class GOOSEDatasetAttribute {
 
  // constants
  
  /* Ref. IEC 61850.8.1 Fig.3 p24  */
  public static final int ATTRIB_MX = 0;
  public static final int ATTRIB_ST = 1;
  public static final int ATTRIB_CO = 2;
  public static final int ATTRIB_CF = 4;
  public static final int ATTRIB_DC = 8;
  public static final int ATTRIB_SP = 16;
  public static final int ATTRIB_SG = 32;
  public static final int ATTRIB_RP = 64; // unbuffered report control block
  public static final int ATTRIB_LG = 128; // log control block
  public static final int ATTRIB_BR = 256; // buffered report control block
  public static final int ATTRIB_GO = 512; // GOOSE control block
  public static final int ATTRIB_GS = 1024; // GSSE control block
  public static final int ATTRIB_SV = 2048; // substituted values
  public static final int ATTRIB_SE = 4096; // setting group editing
  public static final int ATTRIB_MS = 8192; // multicast sampled values
  public static final int ATTRIB_SC = 16384; // SCL
  public static final int ATTRIB_US = 32768; // unicast sampled values
  public static final int ATTRIB_EX = 65536; // extension
  
  /* Ref. IEC 61850.8.1 pp25-27 */
  public static final int TYPE_BOOL = 0; // boolean
  public static final int TYPE_INT8 = 1; // -128 - 127
  public static final int TYPE_INT16 = 2; // integer -32,768 - 32,767
  public static final int TYPE_INT32 = 4; // integer -2,147,483,648 - 2,147,483,647
  public static final int TYPE_INT128 = 8; // integer -2^127 - 2^127
  public static final int TYPE_INT8U = 16; // unsigned 0 - 255
  public static final int TYPE_INT16U = 32; // unsigned 0 - 65535
  public static final int TYPE_INT32U = 64; // unsigned 0 - 4,294,967,295
  public static final int TYPE_FLOAT32 = 128; // floating point
  public static final int TYPE_FLOAT64 = 256; // floating point
  public static final int TYPE_ENUM = 512; // integer ordered set of values (Ref. IEC 61850.8.1 8.1.2.2)
  public static final int TYPE_CODED_ENUM = 1024; // ordered set of values (Ref. IEC 61850.8.1 8.1.2.3)
  public static final int TYPE_OCTET_STR = 2048; // octet string, max length to be defined (Ref. IEC 61850.8.1 8.1.2.4)
  public static final int TYPE_VISIBLE_STR = 4096; // visible string, max. length to be defined (Ref. IEC 61850.8.1 8.1.2.5)
  public static final int TYPE_UNICODE_STR = 8192; // MMS string, max. length to be defined (Ref. IEC 61850.8.1 8.1.3.9)
  
  
  // attributes
  
  /* Ref. IEC 61850.6 p65 */
  private int offset; // order of the attribute within the data set
  private int ldInst; // the LD where the DO resides
  private String prefix; // prefix identifying together with lnInst and lnClass the LN where the DO resides
  private int lnClass; // LN class of the LN where the DO resides; shall be specified except for LLN0
  private int lnInst; // Instance number of the LN where the DO resides; shall be specified except for LLN0
  private String doName; // name identifying the DO (within the LN). Standardised in IEC 61850.7.4
  private String daName; // data attribute name
  private int fc; // functional constraint, see IEC 61850.7.2
  
  
  // constructors
  
  /**
   * Constructor accepting the order/offset and the functional constraint of the 
   * attribute
   * 
   * @param offset into the data set for this attribute
   * @param fc the functional constraint of this attribute
   */
  public GOOSEDatasetAttribute( int offset, int fc ) {
    this.setOffset(offset);
    this.setFc(fc);
  }

  
  // accessors
  
  /**
   * @return the offset
   */
  public int getOffset() {
    return offset;
  }


  /**
   * @return the ldInst
   */
  public int getLdInst() {
    return ldInst;
  }


  /**
   * @return the prefix
   */
  public String getPrefix() {
    return prefix;
  }


  /**
   * @return the lnInst
   */
  public int getLnInst() {
    return lnInst;
  }


  /**
   * @return the lnClass
   */
  public int getLnClass() {
    return lnClass;
  }


  /**
   * @return the doName
   */
  public String getDoName() {
    return doName;
  }

  
  /**
   * @return the daName
   */
  public String getDaName() {
    return daName;
  }
  
  
  /**
   * @return the fc
   */
  public int getFc() {
    return fc;
  }
  
  
  // mutators
  
  /**
   * @param daName the daName to set
   */
  public void setDaName(String daName) {
    this.daName = daName;
  }

  
  /**
   * @param fc the fc to set
   */
  public void setFc(int fc) {
    this.fc = fc;
  }
  
  
  /**
   * @param ldInst the ldInst to set
   */
  public void setLdInst(int ldInst) {
    this.ldInst = ldInst;
  }
  
  
  /**
   * @param offset the offset to set
   */
  public void setOffset(int offset) {
    this.offset = offset;
  }
  

  /**
   * @param doName the doName to set
   */
  public void setDoName(String doName) {
    this.doName = doName;
  }
  

  /**
   * @param lnClass the lnClass to set
   */
  public void setLnClass(int lnClass) {
    this.lnClass = lnClass;
  }
  

  /**
   * @param lnInst the lnInst to set
   */
  public void setLnInst(int lnInst) {
    this.lnInst = lnInst;
  }
  
  
  /**
   * @param prefix the prefix to set
   */
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
