package com.gunshippenguin.jchat.client;

import javafx.collections.FXCollections;

import com.gunshippenguin.jchat.shared.ChatRoomInfo;
import com.gunshippenguin.jchat.shared.ClientInfo;

import javafx.application.Platform;
import javafx.collections.ObservableList;

/**
 * Object representing a chat room on the client side. Contains methods to get
 * information about the chat room represented and interface with it.
 * 
 * @author GunshipPenguin
 */
public class ChatRoomManager {
	/**
	 * Connection object associated with this ChatRoomManager
	 */
	private Connection conn;
	/**
	 * Name of the chat room that this ChatRoomManager represents.
	 */
	private String name;
	/**
	 * ObservableList of all chat messages that have been sent since the client
	 * joined this chat room. This will be displayed in the UI.
	 */
	private ObservableList<String> messages;
	/**
	 * ObservableList of ClientInfo objects representing all the users connected
	 * to the chat room. This will be displayed in the UI.
	 */
	private ObservableList<ClientInfo> clients;

	public ChatRoomManager(ChatRoomInfo chatRoomInfo, Connection conn) {
		messages = FXCollections.observableArrayList();
		clients = FXCollections.observableArrayList(chatRoomInfo.getClients());
		name = chatRoomInfo.getName();
		this.conn = conn;
	}

	/**
	 * Adds the chat message message to the chatRoom. The message will appear as
	 * being sent by the client represented by sender.
	 * 
	 * @param sender
	 *            ClientInfo object representing the sender of the message
	 * @param message
	 *            The message in string format
	 */
	public void addChatMessage(ClientInfo sender, String message) {
		String messageToShow = "<" + sender.getNick() + ">" + " " + message;
		addChatString(messageToShow);
		return;
	}

	/**
	 * Returns the Connection object associated with this ChatRoomManager.
	 * 
	 * @return The Connection object associated with this ChatRoomManager
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * Adds the message message to the chat room. The message will appear as if
	 * it is being sent by the server. Useful to show information about the chat
	 * room and other messages.
	 * 
	 * @param message
	 *            The message to add
	 */
	public synchronized void addServerMessage(String message) {
		addChatString("#SERVER: " + message);
		return;
	}

	/**
	 * Adds the string str to the list of chat messages.
	 * 
	 * @param str
	 *            The String to add to the list of chat messages
	 */
	public synchronized void addChatString(final String str) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				messages.add(str);
				return;
			}

		});
	}

	/**
	 * Returns the ClientInfo object for the client in the chat room with
	 * nickname nick.
	 * 
	 * @param nick
	 *            Nickname of the client whose ClientInfo object to return.
	 * @return The ClientInfo object for the client in the chat room with
	 *         nickname nick
	 */
	public ClientInfo getClientInfoByNick(String nick) {
		for (ClientInfo clientInfo : clients) {
			if (clientInfo.getNick().equals(nick)) {
				return clientInfo;
			}
		}
		throw new RuntimeException("Client with nick of " + nick + " not found");
	}

	/**
	 * Adds the client represented by client to this ChatRoomManager.
	 * 
	 * @param client
	 *            The client to add to the ChatRoomManager
	 */
	public synchronized void addClient(final ClientInfo client) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				clients.add(client);
			}
		});
		return;
	}

	/**
	 * Removes the client with the nickname nick from this ChatRoomManager.
	 * 
	 * @param nick
	 *            Nickname of client to remove
	 */
	public synchronized void removeClient(String nick) {
		for (final ClientInfo ci : clients) {
			if (ci.getNick().equals(nick)) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						clients.remove(ci);
					}
				});
				break;
			}
		}
		return;
	}

	/**
	 * Returns the list of chat messages for this ChatRoomManager.
	 * 
	 * @return The list of chat messages for this ChatRoomManager
	 */
	public ObservableList<String> getMessages() {
		return messages;
	}

	/**
	 * Returns the list of clients for this ChatRoomManager as a list of
	 * ClientInfo objects.
	 * 
	 * @return The list of clients for this ChatRoomManager
	 */
	public ObservableList<ClientInfo> getClients() {
		return clients;
	}

	/**
	 * Returns the name of the chat room represented by this ChatRoomManager.
	 * 
	 * @return The name of the chat room represented by this ChatRoomManager
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this ChatRoomManager.
	 * 
	 * @param name
	 *            New name for the ChatRoomManager
	 */
	public void setName(String name) {
		this.name = name;
		return;
	}

	/**
	 * Returns true if this ChatRoomManager represents a default chat room,
	 * false otherwise.
	 * 
	 * @return True if this ChatRoomManager represents a default chat room,
	 *         false otherwise
	 */
	public boolean isDefaultChatRoom() {
		if (conn.getDefaultChatRoom().equals(this)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns a string representation of the chat room represented by this
	 * ChatRoomManager.
	 * 
	 * @return A string representation of the chat room represented by this
	 *         ChatRoomManager
	 */
	@Override
	public String toString() {
		if (isDefaultChatRoom()) {
			return conn.getConnectionInfo();
		} else {
			return name;
		}
	}
}
