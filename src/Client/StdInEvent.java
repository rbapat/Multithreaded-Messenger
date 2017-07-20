package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Observable;

public class StdInEvent extends Observable implements Runnable {
	BufferedReader stdIn;
	private boolean isAlive;
	
	public StdInEvent(BufferedReader stdIn) {
		this.stdIn = stdIn;
		isAlive = true;
	}

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
	
	public void kill() {
		isAlive = false;
	}
}
