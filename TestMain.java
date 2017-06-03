package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TestMain {

	public static void main(String[] args) {
		try {
			
			//thread save queue
			//thread safe linked list
			
			DatagramSocket sock = new DatagramSocket(new InetSocketAddress("localhost", 8000));
			DatagramPacket packet = new DatagramPacket(new byte[10], 10, new InetSocketAddress("localhost", 8000));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			while(true) {
				String data = stdIn.readLine();
				sock.send(new DatagramPacket(data.getBytes(), data.length(), new InetSocketAddress("localhost", 8000)));
			}
			//sock.receive(packet);
			//System.out.println(Arrays.toString(packet.getData()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//UDPMessage message = new 
	}
}
