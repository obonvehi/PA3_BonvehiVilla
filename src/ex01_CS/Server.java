package ex01_CS;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Server extends Thread {

	private Socket connection;
	private BufferedReader inputChannel;
	private PrintWriter outputChannel;
	private int value, guesses=0, gessed=0;

	/* MAIN IS THE LAUNCHER */
	public static void main(String[] args) throws IOException {
		
		Socket connection;
		ServerSocket serverSocket = new ServerSocket(6666);
		System.out.println("Server running and listening to port 6666");
		
		while(true) {
			connection = serverSocket.accept();
			new Server(connection).start();
		}
	}
	public Server(Socket connection) { this.connection = connection; }
	
	private void disconnect() throws IOException {
		this.connection.close();
		this.inputChannel.close();
		this.outputChannel.close();
	}
	private void createChannels() throws IOException{
		this.inputChannel = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		this.outputChannel = new PrintWriter(connection.getOutputStream(), true);
	}
	
	private void reply(String message) { this.outputChannel.println(message);this.outputChannel.flush(); }
	
	private String recive() throws IOException { return this.inputChannel.readLine(); }
	
	public void run() {
		try { innerRun(); } catch (IOException ioex) { ioex.printStackTrace(System.err); }
	}
	
	public void innerRun() throws IOException {
		createChannels();
		Boolean terminate = false;
		while(!terminate) {
			Request request = new Request(recive());
			switch (request.type) {
				case CHECK: reply(check(request.value));
				break;
				case RESET: reply(reset());
				break;
				case TERMINATE: reply(terminate()); terminate=true;
				break;
				case UNKNOWN: reply("Unknown message");
				break;
			}
		}
		disconnect();
	}
	
	private String terminate() {
		return ("GOODBYE. "+"You made "+guesses +" guesses and got "+gessed +" numbers right");
	}
	
	private String reset() {
		this.value=new Random().nextInt(999)+1; //Comprobar el rang de valors 1 a 999
		return "RESET_OK";
	}
	
	private String check(int value) {
		guesses++;
		if (this.value == value) {
			gessed++;
			return "EQUAL";
		} else if (this.value < value) {
			return "LOWER";
		} else {
			return "HIGHER";
		}
	}
}

// utility class. Makes requests out of strings
class Request {

	public enum Type {
		CHECK, RESET, TERMINATE, UNKNOWN
	};

	public int value;
	public Type type;
	public String message;

	// make a request object out of a message...
	public Request(String message) {
		this.message = message;
		String[] elements = message.split(" ");
		if (elements[0].equalsIgnoreCase("check")) {
			try {
				this.value = Integer.parseInt(elements[1]);
				this.type = Type.CHECK;
				return;
			} catch (Exception ex) {
				this.type = Type.UNKNOWN;
				return;
			}
		}
		if (elements[0].equalsIgnoreCase("reset")) {
			this.type = Type.RESET;
			return;
		}
		if (elements[0].equalsIgnoreCase("terminate")) {
			this.type = Type.TERMINATE;
			return;
		}
		this.type = Type.UNKNOWN;
	}
}
