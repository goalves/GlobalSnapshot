package commons;

import java.net.InetAddress;

public class MessageData extends Message {
	int data;

	/**
	 * Creates new data message (this one will not be used from the inner code since it should represent a normal user message)
	 * @param sender
	 * @param receiver
	 * @param data
	 */
	public MessageData(InetAddress senderAddress, InetAddress receiverAddress, int data) {
		super.messageType = Type.DATA;
		
		super.senderAddr = senderAddress;
		super.receiverAddr = receiverAddress;
		
		this.data = data;
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}
}
