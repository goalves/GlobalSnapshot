package commons;

import java.net.InetAddress;

public class Message {
	public enum Type {
		DATA, MARKER
	}

	Type messageType;		// Message type
	
	InetAddress senderAddr;		// Sending process IP Address
	InetAddress receiverAddr;	// Receiving process IP Address

	public Type getMessageType() {
		return messageType;
	}
	
	public InetAddress getSenderAddr() {
		return senderAddr;
	}

	public void setSenderAddr(InetAddress senderAddr) {
		this.senderAddr = senderAddr;
	}

	public InetAddress getReceiverAddr() {
		return receiverAddr;
	}

	public void setReceiverAddr(InetAddress receiverAddr) {
		this.receiverAddr = receiverAddr;
	}
}
