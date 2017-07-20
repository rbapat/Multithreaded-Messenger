package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import Shared.Message;

public class Server {
	
	private UDPHandler udp;
	private DispatchThread dispatch;
	private ConcurrentHashMap<String, ClientHandler> clients;
	private ConcurrentLinkedDeque<Message> messageQueue;
	private BufferedReader fileIO;
	private ServerSocket server;
	private int minPort;

	public Server() throws IOException {
		clients = new ConcurrentHashMap<String, ClientHandler>();
		messageQueue = new ConcurrentLinkedDeque<>();
		fileIO = new BufferedReader(new FileReader(new File(("serverConfig.txt"))));		
		dispatch = new DispatchThread(clients, messageQueue);
		int[] data = parseData();
		this.minPort = data[0];
		udp = new UDPHandler(clients, messageQueue,minPort, data[1]);
		

 	}
	
	private int[] parseData() throws IOException {
		String s;
		int minPort = 0;
		int maxThreads = 0;
		while((s = fileIO.readLine()) != null) {
			String[] data = s.split(":");
			if(data[0].equals("minPort"))
				minPort = Integer.parseInt(data[1]);
			else if(data[0].equals("maxThreads"))
				maxThreads = Integer.parseInt(data[1]);
 		}
		
		return new int[]{minPort, maxThreads};
	}
	
	public void startThreads() {
		udp.start();
		dispatch.start();
	}
	
	public void serverMain() throws IOException {
		startThreads();
		while(true) {
				ClientHandler client = new ClientHandler(clients, messageQueue, minPort++);
				clients.put(client.getName(), client);
		}
	}
	
	public static void main(String[] args) {
		try {
			Server server = new Server();
			server.serverMain();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
