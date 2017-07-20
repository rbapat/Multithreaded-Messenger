package Server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import Shared.Message;

public class DispatchThread implements Runnable {

	private ConcurrentHashMap<String, ClientHandler> clients;
	private ConcurrentLinkedDeque<Message> messageQueue;

	public DispatchThread(ConcurrentHashMap<String, ClientHandler> clients, ConcurrentLinkedDeque<Message> messageQueue) {
		this.clients = clients;
		this.messageQueue = messageQueue;
	}

	public void run() {
		while (true) {
			Message m = null;
			try {
				if (!messageQueue.isEmpty()) {
					m = messageQueue.pop();
					String target = m.getTarget();
					String payload = m.getPayload();
					ClientHandler targetH = clients.get(target);
					targetH.send(payload);
					clients.get(m.getSender()).send("[s] Sent Successfully");
					
				}
			} catch (NullPointerException e) {
				clients.get(m.getSender()).send("[s] Target Not Found");
				// clients.get(sender).send("[s] Target Not Found");
				continue;

			}
		}
	}

	public void start() {
		new Thread(this).start();
	}

}
