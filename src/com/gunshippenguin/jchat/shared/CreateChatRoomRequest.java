package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.server.ChatRoom;
import com.gunshippenguin.jchat.server.ChatServer;

/**
 * A Request to create a new chat room.
 * 
 * @author GunshipPenguin
 */
public class CreateChatRoomRequest implements Request {
	/**
	 * Name of the new chat room to create
	 */
	String name;

	/**
	 * Creates a new CreateChatRoomRequest to request that a chat room with a
	 * name of name be created.
	 * 
	 * @param name
	 *            The name of the chat room to create.
	 */
	public CreateChatRoomRequest(String name) {
		this.name = name;
	}

	/**
	 * Creates a new ChatRoom with its name set to the value of the name
	 * attribute of this CreateChatRoomRequest if no chat room already exists
	 * with a name of the value of the name attribute of this
	 * CreateChatRoomRequest.
	 */
	@Override
	public void handle(ChatServer chatServer, ClientInfo clientInfo) {
		if (!chatServer.chatRoomExists(name)) {
			ChatRoom cr = new ChatRoom(name);
			chatServer.addChatRoom(cr);
		}
		return;
	}
}
