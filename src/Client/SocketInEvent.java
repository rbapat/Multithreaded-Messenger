package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Observable;

public class SocketInEvent extends Observable implements Runnable {

	BufferedReader socketIn;
	private boolean isAlive;

	public SocketInEvent(BufferedReader socketIn) {
		this.socketIn = socketIn;
		isAlive = true;
	}

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
	
	public void kill() {
		isAlive = false;
	}
}
