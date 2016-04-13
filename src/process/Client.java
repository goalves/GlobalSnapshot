package process;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import commons.Constants;
import commons.Message;
import commons.MessageMarker;
import commons.MessageQueue;

public class Client {
	private MessageQueue queue;
	private DatagramSocket sendSocket;
	private DatagramSocket receiveSocket;
	private InetAddress clientIP;
	private ArrayList<InetAddress> neighborsIPs = new ArrayList<InetAddress>();
	
	//Channels for each neighbor (to store messages)
	private ArrayList<ArrayList<Message>> channelStates = new ArrayList<ArrayList<Message>>();
	
	private Boolean isRecording = false;
	private int state = -1;

	private boolean hasSeenMarker = false;
	
	private int processId;
	private int data = -1;
	Object dataLock = new Object();
	
	
	// The main problem is that to ensure a snapshot algorithm every node must have a connection to every other node
	// and this isn't easy to simulate without using threads, so this code will not ever run by itself, it will be used as an API.
	// The client who uses this API must know every node in the network and have a connection to it
	
    public void init(int id, InetAddress IP, ArrayList<InetAddress> neighbors) {
    	queue = new MessageQueue();
    	setData(ThreadLocalRandom.current().nextInt(10, 500));
        setProcessId(id);
        setClientIP(IP);
        setneighborsIPs(neighbors);
        
        // Creates a new entering message channel for each neighbor
        for(int i = 0; i < neighborsIPs.size(); i++)
        	channelStates.add(new ArrayList<Message>());
        
        try {
			sendSocket = new DatagramSocket();
			try {
				
				receiveSocket = new DatagramSocket(Constants.PORT_LISTEN);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		System.out.println("Initial value " + getData());
		Thread S = new Thread(new Sender("Sender" + getClientIP().getHostAddress(), this));
		S.start();
		Thread R = new Thread(new Receiver("Receiver" + getClientIP().getHostAddress(), this));
		R.start();
		
		//Other things that the process must do outside of the API~
    }
    
    /*
     * First process that will start the snapshot algorithm
     * This follows exactly the specified pseudo-code in the PDF
     */
    public void initSnapShot()
    {
    	//Records its own state
    	state = data;
    	//Creates the marker messages and sends out one marker for each neighbor
		for(InetAddress n : getneighborsIPs())
		{
			MessageMarker marker = new MessageMarker(getClientIP(),n);
			getQueue().add(marker);
		}
		this.setHasSeenMarker(true);
    	
    	
    }
    
    //Getters and setters area
    
	public void setData(int value) {
		synchronized (dataLock){
			this.data = value;
		}
	}
	public int getData() {
		synchronized (dataLock){
			return data;
		}
	}
	
	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}
    
	public DatagramSocket getClientSocket() {
		return sendSocket;
	}

	public void setClientSocket(DatagramSocket clientSocket) {
		sendSocket = clientSocket;
	}

	public DatagramSocket getServerSocket() {
		return receiveSocket;
	}
	
	
	public MessageQueue getQueue() {
		return queue;
	}

	public void setQueue(MessageQueue queue) {
		this.queue = queue;
	}

	public Boolean getIsRecording() {
		return isRecording;
	}

	public void setIsRecording(Boolean isRecording) {
		this.isRecording = isRecording;
	}
	
	public ArrayList<InetAddress> getneighborsIPs() {
		return neighborsIPs;
	}

	private void setneighborsIPs(ArrayList<InetAddress> neighborsIPs) {
		this.neighborsIPs = neighborsIPs;
	}
	
	public boolean isHasSeenMarker() {
		return hasSeenMarker;
	}

	public void setHasSeenMarker(boolean hasSeenMarker) {
		this.hasSeenMarker = hasSeenMarker;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	public ArrayList<ArrayList<Message>> getChannelStates() {
		return channelStates;
	}

	public InetAddress getClientIP() {
		return clientIP;
	}

	public void setClientIP(InetAddress clientIP) {
		this.clientIP = clientIP;
	}
}
