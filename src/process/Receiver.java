package process;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import commons.Message;
import commons.MessageData;
import commons.MessageMarker;

public class Receiver implements Runnable {
	@SuppressWarnings("unused")
	private String threadName;
	Client processClient;
	
	public Receiver(String name, Client client) {
		this.processClient = client;
		threadName = name;
	}

	/*
	 * Receivers will always be running. They make sure that no message will have its sender as the current node (client)
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// Read the packet from incoming socket connection
				byte[] recvBuf = new byte[5000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				processClient.getServerSocket().receive(packet);
				byte[] data = packet.getData();

				// Read object in the stream
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream is = new ObjectInputStream(in);
				Object rObj = is.readObject();

				// Process message based on it's type
				try {
					Message msg = (Message) rObj;

					// Accept only messages addressed to us and are not from us
					if (processClient.getClientIP() == msg.getReceiverAddr() && processClient.getClientIP() != msg.getSenderAddr()) {
						if (msg.getMessageType() == Message.Type.DATA)
							processData((MessageData) rObj);
						else if (msg.getMessageType() == Message.Type.MARKER)
							processMarker((MessageMarker) rObj);
					}
				} catch (ClassCastException e) {
					System.out.println("Unknown object type received!");
				}

			} catch (Exception e) {

			}
		}
	}
	
	/**
	 * Processes normal data
	 */
	void processData(MessageData msg) {
		// update data on the client
		processClient.setData(msg.getData());
	}

	/**
	 * Processes a marker packet
	 * This follows exactly the specified pseudo-code in the PDF
	 */
	void processMarker(MessageMarker msg) {
		// If any process Pi receives a marker message on an incoming channel Cki
		if(processClient.isHasSeenMarker())
		{
			processClient.setHasSeenMarker(true);
			// Records its own state
			processClient.setState(processClient.getData());
			ArrayList<ArrayList<Message>> processChannelStates = processClient.getChannelStates();
			
			// Search for the one who sent this one and mark its channel as empty
			ArrayList<InetAddress> neighbours = processClient.getneighborsIPs();
			int i = 0;
			for(InetAddress n : neighbours)
			{
				if(n == msg.getReceiverAddr())
					processChannelStates.get(i).clear();
				i++;
			}

			for(InetAddress n : neighbours)
			{
				if(n != msg.getSenderAddr())
				{
					MessageMarker marker = new MessageMarker(processClient.getClientIP(),n);
					processClient.getQueue().add(marker);
				}
				
			}
		}

		else
		{
			// Search for the one who sent this one and mark its channel as all the messages that have arrived on it since
			ArrayList<InetAddress> neighbours = processClient.getneighborsIPs();
			ArrayList<ArrayList<Message>> processChannelStates = processClient.getChannelStates();
			int i =0;
			for(InetAddress n : neighbours)
			{
				if(n == msg.getReceiverAddr())
					processChannelStates.get(i).add(msg);
				i++;
			}
		}
	}

}
