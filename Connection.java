package src;

import java.io.*;
import java.net.*;
public class Connection {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    
    public Connection(String address, int port) throws IOException {
        socket = new Socket(address, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(),true);
    }
    
    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(),true);
    }
    
    public void send(String msg) {
        writer.println(msg);
    }
    
    public String recv() throws IOException {
        return reader.readLine();
    }
    
    public boolean isConnected() {
        return socket.isConnected();
    }
    
    public String getAddress() {
        return socket.getInetAddress().toString();
    }
    
    public int getPort() {
        return socket.getLocalPort();
    }
}