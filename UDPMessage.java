package src;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class UDPMessage {
	
	private DatagramPacket packet;
	
	public UDPMessage(String message) {
		packet = new DatagramPacket(message.getBytes(), message.length(), new InetSocketAddress("localhost",8000));
	}
	
	public UDPMessage() {
		packet = new DatagramPacket(new byte[100], 100, new InetSocketAddress("localhost",8000));
	}
	
	public String getMessage() {
		byte[] bytes = packet.getData();
		String ret = "";
		for(int x = 0; x < bytes.length; x++)
			ret += Character.toString((char)bytes[x]);
		return ret;
	}
	
	public int getPort() {
		return packet.getPort();
	}
	
	public String getAddress() {
		return packet.getAddress().getHostAddress();
	}
	
	public void setMessage(String message) {
		packet.setData(message.getBytes());
	}
	
	public DatagramPacket getPacket() {
		return packet;
	}
	
	
}
