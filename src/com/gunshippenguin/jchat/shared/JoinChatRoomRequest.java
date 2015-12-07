package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.server.ChatRoom;
import com.gunshippenguin.jchat.server.ChatServer;
import com.gunshippenguin.jchat.server.Client;

/**
 * A Request to join a chat room.
 * 
 * @author GunshipPenguin
 */
public class JoinChatRoomRequest implements Request {
	/**
	 * Name of the chat room that the client is requesting to join
	 */
	private String chatRoomName;

	/**
	 * Creates a new JoinChatRoomRequqest with chatRoomName as the name of the
	 * chat room the client is requesting to join.
	 * 
	 * @param chatRoomName
	 *            The name of the chat room the client is requesting to join.
	 */
	public JoinChatRoomRequest(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}

	/**
	 * Adds the requesting client to the chat room that this request pertains to
	 * if the client is not already in it.
	 */
	@Override
	public void handle(ChatServer chatServer, ClientInfo clientInfo) {
		if (chatServer.chatRoomExists(chatRoomName)) {
			Client client = chatServer.getDefaultChatRoom().getClientByNick(clientInfo.getNick());
			ChatRoom roomToJoin = chatServer.getChatRoomByName(chatRoomName);
			if (!roomToJoin.hasClient(clientInfo.getNick())) {
				roomToJoin.addClient(client);
			}
		}
		return;
	}
}
