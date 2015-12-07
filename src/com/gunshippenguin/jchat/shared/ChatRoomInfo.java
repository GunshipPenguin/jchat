package com.gunshippenguin.jchat.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class representing information about a particular chat room. Contains methods
 * to query and modify the information. Primarily used to send information about
 * chat rooms over a network.
 * 
 * @author GunshipPenguin
 */
public class ChatRoomInfo implements Serializable {
	/**
	 * Name of the chat room.
	 */
	private String name;
	/**
	 * ArrayList containing one ClientInfo object for each client connected to
	 * the chat room.
	 */
	private ArrayList<ClientInfo> clients;
	/**
	 * True if this is a default chat room, false otherwise.
	 */
	private boolean isDefault = false;

	/**
	 * Constructs a new ChatRoomInfo object
	 * 
	 * @param name
	 *            The name of the chat room
	 */
	public ChatRoomInfo(String name) {
		this.name = name;
		clients = new ArrayList<ClientInfo>();
	}

	/**
	 * Sets the value the isDefault attribute for this ChatRoomInfo to val.
	 * 
	 * @param val
	 *            The value to set the isDefault attribute to for this
	 *            ChatRoomInfo
	 */
	public void setDefault(boolean val) {
		isDefault = val;
		return;
	}

	/**
	 * Returns the value of the isDefault attribute for this ChatRoomInfo
	 * object.
	 * 
	 * @return The value of the isDefault attribute for this ChatRoomInfo object
	 */
	public boolean isDefaultChatRoom() {
		return isDefault;
	}

	/**
	 * Adds the clientInfo object newClient to the clients ArrayList.
	 * 
	 * @param newClient
	 *            The ClientInfo object to add to the clients ArrayList
	 */
	public synchronized void addClient(ClientInfo newClient) {
		clients.add(newClient);
		return;
	}

	/**
	 * Removes the client with nickname nick from the clients ArrayList. Throws
	 * a RuntimeException if no client exists in the clients ArrayList with the
	 * nickname nick.
	 * 
	 * @param nick
	 *            Nickname of client whose ClientInfo object is to be removed
	 *            from the clients ArrayList
	 */
	public synchronized void removeClient(String nick) {
		Iterator<ClientInfo> it = clients.iterator();
		while (it.hasNext()) {
			ClientInfo currClient = it.next();
			if (currClient.getNick().equals(nick)) {
				clients.remove(currClient);
				return;
			}
		}
		throw new RuntimeException(
				"Trying to remove client with a nick of " + nick + " from ClientInfo, but the client does not exist");
	}

	/**
	 * Returns true if a client with the nickname nick is in the clients
	 * ArrayList, returns false otherwise.
	 * 
	 * @param nick
	 *            The nickname of the client whose presence in the clients
	 *            ArrayList should cause true to be returned.
	 * @return True if a client with the nickname nick appears in the clients
	 *         ArrayList, false otherwise.
	 */
	public boolean hasClient(String nick) {
		for (ClientInfo ci : clients) {
			if (ci.getNick().equals(nick)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the name of the chat room as a string.
	 * 
	 * @return The name of the chat room as a string.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the chat room.
	 * 
	 * @param name
	 *            Value that the name of the chat room will be set to
	 */
	public synchronized void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns an ArrayList of ClientInfo objects corresponding to all clients
	 * in the chat room.
	 * 
	 * @return An ArrayList of ClientInfo objects corresponding to all clients
	 *         in the chat room
	 */
	public ArrayList<ClientInfo> getClients() {
		return clients;
	}
}
