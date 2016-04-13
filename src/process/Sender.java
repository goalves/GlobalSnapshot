package process;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import commons.Constants;
import commons.Message;

public class Sender implements Runnable {
	@SuppressWarnings("unused")
	private String threadName;
	private Client processClient;
	
	public Sender(String name, Client client) {
		this.processClient = client;
		threadName = name;
	}

	@Override
	/**
	 * Continuously check for message at head of the queue and send it to the
	 * corresponding process
	 */
	public void run() {
		while (true) {
			// Send element at the head of the queue
			if (processClient.getQueue().hasElements()) {
				Message message = processClient.getQueue().remove();
				sendToProcess(message);
			}
		}
	}
	
	public void sendToProcess(Message message) {
		sendObject(message, Constants.PORT_LISTEN, message.getReceiverAddr());
	}
	
	/**
	 * Send object to a process
	 * 
	 * @param object
	 * @param port
	 * @param receiverIP
	 * 
	 */
	public void sendObject(Object object, int port, InetAddress receiverIP) {
		try {
			DatagramSocket sendSocket;
			sendSocket = processClient.getClientSocket();
			
			// Close the previous socket if left open
			if (sendSocket != null && !sendSocket.isClosed())
				sendSocket.close();

			// Build a socket
			sendSocket = new DatagramSocket();

			// Put message object in a stream
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(object);

			// Send stream over the socket
			byte[] sendBuf = outputStream.toByteArray();
			
			DatagramPacket sendPacket;
			sendPacket = new DatagramPacket(sendBuf, sendBuf.length, receiverIP, port);
			sendSocket.send(sendPacket);
			
			os.close();
			sendSocket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
