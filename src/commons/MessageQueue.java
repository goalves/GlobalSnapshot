package commons;

import java.util.LinkedList;

public class MessageQueue {
	public static LinkedList<Message> FIFO = new LinkedList<Message>();

	// Add message to the tail
	public void add(Message message) {
		FIFO.add(message);
	}

	// Remove message from head
	public Message remove() {
		if (FIFO.isEmpty()) {
			return null;
		} else {
			Message message = FIFO.remove();
			return message;
		}
	}

	// Returns true if queue has any element
	public boolean hasElements() {
		return !FIFO.isEmpty();
	}
}
