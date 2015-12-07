package com.gunshippenguin.jchat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gunshippenguin.jchat.shared.ClientInfo;
import com.gunshippenguin.jchat.shared.InitClientInfo;
import com.gunshippenguin.jchat.shared.InitServerInfo;

/**
 * Class that waits for incoming connections from clients and adds them to the
 * ChatServer.
 * 
 * @author GunshipPenguin
 */
public class ClientAccepter implements Runnable {
	/**
	 * ChatServer object associated with this ClientAccepter.
	 */
	private ChatServer chatServer;
	/**
	 * ServerSocket to listen for incoming connections on.
	 */
	private ServerSocket serverSocket;
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
	 * Logger object to be used by the ClientAccepter class.
	 */
	private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

	/**
	 * Creates a new ClientAccepter object that accepts clients for the chat
	 * server represented by ChatServer.
	 * 
	 * @param chatServer
	 *            the ChatServer object that this ClientAccepter will accept
	 *            clients for
	 */
	public ClientAccepter(ChatServer chatServer) {
		this.chatServer = chatServer;
		int port = Integer.parseInt(chatServer.getProperties().getProperty("port"));
		try {
			serverSocket = new ServerSocket(port);
			logger.log(Level.INFO, "Listening for clients on port " + port);
		} catch (IOException e) {
			logger.log(Level.SEVERE,
					"Could not listen for clients on port " + Integer.toString(port) + " (Is it already in use?)", e);
			e.printStackTrace();
		}
	}

	/**
	 * Listens for clients connecting to serverSocket, receives, their
	 * InitClientInfo, adds them to the ChatServer chatServer and sends them an
	 * InitServerInfo object.
	 */
	@Override
	public void run() {
		Socket newClientSocket;
		Client newClient;
		while (true) {
			try {
				newClientSocket = serverSocket.accept();
				out = new ObjectOutputStream(newClientSocket.getOutputStream());
				in = new ObjectInputStream(newClientSocket.getInputStream());

				// Get InitClientInfo from the client
				InitClientInfo ici = (InitClientInfo) in.readUnshared();

				// Create a ClientInfo object for the new client
				ClientInfo newClientInfo = new ClientInfo(ici.getNick());

				// Create the InitServerInfo to send to the new client
				InitServerInfo isi = new InitServerInfo(newClientInfo,
						chatServer.getDefaultChatRoom().getChatRoomInfo());

				// Ensure that the nick requested by the user is not already
				// taken
				if (chatServer.getDefaultChatRoom().hasClient(ici.getNick())) {
					isi.setFlag(InitServerInfo.BAD_NICK);
					String appendString;
					int counter = 1;
					do {
						appendString = "_" + Integer.toString(counter);
					} while (chatServer.getDefaultChatRoom().hasClient(ici.getNick() + appendString));
					isi.setNick(ici.getNick() + appendString);
				}

				// Create a Client object for the new client
				newClient = new Client(out, in, newClientSocket, newClientInfo, chatServer);

				// Add the client to the default chat room
				chatServer.getDefaultChatRoom().addClient(newClient);

				// Send the InitServerInfo to the client
				out.writeUnshared(isi);

				// Start the client's thread and add it to the chatServer's list
				// of clients
				Thread clientThread = new Thread(newClient);
				clientThread.setDaemon(true);
				clientThread.setName("ClientThread - " + newClientInfo.getNick());
				clientThread.start();

				// Log information about the client that just connected
				logger.log(Level.INFO, "New client connected. Nick:" + newClientInfo.getNick() + " Addr:"
						+ newClientSocket.getInetAddress().toString());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
