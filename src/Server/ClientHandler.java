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

/*
 * Class that runs as its own thread to interact with the client
 * takes user input and sends it to server
 * 
 * @author Rohan Bapat
 * @version 1.0
 * @since 06-15-17
 */

public class ClientHandler implements Runnable {

	private ServerSocket server;
	private Socket client;
	private BufferedReader socketIn;
	private PrintWriter socketOut;
	private ConcurrentHashMap<String, ClientHandler> clients;
	private ConcurrentLinkedDeque<Message> messageQueue;
	private String name;
	private int port;
	
	/*
	 * instantiates and connects to server socket
	 * sets up input and output streams to client socket
	 * received initial user input and starts thread
	 *	 
	 * @param clients
	 *	thread-safe ConcurrentHashMap that maps a clients username to their respective ClientHandler object. Dispatch thread uses this to retreive clients
	 * @param messageQueue
	 *	thread-safe ConcurrentLinkedDeque of Messages. The dispatch thread send messages that are stored here to a client
	 * @param
	 *	integer which represents which TCP Server port to connect to
	 */
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
	/*
	 * wrapper that prompts user for their name
	 * 
	 * @return String of name given by client
	 */
	private String promptName() throws IOException {
		send("What is your name?");
		return recv();
	}
	
	/*
	 * retreives names of all users from ConcurrentHashMap
	 * 
	 * @return String of names of all cients
	 */
	private String getCurrentUsers() {
		String users = "";
		Set<String> userSet = clients.keySet();
		for (String user : userSet)
			users += (user + "#");
		return users;
	}
	
	/*
	 * retreives name of client
	 * 
	 * @return String of name of client
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * wrapper to send data to client socket
	 * @param
	 * 	String to send to client
	 */
	public void send(String payload) {
		socketOut.println(payload);
	}
	
	/*
	 * wrapper to receive data from client socket
	 *
	 * @return data received from client
	 */
	public String recv() throws IOException {
		return socketIn.readLine();

	}
	
	/*
	 * handles user commands
	 * @param
	 * 	String of command to execute
	 */
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
	
	/*
	 * Main loop of thread, handles user input and pushes it to dispatch queue
	 */
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
