package Server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import Shared.Message;
import Shared.UDPMessage;

public class UDPHandler implements Runnable {

	private DatagramSocket udpServer;
	private ConcurrentHashMap<String, ClientHandler> clients;
	private ConcurrentLinkedDeque<Message> messageQueue;
	private UDPMessage message;
	private int minPort;
	private int maxThreads;

	public UDPHandler(ConcurrentHashMap<String, ClientHandler> clients, ConcurrentLinkedDeque<Message> messageQueue,int minPort, int maxThreads) throws IOException {
		udpServer = new DatagramSocket(8888);
		message = new UDPMessage("", 0);
		this.maxThreads = maxThreads;
		this.minPort = minPort;
		this.clients = clients;
		this.messageQueue = messageQueue;
	}

	public void run() {
		try {
			while (true) {
				udpServer.receive(message.getPacket());
				String[] data = message.getMessage().split("#");
				if(clients.size() > maxThreads)
					message.setMessage(-1 + "#");
				else
					message.setMessage((minPort++) + "#");
				udpServer.send(message.getPacket());
				message.clearMessage();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		new Thread(this).start();
	}
}
