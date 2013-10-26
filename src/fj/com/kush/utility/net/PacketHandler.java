/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.net;

import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;

/**
 * Generic network packet handler abstract class which implements the handler
 * design as a chain of responsibility pattern. This is a A dispatchable packet
 * handler which receives fully decoded packets from the underlying libpcap
 * library.
 */
public abstract class PacketHandler implements JPacketHandler<Object> {

	// attributes
	protected PacketHandler _handler = null;

	/**
	 * Handles the packet specified, and forward packet to the responsible
	 * chained handler once done
	 * 
	 * @param packet
	 *            the frame/packet to process
	 */
	public abstract void handlePacket(JPacket packet);

	/**
	 * Callback function called on by the underlying libpcap library and the
	 * JNetPCAP scanner once a new packet arrives and has passed the set
	 * filters. The packet object dispatched is not allocated on a per call
	 * basis, but is shared between every call made. At the time the pcap
	 * dispatch or loop is established a freshly allocated packet is used to
	 * peer with received packet buffers from the libpcap library, scanned then
	 * dispatched to this method for the user to process. The packet memory and
	 * state is not persistent between calls. If a more persistent state is need
	 * it must be copied out of the supplied packet into a more permanent
	 * packet, i.e. cloned out of the shared memory allocation
	 * 
	 * @param packet
	 *            the packet reference when called from the underlying libpcpap
	 * @param obj
	 *            the user object reference
	 */
	public void nextPacket(JPacket packet, Object obj) {
		this.handlePacket(packet);
	}

	/**
	 * Adds the supplied <code>PacketHandler</code> to the chain, i.e. once this
	 * handler is done processing the frame/packet, it will be forwarded to the
	 * chained handler for further processing. To remove the chained handler
	 * from this handler, make a call to this method will a <code>
	 * null</code> parameter
	 * 
	 * @param handler
	 *            the <code>PacketHandler</code> to chain to
	 */
	public void chainHandler(PacketHandler handler) {
		this._handler = handler;
	}
}
