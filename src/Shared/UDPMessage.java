package Shared;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class UDPMessage {
	
	private DatagramPacket packet;
	
	public UDPMessage(String message, String addr, int port) {
		packet = new DatagramPacket(message.getBytes(), message.length(), new InetSocketAddress(addr,port));
	}
	
	public UDPMessage(String addr, int port) {
		packet = new DatagramPacket(new byte[100], 100, new InetSocketAddress(addr,port));
	}
	
	public String getMessage() {
		byte[] bytes = packet.getData();
		String ret = "";
		for(int x = 0; x < bytes.length; x++) {
			if(bytes[x] < 32 || bytes[x] > 127)
				continue;
			ret += Character.toString((char)bytes[x]);
		}
		return ret;
	}
	
	public void clearMessage() {
		packet.setData(new byte[100]);
	}
	
	public int getPort() {
		return packet.getPort();
	}
	
	public String getAddress() {
		return packet.getAddress().getHostAddress();
	}
	
	public void setMessage(String message) {
		clearMessage();
		packet.setData(message.getBytes());
	}
	
	public DatagramPacket getPacket() {
		return packet;
	}
	
	
}
