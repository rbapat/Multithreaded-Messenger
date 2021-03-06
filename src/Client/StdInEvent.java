package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Observable;

/*
 * Event Handler that runs as its own thread and notifies observers when it recieved input from STDIN
 * 
 * @author Rohan Bapat
 * @version 1.0
 * @since 06-15-17
 */

public class StdInEvent extends Observable implements Runnable {
	BufferedReader stdIn;
	private boolean isAlive;
	
	/*
	 * instantiate input stream to STDIN
	 *
	 * @param stdIn
	 *	BufferedReader to STDIN
	 *
	 */
	
	public StdInEvent(BufferedReader stdIn) {
		this.stdIn = stdIn;
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
				if(stdIn.ready()) {
					String input = stdIn.readLine();
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
