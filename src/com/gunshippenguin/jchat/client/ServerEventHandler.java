package com.gunshippenguin.jchat.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.logging.Level;

import com.gunshippenguin.jchat.shared.Event;
import java.util.logging.Logger;

/**
 * Runnable class that handles incoming events from the server. Will receive
 * incoming events and call their handle method.
 * 
 * @author GunshipPenguin
 *
 */
public class ServerEventHandler implements Runnable {
	/**
	 * The Connection object associated with this ServerEventHandler
	 */
	private Connection conn;
	/**
	 * The ObjectInputStream to listen for incoming events on.
	 */
	private ObjectInputStream ois;
	/**
	 * Logger object for ServerEventHandler.
	 */
	private static final Logger logger = Logger.getLogger(ServerEventHandler.class.getName());

	/**
	 * Sets up this ServerEventHandler to listen for incoming events on the
	 * ObjectInputStream ois, and to treat all events as if they are coming from
	 * the server associated with the Connection conn.
	 * 
	 * @param conn
	 *            - The connection to be associated with this
	 *            ServerEventHandler.
	 * @param ois
	 *            - The ObjectInputStream to listen for incoming events on.
	 */
	public ServerEventHandler(Connection conn, ObjectInputStream ois) {
		this.conn = conn;
		this.ois = ois;
	}

	/**
	 * Listens for incoming events on ois and calls their handle method. If an
	 * exception is caught when trying to read from ois, disconnects from the
	 * server.
	 */
	@Override
	public void run() {
		Event incomingEvent = null;
		do {
			try {
				incomingEvent = (Event) ois.readObject();
				logger.log(Level.FINER, "Received event " + incomingEvent.toString());
				incomingEvent.handle(conn);
			} catch (EOFException e) {
				logger.log(Level.INFO, "Server " + conn.getConnectionInfo() + " has shut down");
				conn.getUi().showServerUiMessage("Server is shutting down");
			} catch (SocketException s) {
				logger.log(Level.INFO, "Disconnected from " + conn.getConnectionInfo());
				return;
			} catch (ClassNotFoundException | IOException e) {
				logger.log(Level.SEVERE, "Something went wrong when trying to receive an event from the server");
				e.printStackTrace();
			}
		} while (incomingEvent != null);
	}
}