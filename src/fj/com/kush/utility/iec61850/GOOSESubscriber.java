/**
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 * 
 * @author Nishchal Kush
 * @version %I%, %G%
 * @since 1.0
 */
package fj.com.kush.utility.iec61850;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacketHandler;

import fj.com.kush.utility.iec61850.net.GOOSEPacketHandler;
// import java.nio.*;
// import org.jnetpcap.packet.format.*;

/**
 * A GOOSE subscriber server that received <code>GOOSEMessage</code>s. The class
 * is intended to be used to provide specific GOOSE functionality by populating
 * the publisher with appropriate <code>GOOSEMessage</code>s. 
 */
// TODO - should be multi-threaded, i.e. implement runnable and have synchronized methods
// TODO: impement subscribing to publishers
public abstract class GOOSESubscriber {

  // constants

  public static final String COMMA = ",";
  public static final int PCAP_IF_LOOPBACK = 1; // TODO: fix in
                                                // another class

  // class attributes
  
  private static List<PcapIf> alldevs = null;
  private static StringBuilder errbuf = null;
  private static boolean initialised = false;
  private static GOOSEPacketHandler packetHandler = null;
  
  // private instance attributes
  
  private Map<String,Boolean> subscribed = null;

  
  // package instance attributes
  
  GOOSEMessage gooseMessage = null;
  boolean verbose = false;

  
  // constructors
  
  /**
   * Default constructor
   */
  protected GOOSESubscriber() {
    gooseMessage = null;
    verbose = false;
    subscribed = new HashMap<String,Boolean>();
    init();
  }

  
  // accessors

  /**
   * Returns a reference to the <code>GOOSEMessage</code>
   * 
   * @returns GOOSEMessage for this publisher
   */
  protected GOOSEMessage getMessage() {
    return gooseMessage;
  }
  
  /**
   * Returns a comma separated <code>String</code> of all IED MAC addresses to 
   * which the subscriber has subscribed to
   * 
   * @returns String  of comma separated MAC addresses
   */
  protected String getSubscription() {
    if ( subscribed != null && !subscribed.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      Set<String> macs = subscribed.keySet();
      Iterator<String> iter = macs.iterator();
      while( iter.hasNext() ) {
        String mac = iter.next();
        sb.append(mac);
        sb.append( GOOSESubscriber.COMMA );
      }
      // trim the last comma
      if ( sb.lastIndexOf(COMMA) == (sb.length() - 1)) {
         sb.deleteCharAt(sb.lastIndexOf(COMMA)); 
      }
      return sb.toString(); 
    } else {
      return ""; 
    }
  }

  
  /**
   * Gets the initilised flag to indicate if the subscriber has been initilised
   * 
   * @return boolean  flag to indicate if subscriber is initiliased
   */
  protected static boolean isInitialised() {
    return GOOSESubscriber.initialised;
  }

  
  /**
   * Checks to see of this <code>GOOSESubscriber</code> is subscribed to the IED
   * with the specified MAC address.
   *
   * @param mac <code>String</code> representing the MAC address of the publishing IED
   * @return Boolean <code>true</code> if subscribed else <code>false</code>
   */
  public boolean isSubscribed( String mac ) {
    if ( mac == null || mac.isEmpty() ) {
      return false;
    }
    
    if ( subscribed.isEmpty() ) {
      return false;
    }
    
    if ( subscribed.containsKey(mac.toLowerCase())) {
      return true;
    }
    
    return false;
  }

  /**
   * Gets the verbose mode.
   * 
   * @return boolean representing verbose mode of this GOOSESubscriber
   */
  public boolean isVerbose() {
    return verbose;
  }

  // mutators

  /**
   * Sets the initialisation flag for the subscriber to indicate if the subscriber has been initialised or not
   * 
   * @param initialised boolean flag to indicate if the subscriber is initilised
   */
  protected static void setInitialised(boolean initialised) {
    GOOSESubscriber.initialised = initialised;
  }

  /**
   * Sets the <code>GOOSEMessage</code> for this subscriber
   * 
   * @param GOOSEMessage
   *          for this subscriber
   */
  protected void setMessage(GOOSEMessage message) {
    this.gooseMessage = message;
  }

