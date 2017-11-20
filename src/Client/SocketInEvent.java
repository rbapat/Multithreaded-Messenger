package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Observable;

/*
 * Event Handler that runs as its own thread and notifies observers when it recieved input from a given socket
 * 
 * @author Rohan Bapat
 * @version 1.0
 * @since 06-15-17
 */

public class SocketInEvent extends Observable implements Runnable {

	BufferedReader socketIn;
	private boolean isAlive;
	
	/*
	 * instantiate input stream to socket
	 *
	 * @param socketIn
	 *	BufferedReader to socket
	 *
	 */
	public SocketInEvent(BufferedReader socketIn) {
		this.socketIn = socketIn;
		isAlive = true;
	}
	
	/*
	 * main loop for thread
	 * notifies registered observers when it recieves input from a given socket
	 *
	 */
	public void run() {
		while (isAlive) {
			try {
				if (socketIn.ready()) {
					String input = socketIn.readLine();
					setChanged();
					notifyObservers(input);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * kills thread when called
	 */
	public void kill() {
		isAlive = false;
	}
}
