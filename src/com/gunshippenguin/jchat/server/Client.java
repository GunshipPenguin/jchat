package com.gunshippenguin.jchat.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.gunshippenguin.jchat.shared.ClientInfo;
import com.gunshippenguin.jchat.shared.Event;
import com.gunshippenguin.jchat.shared.Request;

/**
 * Class representing a client.
 * 
 * This class implements the Runnable interface so that it can handle incoming
 * requests from the client in a separate thread.
 * 
 * @author GunshipPenguin
 *
 */
public class Client implements Runnable {

	/**
	 * ClientInfo object representing this Client
	 */
	private ClientInfo clientInfo;
	/**
	 * Socket object where Requests from the client will be received and where
	 * Events from the server will be sent
	 */
	private Socket socket;
	/**
	 * ObjectInputStream connected to socket. Used to receive Requests from the
	 * client.
	 */
	private ObjectInputStream in;
	/**
	 * ObjectOutput stream connected to socket. Used to sent Events to the
	 * client.
	 */
	private ObjectOutputStream out;
	/**
	 * ChatServer object to be passed into the handle method of incoming
	 * Requests.
	 */
	private ChatServer chatServer;
	/**
	 * Logger object to be used by the Client class.
	 */
	private static final Logger logger = Logger.getLogger(Client.class.getName());

	/**
	 * Creates a new Client object.
	 * 
	 * @param out
	 *            ObjectOutputStream that will be used to send events to the
	 *            client
	 * @param in
	 *            The ObjectInputStream that will be used to receive Requests
	 *            from the client
	 * @param socket
	 *            The Socket object where Requests from the client will be
	 *            received and where Events from the server will be sent
	 * @param clientInfo
	 *            ClientInfo object representing this client
	 * @param chatServer
	 *            ChatServer object representing the chat server
	 */
	public Client(ObjectOutputStream out, ObjectInputStream in, Socket socket, ClientInfo clientInfo,
			ChatServer chatServer) {
		this.clientInfo = clientInfo;
		this.socket = socket;
		this.chatServer = chatServer;
		this.out = out;
		this.in = in;
	}

	/**
	 * Sends an event to the client.
	 * 
	 * @param evnt
	 *            The event to send.
	 */
	public void sendEvent(Event evnt) {
		logger.log(Level.FINER, "Sending event " + evnt.toString() + " to client " + clientInfo.getNick());
		try {
			out.reset();
			out.writeObject(evnt);
		} catch (IOException e) {
			logger.log(Level.WARNING,
					"Could not send event - " + evnt.toString() + " to client " + clientInfo.getNick(), e);
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Closes the socket object for this Client.
	 */
	public void stopListening() {
		try {
			socket.close();
			logger.log(Level.FINER, "Stopped listening for requests from " + clientInfo.getNick());
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "Could not stop listening for requests from " + clientInfo.getNick());
		}
		return;
	}

	/**
	 * Receives incoming Requests from the client and calls their handle method.
	 */
	@Override
	public void run() {
		logger.log(Level.FINER, "Starting to listen for requests from" + clientInfo.getNick());
		Request newRequest = null;
		do {
			try {
				newRequest = (Request) in.readObject();
				logger.log(Level.FINER,
						"Got request " + newRequest.toString() + " from client " + clientInfo.getNick());
				newRequest.handle(chatServer, clientInfo);
			} catch (EOFException e) {
				logger.log(Level.INFO, "Client " + clientInfo.getNick() + " disconnected");
				chatServer.getDefaultChatRoom().removeClient(clientInfo.getNick());
				stopListening();
				return;
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
				return;
			}
		} while (newRequest != null);
	}

	/**
	 * Returns a ClientInfo object representing this Client.
	 * 
	 * @return A ClientInfo object representing this Client
	 */
	public ClientInfo getClientInfo() {
		return clientInfo;
	}
}
