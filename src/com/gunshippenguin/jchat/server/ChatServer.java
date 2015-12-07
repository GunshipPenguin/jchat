package com.gunshippenguin.jchat.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gunshippenguin.jchat.shared.ChatRoomAddedEvent;
import com.gunshippenguin.jchat.shared.ChatRoomInfo;

/**
 * Class representing a running JChat chat server. Contains methods to add,
 * remove and interface with chat rooms.
 * 
 * @author GunshipPenguin
 *
 */
public class ChatServer {
	/**
	 * ArrayList of ChatRoom objects representing all ChatRooms on this server.
	 */
	private ArrayList<ChatRoom> chatRooms = new ArrayList<ChatRoom>();
	/**
	 * DefaultChatRoom object for this server.
	 */
	private DefaultChatRoom defaultChatRoom;
	/**
	 * Properties of this server containing configuration information.
	 */
	private Properties properties;
	/**
	 * Logger object for the ChatServer.
	 */
	private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

	public ChatServer() {
		logger.log(Level.INFO, "Jchat server starting");

		// Create the default chat room
		defaultChatRoom = new DefaultChatRoom(this);
		chatRooms.add(defaultChatRoom);

		// Load server properties
		File f = new File("server.properties");
		if (!f.exists()) {
			logger.log(Level.INFO, "No server.properties file found, creating one");
			setDefaultProperties();
		}
		this.properties = loadProperties();

		// Create the client accepter thread
		Thread clientAccepterThread = new Thread(new ClientAccepter(this));
		clientAccepterThread.setName("ClientAccepterThread");
		clientAccepterThread.start();
	}

	/**
	 * Creates a properties file for this chatServer and fills it with default
	 * configuration information.
	 */
	private void setDefaultProperties() {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream("server.properties");
			prop.setProperty("port", "9001");

			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns the properties object containing configuration information for
	 * this ChatServer.
	 * 
	 * @return The properties object containing configuration information for
	 *         this ChatServer
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Loads the information from the server.properties file into a properties
	 * object and returns it.
	 * 
	 * @return The information from the server.properties file.
	 */
	private Properties loadProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream("server.properties");
			prop.load(input);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	/**
	 * Returns an ArrayList of ChatRoomInfo objects representing all chat rooms
	 * on this chat server.
	 * 
	 * @return An ArrayList of ChatRoomInfo objects representing all chat rooms
	 *         on this chat server
	 */
	public ArrayList<ChatRoomInfo> getChatRoomList() {
		ArrayList<ChatRoomInfo> chatRoomList = new ArrayList<ChatRoomInfo>();
		for (ChatRoom cr : chatRooms) {
			chatRoomList.add(cr.getChatRoomInfo());
		}
		// return new ArrayList<ChatRoomInfo>();
		return chatRoomList;
	}

	/**
	 * Adds the ChatRoom cr to this ChatServer.
	 * 
	 * @param cr
	 *            The chatRoom to add to the server
	 */
	public void addChatRoom(ChatRoom cr) {
		chatRooms.add(cr);
		logger.log(Level.INFO, "Chat Room " + cr.getChatRoomInfo().getName() + " created");
		defaultChatRoom.sendEventToAllClients(new ChatRoomAddedEvent(cr.getChatRoomInfo().getName()));
		return;
	}

	/**
	 * Returns true if a chat room with name name exists on this server, false
	 * otherwise.
	 * 
	 * @param name
	 *            The name of the chat room to search for
	 * @return True if a chat room with name exists, false otherwise
	 */
	public boolean chatRoomExists(String name) {
		for (ChatRoom cr : chatRooms) {
			if (cr.getChatRoomInfo().getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If a chat room exists in this ChatServer with the name name, returns its
	 * ChatRoom object. If there is no chat room with the name name in this
	 * ChatRoom, throws a runtime exception.
	 * 
	 * @param name
	 *            The name of the chat room whose ChatRoom object should be
	 *            returned
	 * @return The ChatRoom object representing the chat room with a name of
	 *         name.
	 */
	public ChatRoom getChatRoomByName(String name) {
		for (ChatRoom cr : chatRooms) {
			if (cr.getChatRoomInfo().getName().equals(name)) {
				return cr;
			}
		}
		throw new RuntimeException("ChatRoom with name of " + name + " could not be found");
	}

	/**
	 * Returns a DefaultChatRoom object representing the default chat room on
	 * this server.
	 * 
	 * @return A DefaultChatRoom object representing the default chat room on
	 *         this server
	 */
	public DefaultChatRoom getDefaultChatRoom() {
		return defaultChatRoom;
	}

	public static void main(String[] args) {
		new ChatServer();
	}
}
