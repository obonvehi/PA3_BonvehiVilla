package ex02_RMI;

import java.rmi.*;

public interface GuessGameObject extends Remote {
	
	public int startGame () throws RemoteException;
	// starts a new game. The result is the ID that the server has
	// assigned to the client. This id will have to be supplied in 
	// future interactions with the server.
	// At the server side a new random number is created
	
	public String check (int id, int number) throws RemoteException;
	// checks the number. The result is HIGHER, LOWER or EQUAL
	// if invoked when the number has been guessed in the last
	// attempt but reset has not been invoked, throws an exception.
	// Throws an exception if the id is unknown
	
	public String reset (int id) throws RemoteException;
	// requests the server to make up another random number
	// Throws an exception if the id is unknown
	
	public String terminate (int id) throws RemoteException;
	// informs the server that no further interaction will take place. 
	// Returns a string containing the number of numbers correctly
	// guessed and the global number of attempts made
	// Throws an exception if the id is unknown

}
