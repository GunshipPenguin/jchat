package com.gunshippenguin.jchat.shared;

import java.io.Serializable;

import com.gunshippenguin.jchat.server.ChatServer;

/**
 * The Request interface should be implemented by any class that is a piece of
 * information sent from server to client in order to inform the server about
 * some sort of request being made by the client. Upon being received by the
 * client, the handle method will be called to perform whatever action is
 * necessary.
 * 
 * @author GunshipPenguin
 */
public interface Request extends Serializable {
	/**
	 * Method that will be called by the Client class upon the server receiving
	 * this Request. The ChatServer will be passed in as chatServer, and the
	 * ClientInfo object representing the client making the request will be
	 * passed in as clientInfo.
	 * 
	 * @param chatServer
	 *            The ChatServer the request is being made to
	 * @param clientInfo
	 *            The clientInfo object representing the object
	 */
	public void handle(ChatServer chatServer, ClientInfo clientInfo);
}
