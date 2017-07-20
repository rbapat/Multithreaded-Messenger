package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import Shared.Message;

public class ClientHandler implements Runnable {

	private ServerSocket server;
	private Socket client;
	private BufferedReader socketIn;
	private PrintWriter socketOut;
	private ConcurrentHashMap<String, ClientHandler> clients;
	private ConcurrentLinkedDeque<Message> messageQueue;
	private String name;
	private int port;

	public ClientHandler(ConcurrentHashMap<String, ClientHandler> clients, ConcurrentLinkedDeque<Message> messageQueue, int port) {
		try {
			this.clients = clients;
			this.messageQueue = messageQueue;
			server = new ServerSocket(port);
			client = server.accept();
			socketIn = new BufferedReader(new InputStreamReader(client.getInputStream()));
			socketOut = new PrintWriter(client.getOutputStream(), true);
			this.port = port;
			name = promptName();
			send(getCurrentUsers());
		} catch (IOException e) {
			System.out.println("Client has disconnected");
		}
		new Thread(this).start();
	}

	private String promptName() throws IOException {
		send("What is your name?");
		return recv();
	}

	private String getCurrentUsers() {
		String users = "";
		Set<String> userSet = clients.keySet();
		for (String user : userSet)
			users += (user + "#");
		return users;
	}

	public String getName() {
		return name;
	}

	public void send(String payload) {
		socketOut.println(payload);
	}

	public String recv() throws IOException {
		return socketIn.readLine();

	}

	private void command(String command) throws IOException {
		if (command.equals("u")) {
			socketOut.println("/u");
			socketOut.println(getCurrentUsers());
		} else if (command.equals("q")) {
			client.close();
			server.close();
			socketIn.close();
			socketOut.close();
			throw new IOException();
		}
	}

	public void run() {
		while (true) {
			try {
				if (socketIn.ready()) {
					String data = recv();
					if (data == null)
						continue;
					else if (data.charAt(0) == '/') {
						command(data.replace("/", ""));
						continue;
					}

					Message message = new Message(data);
					if (!message.getSender().equals(name)) {
						System.out.println("Validation Issue: Invalid Sender");
						break;
					}

					messageQueue.push(message);
				}
			} catch (IOException e) {
				System.out.println(name + " has disconnected");
				break;
			}
		}
		clients.remove(name);

	}
}