package com.gunshippenguin.jchat.shared;

import java.util.ArrayList;

import com.gunshippenguin.jchat.server.ChatServer;
import com.gunshippenguin.jchat.server.Client;

/**
 * A Request to get the list of chat rooms on the server.
 * 
 * @author GunshipPenguin
 */
public class GetChatRoomListRequest implements Request {

	/**
	 * Sends a ChatRoomListEvent to the requesting client.
	 */
	@Override
	public void handle(ChatServer chatServer, ClientInfo clientInfo) {
		Client requester = chatServer.getDefaultChatRoom().getClientByNick(clientInfo.getNick());
		requester.sendEvent(new ChatRoomListEvent(chatServer.getChatRoomList()));
		return;
	}
}
