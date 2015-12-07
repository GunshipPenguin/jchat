package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.client.ChatRoomManager;
import com.gunshippenguin.jchat.client.Connection;

/**
 * Event to inform a client that another client has left a chat room.
 * 
 * @author GunshipPenguin
 */
public class LeaveChatRoomEvent implements Event {
	/**
	 * Nickname of the leaving client.
	 */
	private String leavingClient;
	/**
	 * Name of the chat room that the client is leaving.
	 */
	private String chatRoomName;

	/**
	 * Creates a new LeaveChatRoom event for the client whose name is
	 * leavingClient and for the chatRoom with a name of chatRoomName.
	 * 
	 * @param leavingClient
	 *            Nickname of the client leaving.
	 * @param chatRoomName
	 *            Name of the chat room that the client is leaving
	 */
	public LeaveChatRoomEvent(String leavingClient, String chatRoomName) {
		this.leavingClient = leavingClient;
		this.chatRoomName = chatRoomName;
	}

	/**
	 * Removes the client with nickname leavingClient from the chatRoom
	 * chatRoomName.
	 */
	@Override
	public void handle(Connection conn) {
		ChatRoomManager crm = conn.getChatRoomManagerByName(chatRoomName);
		crm.addServerMessage("Client " + leavingClient + " has left");
		crm.removeClient(leavingClient);
		return;
	}
}
