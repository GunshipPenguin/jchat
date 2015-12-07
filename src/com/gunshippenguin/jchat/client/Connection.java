package com.gunshippenguin.jchat.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gunshippenguin.jchat.shared.ChatRoomInfo;
import com.gunshippenguin.jchat.shared.ClientInfo;
import com.gunshippenguin.jchat.shared.InitClientInfo;
import com.gunshippenguin.jchat.shared.InitServerInfo;
import com.gunshippenguin.jchat.shared.JoinChatRoomRequest;
import com.gunshippenguin.jchat.shared.LeaveChatRoomRequest;
import com.gunshippenguin.jchat.shared.Request;

/**
 * Class representing a connection to a server.
 * 
 * @author GunshipPenguin
 *
 */
public class Connection {
	/**
	 * Hostname of the server.
	 */
	private String hostName;
	/**
	 * Port that the server is accepting connections on.
	 */
	private int port;
	/**
	 * ObjectOutputStream used to send request to the server.
	 */
	private ObjectOutputStream objectOut;
	/**
	 * ServerEventHandler handling incoming events for this connection.
	 */
	private ServerEventHandler eventHandler;
	/**
	 * The thread that the ServerEventHandler eventHandler is running on.
	 */
	private Thread eventHandlerThread; // Thread that the eventHandler runs on
	/**
	 * ClientInfo object representing the user's client.
	 */
	private ClientInfo selfClient;
	/**
	 * ArrayList of ChatRoomManagers corresponding to all of the chatRooms that
	 * the user is currently in.
	 */
	private ArrayList<ChatRoomManager> chatRooms;
	/**
	 * UserInterface object associated with this Connection.
	 */
	private UserInterface ui;
	/**
	 * The ChatRoomManager object corresponding to the defaultChatRoom on the
	 * server.
	 */
	private ChatRoomManager defaultChatRoom;
	/**
	 * Socket object connected to the server.
	 */
	private Socket socket;
	/**
	 * Logger object for Connection.
	 */
	private static final Logger logger = Logger.getLogger(Connection.class.getName());

	/**
	 * Creates a new connection object. The host name and port of the server the
	 * Connection will be associated with will be set to hostName and port. The
	 * UserInterface object associated with the connection will be set to ui.
	 * Note that the constructor does not initiate the connection to the server,
	 * the connect() method must be called to initiate a connection to the
	 * server.
	 * 
	 * @param hostName
	 *            The host name of the server that this connection represents
	 * @param port
	 *            The port that the server is receiving new clients on
	 * @param ui
	 *            UserInterface object associated with this Connection
	 */
	public Connection(String hostName, int port, UserInterface ui) {
		this.ui = ui;
		this.hostName = hostName;
		this.port = port;
		this.chatRooms = new ArrayList<ChatRoomManager>();
	}

	/**
	 * Iterates through this Connection's list of ChatRoomManagers and returns
	 * the one with the specified name. If no ChatRoomManager can be found with
	 * the specified name, throws a runtime exception.
	 * 
	 * @param name
	 *            The name of the ChatRoomManager to return.
	 * @return The ChatRoomManager with name name
	 */
	public ChatRoomManager getChatRoomManagerByName(String name) {
		for (ChatRoomManager chatRoomManager : chatRooms) {
			if (chatRoomManager.getName().equals(name)) {
				return chatRoomManager;
			}
		}
		throw new RuntimeException("ChatRoom with name of " + name + " not found");
	}

