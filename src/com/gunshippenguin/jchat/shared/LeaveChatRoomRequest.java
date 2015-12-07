package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.server.ChatRoom;
import com.gunshippenguin.jchat.server.ChatServer;
import com.gunshippenguin.jchat.server.Client;

/**
 * A Request to leave a ChatRoom.
 * 
 * @author GunshipPenguin
 */
public class LeaveChatRoomRequest implements Request {
	/**
	 * Name of the chat room that the client is requesting to leave.
	 */
	private String chatRoomName;

	/**
	 * Creates a new LeaveChatRoomRequest for the chat room with name
	 * chatRoomName.
	 * 
	 * @param chatRoomName
	 *            The name of the chat room to be left.
	 */
	public LeaveChatRoomRequest(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}

	/**
	 * Removes the requesting client from the chat room if the client is in the
	 * chat room to begin with.
	 */
	@Override
	public void handle(ChatServer chatServer, ClientInfo clientInfo) {
		ChatRoom cr = chatServer.getChatRoomByName(chatRoomName);
		Client c = chatServer.getDefaultChatRoom().getClientByNick(clientInfo.getNick());
		cr.removeClient(c.getClientInfo().getNick());
		return;
	}

}
