package src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;
public class Server {
    
    public static void main(String[] args) {
    	System.out.println("Starting...");
        try {
        	ConcurrentLinkedDeque<Connection> con = new ConcurrentLinkedDeque<>();
        	DatagramSocket udpServer = new DatagramSocket(8000, InetAddress.getByName("localhost"));
        	ServerSocket tcpServer = new ServerSocket(8000, 5, InetAddress.getByName("localhost"));
        	UDPMessage message = new UDPMessage();
        	//Format: requeque
            while(true) {
               udpServer.receive(message.getPacket());
               System.out.println(message.getMessage());
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}