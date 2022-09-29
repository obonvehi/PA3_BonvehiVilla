package ex02_RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuessGameObjectImpl extends UnicastRemoteObject implements GuessGameObject {

	private int countId = 0;
	private ConcurrentHashMap<Integer, ClientRep> clientMap = new ConcurrentHashMap<Integer, ClientRep>();

	protected GuessGameObjectImpl() throws RemoteException {
	}

	// launcher
	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.createRegistry(1999);
			registry.bind("GUESS", new GuessGameObjectImpl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Guess service bound and running");
	}

	public synchronized int startGame() throws RemoteException {
		countId++;
		ClientRep clientRep = new ClientRep();
		clientRep.theNumber = new Random().nextInt(999) + 1;
		clientMap.put(countId, clientRep);
		return countId;
	}

	public String check(int id, int number) throws RemoteException {
		/* Throws an exception if the id is unknown */
		if (!clientMap.containsKey(id))
			throw new RemoteException("Id is unknown");
		ClientRep clientRep = clientMap.get(id);
		/* if invoked when the number has been guessed in the last attempt but reset has not been invoked, throws an exception. */
		if (clientRep.justGuessed)
			throw new RemoteException("The number has been guessed in the last attempt but reset has not been invoked");
		clientRep.attempts++;
		if (number == clientRep.theNumber) {
			clientRep.justGuessed = true;
			clientRep.guessed++;
			return "EQUAL";
		} else if (number > clientRep.theNumber) {
			return "LOWER";
		} else {
			return "HIGHER";
		}
	}

	public String reset(int id) throws RemoteException {
		/* Throws an exception if the id is unknown */
		if (!clientMap.containsKey(id))
			throw new RemoteException("Id is unknown");
		ClientRep clientRep = clientMap.get(id);
		clientRep.theNumber = new Random().nextInt(999) + 1;
		clientRep.justGuessed = false;
		return "RESET_OK";
	}

	public String terminate(int id) throws RemoteException {
		ClientRep clientRep = clientMap.get(id);
		clientMap.remove(id);
		return ("GOODBYE. " + "You made " + clientRep.attempts + " guesses and got " + clientRep.guessed
				+ " numbers right");
	}
}

// utility class to represent clients (stores all relevant info regarding a client)
class ClientRep {
	boolean justGuessed = false;
	int theNumber;
	int attempts = 0;
	int guessed = 0;
}
