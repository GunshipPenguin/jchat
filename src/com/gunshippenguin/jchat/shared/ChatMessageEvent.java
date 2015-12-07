package com.gunshippenguin.jchat.shared;

import com.gunshippenguin.jchat.client.ChatRoomManager;
import com.gunshippenguin.jchat.client.Connection;

/**
 * Event to inform a client of a new ChatMessage being sent.
 * 
 * @author GunshipPenguin
 */
public class ChatMessageEvent implements Event {
	/**
	 * String representing the chat message being sent
	 */
	private String chatMessage;
	/**
	 * String representing the nickname of the sending player.
	 */
	private String sendingPlayer;
	/**
	 * String representing the name of the chatRoom that the chat message is
	 * being sent of.
	 */
	private String chatRoomName;

	/**
	 * Constructs a new ChatMessageEvent.
	 * 
	 * @param chatMessage
	 *            The chat message being sent in String form
	 * @param sendingPlayer
	 *            The nickname of the sending player as a string
	 * @param chatRoomName
	 *            The name of the chat room that the chat message is being sent
	 *            on
	 */
	public ChatMessageEvent(String chatMessage, String sendingPlayer, String chatRoomName) {
		this.chatMessage = chatMessage;
		this.sendingPlayer = sendingPlayer;
		this.chatRoomName = chatRoomName;
	}

	/**
	 * Sends the chat message chatMessage on the chat room with name
	 * chatRoomName.
	 * 
	 * @param conn
	 *            The connection that the chat room with name chatRoomName will
	 *            be searched for.
	 */
	@Override
	public void handle(Connection conn) {
		ChatRoomManager chatRoom = conn.getChatRoomManagerByName(chatRoomName);
		chatRoom.addChatMessage(chatRoom.getClientInfoByNick(sendingPlayer), chatMessage);
		return;
	}
}
