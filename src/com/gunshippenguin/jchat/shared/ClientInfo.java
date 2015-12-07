package com.gunshippenguin.jchat.shared;

import java.io.Serializable;

/**
 * Class containing information about a specific client connected to a server.
 * Primarily used to send information about a client over the network.
 * 
 * @author GunshipPenguin
 */
public class ClientInfo implements Serializable {
	/**
	 * Nickname of the client.
	 */
	private String nick;

	/**
	 * Creates a new ClientInfo object with nick as the nickname of the
	 * represented client.
	 * 
	 * @param nick
	 *            The nickname of the client who this ClientInfo object
	 *            represents.
	 */
	public ClientInfo(String nick) {
		this.nick = nick;
	}

	/**
	 * Returns the nickname of the client who this ClientInfo object represents.
	 * 
	 * @return The nickname of the client who this ClientInfo object represents
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Sets the value of the nick attribute of this ClientInfo object.
	 * 
	 * @param nick
	 *            The value to set the nick attribute of this ClientInfo object
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * Returns the nick attribute of this ClientInfo object.
	 * 
	 * @return The nick attribute of this ClientInfo object
	 */
	@Override
	public String toString() {
		return nick;
	}
}