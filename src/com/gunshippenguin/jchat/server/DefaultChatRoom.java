package com.gunshippenguin.jchat.server;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.gunshippenguin.jchat.shared.ChatRoomInfo;
import com.gunshippenguin.jchat.shared.JoinChatRoomEvent;

/**
 * Class representing a default chat room. Contains methods to obtain
 * information about the default chat room and interface with its connected
 * clients.
 * 
 * A DefaultChatRoom is a special type of ChatRoom which all servers have
 * exactly one of and which all clients connected to the server are always in.
 * It can be used as a central directory of all clients connected to the server,
 * and can also be used to send events to all clients connected to the server.
 * Removing a client from the DefaultChatRoom is equivalent to removing him from
 * the server.
 * 
 * @author GunshipPenguin
 */
public class DefaultChatRoom extends ChatRoom {
	/**
	 * The ChatServer object associated with this DefaultChatRoom.
	 */
	ChatServer server;
	/**
	 * The Logger object to be used by the DefaultChatRoom class.
	 */
	private static final Logger logger = Logger.getLogger(DefaultChatRoom.class.getName());

	/**
	 * Creates a new DefaultChatRoom object for the ChatServer server.
	 * 
	 * @param server
	 *            The ChatServer object associated with this DefaultChatRoom
	 */
	public DefaultChatRoom(ChatServer server) {
		super("default");
		this.server = server;
		chatRoomInfo.setDefault(true);
	}

	/**
	 * Adds a client to the DefaultChatRoom. All clients are added to a
	 * ChatServer's DefaultChatRoom upon joining it.
	 * 
	 * @param client
	 *            The Client object representing the client to be added to the
	 *            DefaultChatRoom
	 */
	@Override
	public synchronized void addClient(Client client) {
		sendEventToAllClients(new JoinChatRoomEvent(client.getClientInfo(), chatRoomInfo.getName()));
		clients.add(client);
		chatRoomInfo.addClient(client.getClientInfo());
		logger.log(Level.INFO, "Client " + client.getClientInfo().getNick() + " added to default chat room");
		return;
	}

	/**
	 * Removes a client from the DefaultChatRoom and all other ChatRooms on the
	 * ChatServer chatServer.
	 *
	 * @param nick
	 *            The nickname of the client to remove
	 */
	@Override
	public synchronized void removeClient(String nick) {
		/* Get the client object to remove */
		Client clientToRemove = getClientByNick(nick);

		/* Remove the client from the defaultChatRoom */
		super.removeClient(nick);

		/* Remove this client from all other chat rooms that it is in */
		ArrayList<ChatRoomInfo> chatRoomList = server.getChatRoomList();
		for (ChatRoomInfo cri : chatRoomList) {
			if (cri.hasClient(clientToRemove.getClientInfo().getNick()) && !cri.isDefaultChatRoom()) {
				server.getChatRoomByName(cri.getName()).removeClient(nick);
			}
		}
		logger.log(Level.INFO, "Client " + nick + " removed from default chat room");
	}
}