	/**
	 * Sends a request to the server.
	 * 
	 * @param r
	 *            The request to send
	 */
	public void sendRequest(Request r) {
		logger.log(Level.FINER, "Sending request - " + r.toString());
		try {
			objectOut.reset();
			objectOut.writeObject(r);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return;
	}

	/**
	 * Returns the UserInterface object associated with this Connection.
	 * 
	 * @return The UserInterface object associated with this Connection
	 */
	public UserInterface getUi() {
		return ui;
	}

	/**
	 * Returns the ClientInfo object representing the user's client.
	 * 
	 * @return the ClientInfo object representing the user's client
	 */
	public ClientInfo getSelfClientInfo() {
		return selfClient;
	}

	/**
	 * Returns the ChatRoomManager associated with the default chat room for
	 * this connection.
	 * 
	 * @return the ChatRoomManager associated with the default chat room for
	 *         this connection
	 */
	public ChatRoomManager getDefaultChatRoom() {
		return defaultChatRoom;
	}

	/**
	 * Returns connection information for this server as a String in human
	 * readable format.
	 * 
	 * @return connection information for this server as a String in human
	 *         readable format
	 */
	public String getConnectionInfo() {
		return socket.getInetAddress().getHostName();
	}

	/**
	 * Creates a ChatRoomManager instance for the specified chatRoomInfo and
	 * adds it to this connection's list of ChatRoomManagers.
	 * 
	 * @param chatRoomInfo
	 *            The chatRoomInfo object representing the new chat room to be
	 *            added.
	 */
	public void addChatRoom(ChatRoomInfo chatRoomInfo) {
		ChatRoomManager newCrm = new ChatRoomManager(chatRoomInfo, this);
		chatRooms.add(newCrm);
		ui.addChatRoom(this, newCrm);
		return;
	}

	/**
	 * Sends a LeaveChatRoomRequest to the server regarding the chat room with
	 * name name and removes the ChatRoomManager with name name from the
	 * connection's list of ChatRoomManagers.
	 * 
	 * @param name
	 *            The name of the chat room to disconnect from
	 */
	public void leaveChatRoom(String name) {
		ChatRoomManager crm = getChatRoomManagerByName(name);
		if (!crm.isDefaultChatRoom()) {
			sendRequest(new LeaveChatRoomRequest(name));
			chatRooms.remove(crm);
		}
		return;
	}

	/**
	 * Joins a chat room by sending a JoinChatRoomRequest to the server.
	 * 
	 * @param name
	 *            The name of the chatRoom to join.
	 */
	public void joinChatRoom(String name) {
		sendRequest(new JoinChatRoomRequest(name));
		return;
	}

	/**
	 * Disconnects this connection from the server.
	 */
	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * Attempts to connect to the server.
	 * 
	 * @param nick
	 *            nickname to request when connecting
	 * @return True if connection was successful, false otherwise
	 */
	public boolean connect(String nick) {
		try {
			this.socket = new Socket(hostName, port);
			this.objectOut = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.INFO, "Connection to server " + socket.getInetAddress().getHostAddress() + " on port "
					+ Integer.toString(socket.getPort()) + " Failed");
			return false;
		}

		InitClientInfo ici = new InitClientInfo(nick);
		InitServerInfo isi;
		try {
			// Send the InitClientInfo
			objectOut.writeObject(ici);

			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			isi = (InitServerInfo) ois.readObject();

			// Client object representing this client
			selfClient = isi.getSelfClient();

			// Default chat room for server
			defaultChatRoom = new ChatRoomManager(isi.getDefaultChatRoom(), this);
			chatRooms.add(defaultChatRoom);

			// Handle flags in InitServerInfo
			if ((isi.getFlags() & InitServerInfo.BAD_NICK) == InitServerInfo.BAD_NICK) {
				defaultChatRoom.addServerMessage(
						"Nickname already taken, your nick has been set to " + isi.getSelfClient().getNick());
			}

			// Create a serverEventHandler
			eventHandler = new ServerEventHandler(this, ois);
			eventHandlerThread = new Thread(eventHandler);
			eventHandlerThread.setDaemon(true);
			eventHandlerThread.setName("EventHandlerThread");
			eventHandlerThread.start();
			logger.log(Level.INFO, "Connected to server " + socket.getInetAddress().getHostAddress() + " on port "
					+ Integer.toString(socket.getPort()) + " successfully");

			return true;
		} catch (ClassNotFoundException | IOException e) { // Connection Failed
			e.printStackTrace();
			logger.log(Level.INFO, "Connection to server " + socket.getInetAddress().getHostAddress() + " on port "
					+ Integer.toString(socket.getPort()) + " Failed");
			return false;
		}
	}
}
