package com.gunshippenguin.jchat.shared;

import java.io.Serializable;

/**
 * Class representing initial information sent from client to server upon
 * establishing a connection. A serialized InitClientInfo object will be sent to
 * the server by a Connection. Contains all information that must be initially
 * sent from server to client (eg. requested nickname).
 * 
 * @author GunshipPenguin
 */
public class InitClientInfo implements Serializable {
	/**
	 * The nickname requested by the client.
	 */
	private String nick;

	/**
	 * Creates a new InitClientInfo object with the nickname requested by the
	 * client set to nick.
	 * 
	 * @param nick
	 *            The nickname requested by the client
	 */
	public InitClientInfo(String nick) {
		this.nick = nick;
	}

	/**
	 * Returns the nickname requested by the client.
	 * 
	 * @return The nickname requested by the client.
	 */
	public String getNick() {
		return nick;
	}
}
