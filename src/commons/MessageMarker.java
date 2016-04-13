package commons;

import java.net.InetAddress;

public class MessageMarker extends Message {
	/**
	 * Creates new marker message (this one will be used to run the global snapshot algorithm)
	 * @param sender
	 * @param receiver
	 * @param data
	 */
	public MessageMarker(InetAddress senderAddress, InetAddress receiverAddress) {
		super.messageType = Type.MARKER;
		super.senderAddr = senderAddress;
		super.receiverAddr = receiverAddress;
		
	}
}