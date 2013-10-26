/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 * A GOOSE broadcast server that broadcasts <code>GOOSEMessage</code>s. The
 * class is intended to be used to provide specific GOOSE functionality by
 * populating the publisher with appropriate <code>GOOSEMessage</code>s.
 * 
 * TODO - complete this implementation, currently just a copy of the
 * DummyGOOSEServer that was developed as a PoC!! TODO - should be
 * multi-threaded
 */
public abstract class GOOSEPublisher {

	// constants
  
  // TODO: put in appropriate class or use other appropriate reference
  public static final int    PCAP_IF_LOOPBACK = 1;

  // broadcast MAC address
  public static final byte[] BROADCAST_MAC = new byte[] { (byte)0xFF,
      (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF };

  
	// attributes

  // class attributes

  // TODO: debug
  static int                   count            = 0;                    // debug

  static private List<PcapIf>  alldevs          = null;
  static private StringBuilder errbuf           = null;
  static private boolean       initialised      = false;

  // private attributes

  // private float delay = 0;
  private boolean            verbose          = false;

  // package attributes

  int                        flags            = Pcap.MODE_PROMISCUOUS;
  GOOSEMessage               gooseMessage     = null;
  
  int                        snaplen          = 64 * 1024;
  int                        timeout          = 10 * 1000;
  
  // constructors

  protected GOOSEPublisher() {
    // alldevs = null;
    // errbuf = null;
    // GOOSEPublisher.setInitialised( false );
    verbose = false;
    // delay = 0;
    initialised = false;
    gooseMessage = null;
    snaplen = 64 * 1024;
    flags = Pcap.MODE_PROMISCUOUS;
    timeout = 10 * 1000;
    init(); // initialise interfaces
  }

  // accessors

  /**
   * Gets the delay between broadcasts in seconds
   * 
   * @return int representing the delay between broadcasts in seconds
   */
  // public float getDelay() {
  // return delay;
  // }

  /**
   * Returns a reference to the <code>GOOSEMessage</code>
   * 
   * @returns GOOSEMessage for this publisher
   */
	protected GOOSEMessage getMessage() {
		return gooseMessage;
	}

	  /**
   * Gets the initialised flag for the publisher
   * 
   * @return boolean true if initialised, else false
   */
  protected static boolean isInitialised() {
    return initialised;
  }

  /**
   * Gets if the publisher is in verbose mode
   * 
   * @return boolean representing the verbose mode of the publisher
   */
	public boolean isVerbose() {
		return verbose;
	}

  // mutators

  /**
   * Sets the delay between broadcast. The delay is specified in seconds.
   * Decimal values may be supplied for values which are to introduce delays
   * less than a second
   * 
   * @param delay
   *          the delay specified in seconds
   */
  // public void setDelay( float delay ) {
  // this.delay = delay;
  // }

  /**
   * Sets the initialised flag for the publiser.
   * 
   * @param initialised
   */

  protected static void setInitialised( boolean initialised ) {
    GOOSEPublisher.initialised = initialised;
  }

  /**
   * Sets the <code>GOOSEMessage</code> for this publisher
   * 
   * @param GOOSEMessage
   *          for this publisher
   */
	protected void setMessage(GOOSEMessage message) {
		this.gooseMessage = message;
	}

	/**
	 * Sets the verbose mode of the publisher to the mode specified
	 * 
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

  // auxillary methods

	/**
	 * Broadcast the data on all network interfaces as a GOOSE message. The
	 * broadcast data should be an array of bytes that represent a valud GOOSE
	 * message.
	 */
	protected void broadcast() {
    if (!GOOSEPublisher.isInitialised()) {
      init();
    }

    Iterator<PcapIf> iterator = GOOSEPublisher.alldevs.iterator();
		while (iterator.hasNext()) {
			PcapIf nic = (PcapIf) iterator.next();

      Pcap pcap = Pcap.openLive( nic.getName(), this.snaplen, this.flags,
          this.timeout, GOOSEPublisher.errbuf );

      if (pcap == null) {
        System.err.printf( "Can't open device for capture, error is '%s'",
            errbuf.toString() );
        // System.exit( 0 );
        continue;
      }

			byte[] mac = null;

			try {
				mac = nic.getHardwareAddress();
			} catch (IOException ioe) {
				mac = null;
			}

			// null is returned, that means that the physical interface, does
      // not support hardware addresses, such as a PPP interface, TAP
      // interface, etc.. so do not use it
			if (mac != null) {
        byte[] tmp = null;

				try {
					gooseMessage.setSrc(mac);
					gooseMessage.setDst(GOOSEMessage.GOOSE_BCAST_MAC);
					tmp = gooseMessage.toBytes();
				} catch (GOOSEMessageException gme) {
					System.err.print(gme.getMessage());
					gme.printStackTrace();
				}

				// send the packet
				ByteBuffer buffer = ByteBuffer.wrap(tmp);
				if (pcap.sendPacket(buffer) != Pcap.OK) {
					System.err.println(pcap.getErr());
				}
        // TODO: debug
        count++;

				pcap.close();

        // help the gc
        buffer = null;
        tmp = null;

        // check if output is to be produced
        if (this.isVerbose()) {
          System.out.println( gooseMessage.toString() );
        } else {
          System.out.print( "!" ); // TODO: use constant
        }

        // Code below commented out to have delay implemented by extending
        // class, i.e. virtual devices can specify their own delays based on
        // whatever property they are simulating to have an appropriate delay in
        // the emulated network traffic
        //
        // introduce a delay
        // try {
        // Thread.sleep( (long)( delay ) );
        // } catch (Exception e) {
        // do nothing for now
        // TODO: do something clever here... timeout or re-init
        // }
			}

      // trying to help the gc
      nic = null;
      mac = null;
		}
	}

  /**
   * TODO: uopdate comments and documentation
   */
  private static final void init() {
    GOOSEPublisher.alldevs = new ArrayList<PcapIf>();
    GOOSEPublisher.errbuf = new StringBuilder();

    // get all interfaces
    int r = Pcap.findAllDevs( GOOSEPublisher.alldevs, GOOSEPublisher.errbuf );

    if (r == Pcap.NOT_OK || GOOSEPublisher.alldevs.isEmpty()) {
      System.err.printf( "Can't read list of devices, error is '%s'",
          errbuf.toString() );
      System.exit( 0 );
      return;
    }
    
    ArrayList<PcapIf> tmpList = new ArrayList<PcapIf>();

    Iterator<PcapIf> iterator = GOOSEPublisher.alldevs.iterator();
    while (iterator.hasNext()) {
      PcapIf nic = (PcapIf) iterator.next();
      
      // if loopback interface then ignore interface
      if (( nic.getFlags() & PCAP_IF_LOOPBACK ) == PCAP_IF_LOOPBACK) {
        continue;
      }

      byte[] mac = null;

      try {
        mac = nic.getHardwareAddress();
      } catch (IOException ioe) {
        mac = null;
      }

      // if cannot get mac then ignore interface
      if (mac != null) {
        tmpList.add( nic );
      }
    }

    GOOSEPublisher.alldevs = tmpList;
    setInitialised( true );
  }
}
