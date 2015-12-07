package com.gunshippenguin.jchat.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.gunshippenguin.jchat.shared.AcceptedToChatRoomEvent;
import com.gunshippenguin.jchat.shared.ChatRoomInfo;
import com.gunshippenguin.jchat.shared.Event;
import com.gunshippenguin.jchat.shared.JoinChatRoomEvent;
import com.gunshippenguin.jchat.shared.LeaveChatRoomEvent;

/**
 * Class representing a chat room on the server side. Contains methods to obtain
 * information about the chat room and interface with its connected clients.
 * 
 * @author GunshipPenguin
 *
 */
public class ChatRoom {
	protected ChatRoomInfo chatRoomInfo;
	protected ArrayList<Client> clients;
	private static final Logger logger = Logger.getLogger(ChatRoom.class.getName());

	public ChatRoom(String name) {
		clients = new ArrayList<Client>();
		this.chatRoomInfo = new ChatRoomInfo(name);
	}

	/**
	 * Sends the event evnt to all clients in this chat room.
	 * 
	 * @param evnt
	 *            The event to send
	 */
	public void sendEventToAllClients(Event evnt) {
		logger.log(Level.FINE,
				"Event " + evnt.toString() + " being sent to all clients in chat room " + chatRoomInfo.getName());
		for (Client c : clients) {
			c.sendEvent(evnt);
		}
		return;
	}

	/**
	 * Removes the client with nickname nick from the ChatRoom.
	 * 
	 * @param nick
	 *            The nickname of the client to remove
	 */
	public synchronized void removeClient(String nick) {
		logger.log(Level.INFO, "Client " + nick + " leaving chat room " + chatRoomInfo.getName());
		Iterator<Client> it = clients.iterator();
		Client clientToRemove = null;
		while (it.hasNext()) {
			clientToRemove = it.next();
			if (clientToRemove.getClientInfo().getNick().equals(nick)) {
				clients.remove(clientToRemove);
				break;
			}
		}
		if (clientToRemove != null) {
			chatRoomInfo.removeClient(nick);
			sendEventToAllClients(
					new LeaveChatRoomEvent(clientToRemove.getClientInfo().getNick(), chatRoomInfo.getName()));
		} else {
			throw new RuntimeException("Trying to remove client " + nick + " from chatRoom " + chatRoomInfo.getName()
					+ " but client does not exist");
		}
		return;
	}

	/**
	 * Adds the client client to the ChatRoom.
	 * 
	 * @param client
	 *            The client to add
	 */
	public synchronized void addClient(Client client) {
		logger.log(Level.INFO,
				"Client " + client.getClientInfo().getNick() + " joining chat room" + chatRoomInfo.getName());
		sendEventToAllClients(new JoinChatRoomEvent(client.getClientInfo(), chatRoomInfo.getName()));
		clients.add(client);
		chatRoomInfo.addClient(client.getClientInfo());
		client.sendEvent(new AcceptedToChatRoomEvent(chatRoomInfo));
		return;
	}

	/**
	 * Returns true if a client with nick clientNick is in this ChatRoom, false
	 * otherwise.
	 * 
	 * @param nick
	 *            The nickname of the client to search for.
	 * @return True if a client exists in this chatRoom with the nick
	 *         clientNick, false otherwise.
	 */
	public boolean hasClient(String nick) {
		for (Client c : clients) {
			if (c.getClientInfo().getNick().equals(nick)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If a client exists in this ChatRoom with the nickname nick, returns its
	 * Client object. If there is no client with the nick nick in this ChatRoom,
	 * throws a runtime exception.
	 * 
	 * @param nick
	 *            The nickname of the client whose Client object should be
	 *            returned
	 * @return The client object of the client with the nickname nick.
	 */
	public Client getClientByNick(String nick) {
		for (Client c : clients) {
			if (c.getClientInfo().getNick().equals(nick)) {
				return c;
			}
		}
		throw new RuntimeException("Client with nickname of " + nick + " could not be found");
	}

	/**
	 * Returns the ChatRoomInfo for this ChatRoom.
	 * 
	 * @return The ChatRoomInfo for this ChatRoom
	 */
	public ChatRoomInfo getChatRoomInfo() {
		return chatRoomInfo;
	}
}
