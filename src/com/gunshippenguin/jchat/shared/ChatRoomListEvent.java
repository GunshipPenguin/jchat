package com.gunshippenguin.jchat.shared;

import java.util.ArrayList;

import com.gunshippenguin.jchat.client.Connection;

import javafx.application.Platform;

/**
 * Event to inform a client of all chat rooms on the server. Sent to a client
 * upon receiving a GetChatRoomListEvent.
 * 
 * @author GunshipPenguin
 */
public class ChatRoomListEvent implements Event {
	/**
	 * ArrayList of ChatRoomInfo objects corresponding to all chat rooms on the
	 * server.
	 */
	private ArrayList<ChatRoomInfo> chatRoomList;

	/**
	 * Creates a new ChatRoomListEvent with chatRoomList as the list of
	 * ChatRoomInfo objects corresponding to each chat room on the server.
	 * 
	 * @param chatRoomList
	 *            A list of ChatRoomInfo objects corresponding to each chat room
	 *            on the server.
	 */
	public ChatRoomListEvent(ArrayList<ChatRoomInfo> chatRoomList) {
		this.chatRoomList = chatRoomList;
	}

	/**
	 * Displays the list of chat rooms on the server via a message in the
	 * client's default chat room.
	 */
	@Override
	public void handle(final Connection conn) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				conn.getUi().showServerUiMessage("Chat Room List:");
				for (ChatRoomInfo cri : chatRoomList) {
					if (!cri.isDefaultChatRoom()) {
						conn.getUi().showServerUiMessage("\t" + cri.getName());
					}
				}
			}
		});
		return;
	}
}
