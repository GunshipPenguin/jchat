package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.client.Connection;

/**
 * Event to inform a client about a chat room being created.
 * 
 * @author GunshipPenguin
 */
public class ChatRoomAddedEvent implements Event {
	/**
	 * Name of the chat room being created.
	 */
	String name;

	/**
	 * Creates a new ChatRoomAdded event for a chat room with name name.
	 * 
	 * @param name
	 *            The name of the newly created chat room
	 */
	public ChatRoomAddedEvent(String name) {
		this.name = name;
	}

	/**
	 * Informs the client of the newly created chat room.
	 */
	@Override
	public void handle(Connection conn) {
		conn.getDefaultChatRoom().addServerMessage("New Room Added - " + name);
		return;
	}

}
