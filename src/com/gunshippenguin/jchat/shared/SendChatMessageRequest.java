package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.server.ChatRoom;
import com.gunshippenguin.jchat.server.ChatServer;

/**
 * A Request to send a chat message on a specific chat room.
 * 
 * @author GunshipPenguin
 */
public class SendChatMessageRequest implements Request {
	/**
	 * String representation of the chat message to be sent.
	 */
	private String chatMessage;
	/**
	 * Name of the chat room that the chat message is being sent on.
	 */
	private String chatRoomName;

	/**
	 * Creates a new SendChatMessage request for the message chatMessage and the
	 * chat room with name chatRoomName
	 * 
	 * @param chatMessage
	 *            String representation of the chat message to be sent
	 * @param chatRoomName
	 *            Name of the chat room that the message is being sent on
	 */
	public SendChatMessageRequest(String chatMessage, String chatRoomName) {
		this.chatRoomName = chatRoomName;
		this.chatMessage = chatMessage;
	}

	/**
	 * Returns the chat message of this SendChatMessageRequest as a String.
	 * 
	 * @return The chat message of this SendChatMessageRequest as a String
	 */
	public String getChatMessage() {
		return chatMessage;
	}

	/**
	 * Sends the chat message for this SendChatMessageRequest on the chat room
	 * for this SendChatMessageRequest.
	 */
	public void handle(ChatServer chatServer, ClientInfo clientInfo) {
		ChatRoom chatRoom = chatServer.getChatRoomByName(chatRoomName);
		ChatMessageEvent chatMessageEvent = new ChatMessageEvent(chatMessage, clientInfo.getNick(), chatRoomName);
		chatRoom.sendEventToAllClients(chatMessageEvent);
		return;
	}
}
