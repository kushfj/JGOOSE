/**
 * 
 */
package fj.com.kush.utility.iec61850;

/**
 * @author core
 *
 */
public class GOOSEControlBlockData {
  
  // private attributes
  
  private int memberOffset = -1;
  private byte[] memberReference = null; // FCD (functionally constrained data
                                         //  or FDCA (functionally constrained
                                         //  data attribute
  
  
  // constructors
  
  /**
   * Constructor accepting the member offset and the (FDC) member reference
   * 
   *  @param memberOffset the member offset
   *  @param memberReference the functionally constrained data
   */
  public GOOSEControlBlockData(int memberOffset, byte[] memberReference) {
    super();
    this.setMemberOffset(memberOffset);
    this.setMemberReference(memberReference);
  }
  
  
  //public methods
  
  /**
   * Sets the member offset
   * 
   * @param memberOffset the memberOffset to set
   */
  public void setMemberOffset(int memberOffset) {
    this.memberOffset = memberOffset;
  }


  /**
   * Gets the member offset for this control block data
   * @return the memberOffset
   */
  public int getMemberOffset() {
    return memberOffset;
  }

  
  /**
   * Gets the member reference or functionally constrained data for this 
   * control block
   * 
   * @return the memberReference
   */
  public byte[] getMemberReference() {
    return this.memberReference;
  }

  
  /**
   * Sets the member reference or functionally constrained data for this 
   * control block 
   * 
   * @param memberReference the memberReference to set
   */
  public void setMemberReference(byte[] memberReference) {
    this.memberReference = memberReference;
  }
  
  // TODO - complete implementation to add toBytes method when 
}
