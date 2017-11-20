package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

import Shared.Message;
import Shared.UDPMessage;

/*
 * Class to interface with the user and send user input to the server
 * 
 * @author Rohan Bapat
 * @version 1.0
 * @since 06-15-17
 */

public class Client {
	private DatagramSocket udpSocket;
	private UDPMessage message;
	private BufferedReader stdIn, socketIn;
	private PrintWriter socketOut;
	private Socket clientSocket;
	private String name, hostAddress;
	private StdInEvent stdInEvent;
	private SocketInEvent socketInEvent;
	private BufferedReader fileIO;
		
	/*
	 * initializes UDP socket and message, input/output streams to socket, and config file reader
	 *
	 * @param udpPort
	 *	port to connect udp socket to
	 *
	 */
	public Client(int udpPort) throws IOException {
		udpSocket = new DatagramSocket();		
		stdIn = new BufferedReader(new InputStreamReader(System.in));
		stdInEvent = new StdInEvent(stdIn);
		name = "";
		fileIO = new BufferedReader(new FileReader(new File(("clientConfig.txt"))));
		this.hostAddress = parseData();
		message = new UDPMessage(hostAddress, udpPort);
	}
	
	/*
	 * retrieves config info from config file
	 *
	 * @return server address string
	 */
	private String parseData() throws IOException {
		String s, ret = "";
		while((s = fileIO.readLine()) != null) {
			String[] data = s.split(":");
			if(data[0].equals("serverAddr"))
				ret = data[1];
 		}
		
		return ret;
	}
	/*
	 * intitializes and connects to TCP socket and sets up input/output streams
	 *
	 * @param port
	 *	port to connect TCP socket to
	 *
	 */
	public void connectTCP(int port) throws IOException {
		clientSocket = new Socket(hostAddress, port);
		socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
		socketInEvent = new SocketInEvent(socketIn);
	}
	
	/*
	 * requests an open TCP port from server via 
	 *
	 * @param port
	 *	port to connect TCP socket to
	 *
	 * @return integer of requested TCP port
	 */
	public int requestPort() throws IOException {
		message.setMessage("request_connection#");
		udpSocket.send(message.getPacket());
		udpSocket.receive(message.getPacket());
		String[] response = message.getMessage().split("#");
		udpSocket.close();
		return Integer.parseInt(response[0]);
	}
	
	/*
	 * prompts user to enter name 
	 */
	public void promptName() throws IOException {
		boolean valid = false;
		String request = socketIn.readLine();
		while (!valid) {
			System.out.println(request);
			name = stdIn.readLine();
			if (name.contains(" ")) {
				System.out.println("Invalid name: names cannot contain spaces or #'s. Please try again");
				continue;
			} else
				valid = true;
		}
		socketOut.println(name);
		System.out.println("\nWelcome, " + name);
	}
	/*
	 * executes a given console command
	 *
	 * @param command
	 *	string with command to execute
	 */
	private void command(String command) throws IOException {
		if (command.equals("quit") || command.equals("q")) {
			socketOut.println("/q");
			stdInEvent.kill();
			socketInEvent.kill();
			socketOut.close();
			socketIn.close();
			clientSocket.close();
			stdIn.close();

		} else if (command.equals("users") || command.equals("u")) {
			socketOut.println("/u");
		} else if (command.equals("clear") || command.equals("c")) {
			System.out.print("\033[H\033[2J");  //will maybe work, havent tested
    			System.out.flush();  
		} else if (command.equals("help") || command.equals("h")) {
			System.out.println("\nTo message someone, type \"message <user> <message>\"");
			System.out.println("The current commands are:");
			System.out.println("/c or /clear\tclears the console window");
			System.out.println("/h or /help\tlists all current commands");
			System.out.println("/q or /quit\tcloses messenger gracefully");
			System.out.println("/u or /users\tlists all online users");
		}
	}
	/*
	 * prints all online users
	 */
	public void printCurrentUsers() throws IOException {
		String[] users = socketIn.readLine().split("#");
		System.out.print("Online users: ");
		for (String user : users) {
			System.out.print(user + " ");
		}
		System.out.println("\n");
	}
	
	/*
	 * main loop of client implemented in an observer pattern
	 * creates two respective threads for each input/output event
	 */
	public void clientMain() throws IOException, InterruptedException {

		stdInEvent.addObserver(new Observer() {
			/*
			 * update function for input observer
			 * gets input from user and sends it to server
			 */
			public void update(Observable obj, Object input) {
				try {
					String data = input.toString();
					if (data.charAt(0) == '/') {
						command(data.replaceFirst("/", ""));
					} else {
						String oper = data.substring(0, data.indexOf(" "));
						String person = data.substring(data.indexOf(" ") + 1, data.indexOf(" ", data.indexOf(" ") + 1));
						String message = data.replaceFirst(oper, "").replaceFirst(person, "");
						Message msg = new Message(name, oper, person, message);
						socketOut.println(msg.getPayload());
					}

				} catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
					System.out.println("Invalid Format, try <operation>,<target>:<message>");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		socketInEvent.addObserver(new Observer() {
			/*
			 * update function for output observer
			 * gets payload from server and prints it to console
			 */
			public void update(Observable obj, Object input) {
				if (input.toString().startsWith("/u")) {
					try {
						printCurrentUsers();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (input.toString().startsWith("[s]")) {
					System.out.print(input.toString());
				} else {
					Message message = new Message(input.toString());
					if (!message.getTarget().equals(name))
						return;
					System.out.println(message.getSender() + "->" + name + ": " + message.getMessage());
				}
			}
		});

		Thread stdInThread = new Thread(stdInEvent);
		Thread socketInThread = new Thread(socketInEvent);

		stdInThread.start();
		socketInThread.start();

		stdInThread.join();
		socketInThread.join();
	}
	
	/*
	* entry point for client
	* attempts to connect to TCP port, retrieves username and prints users, and begin observer threads
	*/
	public static void main(String[] args) {
		try {
			Client client = new Client(8888);
			int port = client.requestPort();
			if (port == -1) {
				System.out.println("Too many clients connected to the server at the moment, try again later");
				System.exit(0);
			}
			client.connectTCP(port);
			client.promptName();
			client.printCurrentUsers();
			System.out.println("To message someone, type \"message <user> <message>\". For help, type /help");
			client.clientMain();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