  /**
   * Sets verbose mode to produce debug output
   * 
   * @param verbose
   *          boolean representing verbose mode
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * Subscribed to the IED with the specified MAC address for all non-null MACs
   *
   * @param mac the <code>String</code> representing the publishing IEDs MAC
   */
  public void subscribeTo(String mac ) {
    if ( mac == null || mac.isEmpty()) {
      return;
    } else {
      subscribed.put( mac.toLowerCase(),  true );
    }
  }

  // auxillary methods

  /**
   * Abstract method to be implemented by concrete subscriber implementations to
   * process the actual GOOSE message
   */
  public abstract void processMessage(GOOSEMessage gooseMessage);

  /**
   * Received the data on all network interfaces as a GOOSE message. The
   * broadcast data should be an array of bytes that represent a value GOOSE
   * message. i
   * subscription to specific LN and learn MAC based on name
   */
// TODO: separate threat to receive on each nic 
  protected void receive() {
    if (!GOOSESubscriber.isInitialised()) {
      GOOSESubscriber.init();
    }

    int snaplen = 64 * 1024; // capture whole frame, no trucation
    int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
    int timeout = 10 * 1000; // 10 seconds in millis

    // TODO: listen on a specified interface or listen on the first non-virtual
    // interface - but what if we are in a VM?
    //
    // int i = 0;
    // for (PcapIf device : alldevs) {
    // String description = (device.getDescription() != null) ? device
    // .getDescription() : "No description available";
    // System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
    // }
    //

    Iterator<PcapIf> iterator = alldevs.iterator();
    while (iterator.hasNext()) {
      PcapIf nic = (PcapIf) iterator.next();

      // check if it's a useable nic
      byte[] mac = null;
      try {
        mac = nic.getHardwareAddress();
      } catch (IOException ioe) {
        mac = null;
      }

      // null is returned, that means that the physical interface, does not
      // support hardware addresses, such as a PPP interface, TAP interface,
      // etc.., or an exception may have occurred, so do not use this nic
      if (mac == null) {
        continue; // this should not happen since we initialised properly
      }

      // open selected nic in promiscuous mode
      Pcap pcap = Pcap.openLive(nic.getName(), snaplen, flags, timeout, errbuf);

      if (pcap == null) {
        System.err.printf("Can't open device for capture, error is %s",
            errbuf.toString());
        System.exit(0);
      }

      // receive packet
      if (pcap.loop(1, (JPacketHandler<Object>) GOOSESubscriber.packetHandler,
          null) == -1) {
        System.err.printf("Can't read from device, error is %s",
            errbuf.toString());
        continue;
      }

      // get the message from the handler
      this.gooseMessage = packetHandler.getGooseMessage();

      // process the goose message
      if (this.gooseMessage != null) {
        processMessage(this.gooseMessage);
      }

      // do the needful, i.e. help the gc
      nic = null;
      pcap.close();
    }
  }

  /**
   * Method to initialise the subscriber, by maintaining a singleton reference 
   * to the useable network interface cards
   */
  private static void init() {
    alldevs = new ArrayList<PcapIf>();
    errbuf = new StringBuilder();

    int r = Pcap.findAllDevs(alldevs, errbuf);
    if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
      System.err.printf("Can't read list of devices, error is %s",
          errbuf.toString());
      return;
    }

    ArrayList<PcapIf> tmpList = new ArrayList<PcapIf>();

    Iterator<PcapIf> iterator = alldevs.iterator();
    while (iterator.hasNext()) {
      PcapIf nic = (PcapIf) iterator.next();

      // if loopback interface then ignore interface
      if ((nic.getFlags() & PCAP_IF_LOOPBACK) == PCAP_IF_LOOPBACK) {
        continue;
      }

      // check if it's a useable nic
      byte[] mac = null;
      try {
        mac = nic.getHardwareAddress();
      } catch (IOException ioe) {
        mac = null;
      }

      // null is returned, that means that the physical interface, does not
      // support hardware addresses, such as a PPP interface, TAP interface,
      // etc.., or an exception may have occurred, so do not use this nic
      if (mac == null) {
        continue;
      } else {
        tmpList.add(nic);
      }
    }

    GOOSESubscriber.alldevs = tmpList;
    GOOSESubscriber.packetHandler = new GOOSEPacketHandler();
    GOOSESubscriber.setInitialised(true);
  }
}
