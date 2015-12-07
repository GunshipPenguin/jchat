package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.client.ChatRoomManager;
import com.gunshippenguin.jchat.client.Connection;

/**
 * Event to inform a client that a new client has joined a chat room.
 * 
 * @author GunshipPenguin
 *
 */
public class JoinChatRoomEvent implements Event {
	/**
	 * ClientInfo object representing the joining player
	 */
	private ClientInfo clientInfo;
	/**
	 * Name of the chat room that the JoinChatRoomEvent pertains to.
	 */
	private String chatRoomName;

	/**
	 * Creates a new JoinChatRoomEvent with clientInfo being a ClientInfo object
	 * representing the joining player and chatRoomName being the name of the
	 * chat room being joined.
	 * 
	 * @param clientInfo
	 *            A ClientInfo object representing the joining player
	 * @param chatRoomName
	 *            The name of the chat room being joined
	 */
	public JoinChatRoomEvent(ClientInfo clientInfo, String chatRoomName) {
		this.clientInfo = clientInfo;
		this.chatRoomName = chatRoomName;
	}

	/**
	 * Updates the ChatRoomManager for the chat room receiving the new client to
	 * include the new client.
	 */
	@Override
	public void handle(Connection conn) {
		ChatRoomManager chatRoomManager = conn.getChatRoomManagerByName(chatRoomName);
		chatRoomManager.addClient(clientInfo);
		chatRoomManager.addServerMessage("Client " + clientInfo.getNick() + " has joined");
		return;
	}

}
