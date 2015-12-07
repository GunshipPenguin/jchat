package com.gunshippenguin.jchat.shared;

import java.io.Serializable;

/**
 * Class representing the initial information sent from server to client. A
 * serialized InitServerInfo object will be sent to the client by a
 * ClientAccepter once it has received a serialized instance of InitClientInfo
 * from the client. Contains all information that must be initially sent from
 * server to client (eg. default chat room information).
 * 
 * @author GunshipPenguin
 */
public class InitServerInfo implements Serializable {
	/**
	 * ChatRoomInfo object representing the default chat room for the server.
	 */
	private ChatRoomInfo defaultChatRoom;
	/**
	 * ClientInfo object representing the connecting client.
	 */
	private ClientInfo selfClient;

	/**
	 * Bit flag for a bad nickname. Will be set if the nickname requested by the
	 * client is already taken or otherwise unacceptable.
	 */
	public static final int BAD_NICK = 1 << 0;

	/**
	 * Bit field containing flags that can be set to inform the client of
	 * various information.
	 */
	private int flags = 0;

	/**
	 * Creates a new InitServerInfo object with selfClient being the ClientInfo
	 * object representing the connecting client and defaultChatRoom being the
	 * ChatRoomInfo object representing the defaultChatRoom.
	 * 
	 * @param selfClient
	 *            The ClientInfo object representing the connecting client
	 * @param defaultChatRoom
	 *            the ChatRoomInfo object representing the server's default chat
	 *            room
	 */
	public InitServerInfo(ClientInfo selfClient, ChatRoomInfo defaultChatRoom) {
		this.defaultChatRoom = defaultChatRoom;
		this.selfClient = selfClient;
	}

	/**
	 * Gets the ChatRoomInfo object representing the defaultChatRoom.
	 * 
	 * @return The ChatRoomInfo object representing the defaultChatRoom
	 */
	public ChatRoomInfo getDefaultChatRoom() {
		return defaultChatRoom;
	}

	/**
	 * Returns the ClientInfo object representing the connecting client.
	 * 
	 * @return The ClientInfo object representing the connecting client
	 */
	public ClientInfo getSelfClient() {
		return selfClient;
	}

	/**
	 * Sets nickname of the selfClient attribute
	 * 
	 * @param nick
	 *            The nickname to set in the selfClient attribute
	 */
	public void setNick(String nick) {
		selfClient.setNick(nick);
		return;
	}

	/**
	 * Returns the bit field containing all flags for this InitServerInfo as an
	 * integer.
	 * 
	 * @return The bit field containing all flags for this InitServerInfo
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * Sets the flag flagToSet in this InitServerInfo. The flag will then appear
	 * in the bit field returned by getFlags.
	 * 
	 * @param flagToSet
	 *            The flag to set in the InitServerInfo
	 */
	public void setFlag(int flagToSet) {
		flags |= flagToSet;
		return;
	}
}
