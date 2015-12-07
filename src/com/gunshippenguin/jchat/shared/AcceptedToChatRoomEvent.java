package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.client.Connection;

/**
 * Event to inform a client that it has been accepted to a chat room that it
 * previously requested to join.
 * 
 * @author GunshipPenguin
 */
public class AcceptedToChatRoomEvent implements Event {
	/**
	 * ChatRoomInfo object for the chat room that the client has been accepted
	 * to.
	 */
	private ChatRoomInfo chatRoomInfo;

	/**
	 * Creates a new AcceptedToChatRoomEvent for the chat room represented by
	 * the ChatRoomInfo chatRoomInfo.
	 * 
	 * @param chatRoomInfo
	 *            ChatRoomInfo object for the chat room that has accepted the
	 *            client
	 */
	public AcceptedToChatRoomEvent(ChatRoomInfo chatRoomInfo) {
		this.chatRoomInfo = chatRoomInfo;
	}

	/**
	 * Adds the chat room represented by chatRoomInfo to the connection conn.
	 * 
	 * @param conn
	 *            The connection to add the accepted chat room to
	 */
	@Override
	public void handle(Connection conn) {
		conn.addChatRoom(chatRoomInfo);
		return;
	}
}
