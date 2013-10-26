/**
 *
 * Copyright (c) 2008, Packwolf Consulting Ltd, All Rights Reserved.
 *
 * @author      Nishchal Kush
 * @version     %I%, %G%
 * @since       1.0
 */
package fj.com.kush.utility.iec61850.net;

import org.jnetpcap.packet.JPacket;
import org.jnetpcap.protocol.lan.Ethernet;

import fj.com.kush.utility.iec61850.GOOSEMessage;
import fj.com.kush.utility.iec61850.GOOSEMessageGoosePDU;
import fj.com.kush.utility.iec61850.MessageException;
import fj.com.kush.utility.net.PacketHandler;


/**
 * TODO: comment
 */
public final class GOOSEPacketHandler extends PacketHandler {

	// attributes

	private GOOSEMessage gooseMessage = null;

	// constructors

	/**
	 * Default constructor
	 */
	public GOOSEPacketHandler() {
		// TODO Auto-generated constructor stub
		gooseMessage = new GOOSEMessage();
	}

	// accessors

	/**
	 * Gets a reference to the <code>GOOSEMessage</code> for this handler
	 * 
	 * @return the gooseMessage
	 */
	public GOOSEMessage getGooseMessage() {
		return gooseMessage;
	}

	// mutators

	/**
	 * Sets a reference to the <code>GOOSEMessage</code> for this handler
	 * 
	 * @param gooseMessage
	 *            the gooseMessage to set
	 */
	public void setGooseMessage(GOOSEMessage gooseMessage) {
		this.gooseMessage = gooseMessage;
	}

	// auxillary methods

	/**
	 * Handles the packet specified, and forward packet to the responsible
	 * chained handler once done. In this case, the handler parses the packet
	 * and creates a <code>GOOSEMessage</code>
	 * 
	 * @param packet
	 *            the packet to parse to create the <code>GOOSEMessage</code>
	 */
	@Override
	public void handlePacket(JPacket packet) {
		Ethernet ethernetHeader = new Ethernet();
    if (packet.hasHeader( ethernetHeader )) {
      // If it's not a 61850 message then don't handle the packet
      if (ethernetHeader.type() != GOOSEMessage.GOOSE_MSG) {
        return;
      }

			try {
        if (gooseMessage == null) {
          gooseMessage = new GOOSEMessage();
        }
        gooseMessage.init();

				// set destination and source mac
				gooseMessage.setDst(ethernetHeader.destination());
				gooseMessage.setSrc(ethernetHeader.source());

				// set ethernet type
				switch (ethernetHeader.type()) {
          case GOOSEMessage.GOOSE_MSG: // GOOSE
            gooseMessage.setMessageType( GOOSEMessage.GOOSE_MESSAGE );
            // set goose payload
            byte[] payload = ethernetHeader.getPayload();
            GOOSEMessageGoosePDU goosePDU = GOOSEMessageGoosePDU.getInstance(
                payload, 8 );
            // GOOSEMessageGoosePDU goosePDU = GOOSEMessageGoosePDU.getInstance(
            // payload, 7 );
            gooseMessage.setAPDU( goosePDU );
            break;
          case GOOSEMessage.GSE_MGMT_MSG: // GSE
            gooseMessage.setMessageType( GOOSEMessage.GSE_MANAGEMENT_MESSAGE );
            // TODO: handle GSE management message
            break;
          case GOOSEMessage.SVM_MSG: // SVM
            gooseMessage.setMessageType( GOOSEMessage.SV_MESSAGE );
            // TODO: handle SVM message
            break;
          default: // not GOOSE traffic so ignore the frame
            gooseMessage = null;
            return;
				}
			} catch (MessageException me) {
				// TODO - write error message
				return;
			}
		}

		// chain to next handler
		if (this._handler != null) {
			this._handler.handlePacket(packet);
		}

		return;
	}
}
