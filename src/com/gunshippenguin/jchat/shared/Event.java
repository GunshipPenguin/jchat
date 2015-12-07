package com.gunshippenguin.jchat.shared;

import java.io.Serializable;
import com.gunshippenguin.jchat.client.Connection;

/**
 * The Event interface should be implemented by any class that is a piece of
 * information sent from server to client in order to inform the client of some
 * sort of event. Upon being received by the client, the handle method will be
 * called by the client to perform whatever action is necessary.
 * 
 * @author GunshipPenguin
 */
public interface Event extends Serializable {
	/**
	 * Method that will be called by the ServerEventHandler class upon this
	 * event being received by the client. The Connection representing the
	 * server that the event originated on will be passed in as conn.
	 * 
	 * @param conn
	 *            The Connection representing the server that the event
	 *            originated on
	 */
	public abstract void handle(Connection conn);
}
