package com.gunshippenguin.jchat.client;

import java.util.HashMap;

import com.gunshippenguin.jchat.shared.CreateChatRoomRequest;
import com.gunshippenguin.jchat.shared.ClientInfo;
import com.gunshippenguin.jchat.shared.GetChatRoomListRequest;
import com.gunshippenguin.jchat.shared.SendChatMessageRequest;

import javafx.scene.input.KeyEvent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Object representing the client's user interface. Contains various methods to
 * interface with the UI.
 * 
 * @author GunshipPenguin
 *
 */
public class UserInterface extends Application {
	/**
	 * ListView used to store chat messages and other messages received from the
	 * server.
	 */
	private ListView<String> messagesListView;

	/**
	 * ListView used to display all users in the currently selected chat room.
	 */
	private ListView<ClientInfo> usersListView;

	/**
	 * Text displaying the user's nickname on the current server.
	 */
	private Text nickNameText;
	/**
	 * TreeView that displays all servers and chat rooms the user is connected
	 * to.
	 */
	private TreeView<ChatRoomManager> serverTreeView;
	/**
	 * Hidden root item of the serverTreeView
	 */
	private TreeItem<ChatRoomManager> root;
	/**
	 * TextField used to enter chat messages and commands.
	 */
	private TextField chatTextField;

	/**
	 * HashMap that stores command names as a string and commands as a Command.
	 */
	private HashMap<String, Command> commands;

	/**
	 * Sets up and displays the main UI window to the user.
	 * 
	 * @param chatStage
	 *            - The stage to use for the main UI window.
	 */
	private void showChatWindow(Stage chatStage) {
		loadCommands();

		chatStage.setTitle("JChat-Client");
		BorderPane border = new BorderPane();

		// Chat messages
		messagesListView = new ListView<String>();
		border.setCenter(messagesListView);

		// Users list
		VBox rightPane = new VBox();
		usersListView = new ListView<ClientInfo>();
		rightPane.getChildren().add(new Text("Users"));
		rightPane.getChildren().add(usersListView);
		rightPane.setPrefWidth(100);
		VBox.setVgrow(usersListView, Priority.ALWAYS);
		border.setRight(rightPane);

		// Chat bar and send button and nickname
		HBox bottomPane = new HBox(10);

		nickNameText = new Text();
		nickNameText.setVisible(false);
		bottomPane.getChildren().add(nickNameText);

		chatTextField = new TextField();
		HBox.setHgrow(chatTextField, Priority.ALWAYS);
		bottomPane.getChildren().add(chatTextField);

		Button sendButton = new Button("Send");
		HBox.setHgrow(sendButton, Priority.NEVER);
		bottomPane.getChildren().add(sendButton);

		border.setBottom(bottomPane);

		// Servers and chat rooms
		VBox leftPane = new VBox();
		leftPane.getChildren().add(new Text("Servers/Rooms"));
		root = new TreeItem<>();
		serverTreeView = new TreeView<ChatRoomManager>(root);
		serverTreeView.setShowRoot(false);
		serverTreeView.setPrefWidth(100);
		VBox.setVgrow(serverTreeView, Priority.ALWAYS);
		leftPane.getChildren().add(serverTreeView);
		border.setLeft(leftPane);

		serverTreeView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<ChatRoomManager>>() {
					@Override
					public void changed(ObservableValue<? extends TreeItem<ChatRoomManager>> observable,
							TreeItem<ChatRoomManager> oldValue, TreeItem<ChatRoomManager> newValue) {
						if (newValue != null) {
							switchRooms(newValue.getValue());
						}
					}
				});

		sendButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				sendChatMessageOrCommand();
			}
		});

		chatTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode() == KeyCode.ENTER) {
					sendChatMessageOrCommand();
				}
			}
		});

		Scene scene = new Scene(border, 800, 400);
		chatStage.setScene(scene);
		chatStage.show();
	}

	/**
	 * Returns the TreeItem currently selected in the serverTreeView.
	 * 
	 * @return - The TreeItem currently selected in the serverTreeView
	 */
	private TreeItem<ChatRoomManager> getSelectedChatRoom() {
		MultipleSelectionModel<TreeItem<ChatRoomManager>> sel = serverTreeView.getSelectionModel();
		TreeItem<ChatRoomManager> treeItem = sel.getSelectedItem();
		return treeItem;
	}

	/**
	 * Displays the message to the user in the ListView used for displaying chat
	 * messages.
	 * 
	 * @param msg
	 *            The message to display to the user
	 */
	public void showUiMessage(String msg) {
		messagesListView.getItems().add(msg);
		return;
	}

	/**
	 * Equivalent to showUiMessage, except that the message displayed will
	 * appear with a prefix noting that it is from the server. Useful to let the
	 * user know that the message being shown is from or related to something
	 * server side.
	 * 
	 * @param msg
	 *            The message to display to the user.
	 */
	public void showServerUiMessage(String msg) {
		messagesListView.getItems().add("#SERVER: " + msg);
		return;
	}

	/**
	 * Returns the ChatRoomManager associated with the default chat room of the
	 * currently selected chatRoom's connection.
	 * 
	 * @return - the ChatRoomManager associated with the default chat room of
	 *         the currently selected chatRoom's connection
	 */
	private TreeItem<ChatRoomManager> getSelectedDefaultChatRoom() {
		MultipleSelectionModel<TreeItem<ChatRoomManager>> sel = serverTreeView.getSelectionModel();
		TreeItem<ChatRoomManager> treeItem = sel.getSelectedItem();
		if (treeItem.getParent().equals(root)) {
			return treeItem;
		} else {
			return treeItem.getParent();
		}
	}

	/**
	 * Method called when a user enters text in the chatTextField and presses
	 * enter or presses send. If the first char of the entered text is "/", the
	 * message will be treated as a command and recvCommand will be called,
	 * otherwise, a SendChatMessageRequest will be sent to the server.
	 */
	private void sendChatMessageOrCommand() {
		String msg = chatTextField.getText();
		if (msg.startsWith("/")) {
			msg = msg.substring(1);
			String[] splitCmd = msg.split(" ");
			recvCommand(splitCmd);
		} else {
			ChatRoomManager crm = getSelectedChatRoom().getValue();
			SendChatMessageRequest request = new SendChatMessageRequest(msg, crm.getName());
			crm.getConnection().sendRequest(request);
		}
		chatTextField.clear();
		return;
	}

	/**
	 * Searches the commands HashMap for the command located at cmd[0]. If it
	 * exists, it calls its runCommand method, passing the list of parameters.
	 * 
	 * @param cmd
	 *            - Array of strings representing the command and its
	 *            parameters.
	 */
	private void recvCommand(String[] cmd) {
		if (commands.containsKey(cmd[0])) {
			commands.get(cmd[0]).runCommand(cmd);
		} else {
			showUiMessage("Unknown Command");
		}
		return;
	}

	/**
	 * Class representing a command. Commands can be issued by typing a forward
	 * slash and the name of the command in the text box used to enter chat
	 * messages.
	 */
	abstract class Command {

		/**
		 * String describing what this command does.
		 */
		private String descrip;
		/**
		 * String describing the usage of this command. Required arguments
		 * should be enclosed in greater than / less than signs and optional
		 * arguments should be enclosed in square brackets.
		 */
		private String syntax;

		/**
		 * Creates a new Command object with descrip as the command description
		 * and syntax as a string describing its usage.
		 * 
		 * @param descrip
		 *            A String describing what this command does.
		 * @param syntax
		 *            A String describing the usage of this command.
		 */
		protected Command(String descrip, String syntax) {
			this.descrip = descrip;
			this.syntax = syntax;
		}

		/**
		 * Returns a string describing what this command does.
		 * 
		 * @return A string describing what this command does.
		 */
		private String getDescrip() {
			return descrip;
		}

		/**
		 * Returns a string describing the syntax of this command.
		 * 
		 * @return A String describing the syntax of this command.
		 */
		private String getSyntax() {
			return syntax;
		}

		/**
		 * Method that will be called when the command is issued
		 * 
		 * @param params
		 *            - Array of strings that contain the command parameters
		 */
		abstract public void runCommand(String[] params);
	}

	/**
	 * Instantiates the commands HashMap and loads all commands into it.
	 */
	private void loadCommands() {
		commands = new HashMap<String, Command>();
		commands.put("listchatrooms", new Command("List chat rooms", "") {
			public void runCommand(String[] params) {
				getSelectedChatRoom().getValue().getConnection().sendRequest(new GetChatRoomListRequest());
			}
		});
		commands.put("join", new Command("Join a chat room", "<chat room name>") {
			public void runCommand(String[] params) {
				if (getSelectedChatRoom() != null) {
					if (params.length == 2) {
						if (getSelectedChatRoom() != null) {
							getSelectedChatRoom().getValue().getConnection().joinChatRoom(params[1]);
						} else {
							showUiMessage("You are not connected to a server");
						}
					} else {
						showUiMessage("Incorrect number of parameters");
					}
				} else {
					showUiMessage("You are not connected to a server on which to join a chat room");
				}
			}
		});
		commands.put("server", new Command("Connect to a server", "<hostname> <port> <nickname>") {
			public void runCommand(String[] params) {
				if (params.length == 4) {
					Connection conn = new Connection(params[1], Integer.parseInt(params[2]), UserInterface.this);
					boolean status = conn.connect(params[3]);
					if (status) {
						addConnection(conn);
					}
				} else {
					showUiMessage("Incorrect number of parameters");
				}
			}
		});
		commands.put("leave", new Command("Leave current chat room", "") {
			public void runCommand(String[] params) {
				if (getSelectedChatRoom() != null) {
					if (!getSelectedChatRoom().getValue().isDefaultChatRoom()) {
						removeChatRoom(getSelectedChatRoom());
					} else {
						showUiMessage("You cannot leave the default server chat room (use /disconnect)");
					}
				} else {
					showUiMessage("No chat room selected");
				}
			}
		});

		commands.put("createchatroom", new Command("Create a chat room", "<chat room name>") {
			public void runCommand(String[] params) {
				if (params.length == 2) {
					if (getSelectedChatRoom() != null) {
						Connection conn = getSelectedChatRoom().getValue().getConnection();
						conn.sendRequest(new CreateChatRoomRequest(params[1]));
					} else {
						showUiMessage("You are not connected to a server to create a chat room on");
					}
				} else {
					showUiMessage("Incorrect number of parameters");
				}
			}
		});

		commands.put("disconnect", new Command("Disconnect from current server", "") {
			public void runCommand(String[] params) {
				if (getSelectedChatRoom() != null) {
					Connection conn = getSelectedChatRoom().getValue().getConnection();
					conn.disconnect();
					TreeItem<ChatRoomManager> selectedCrm = getSelectedChatRoom();

					if (selectedCrm.getValue().isDefaultChatRoom()) {
						removeConnection(selectedCrm);
					} else {
						removeConnection(selectedCrm.getParent());
					}
					clearRooms();
				} else {
					showUiMessage("You are not connected to a server to disconnect from");
				}
			}
		});

		commands.put("help", new Command("Display help information", "[command name]") {
			@Override
			public void runCommand(String[] params) {
				if (params.length == 1) {
					showUiMessage(
							"List of all commands, use (/help [command name] for information on specific commands)");
					for (String cmd : commands.keySet()) {
						showUiMessage("\t/" + cmd + " - " + commands.get(cmd).getDescrip());
					}
				} else {
					if (commands.containsKey(params[1])) {
						Command cmd = commands.get(params[1]);
						showUiMessage("/" + params[1] + " - " + cmd.getDescrip());
						showUiMessage("Syntax: " + "/" + params[1] + " " + cmd.getSyntax());
					} else {
						showUiMessage("Command not found");
					}
				}
			}
		});
	}

	/**
	 * Updates the list views displaying messages, users and the text displaying
	 * the user's nickname so that they are in accordance with the chatRoom crm.
	 * This is called when a user selects a new chatRoom.
	 * 
	 * @param crm
	 *            - The ChatRoomManager to use
	 */
	private void switchRooms(ChatRoomManager crm) {
		messagesListView.setItems(crm.getMessages());
		usersListView.setItems(crm.getClients());
		nickNameText.setText(crm.getConnection().getSelfClientInfo().getNick());
		nickNameText.setVisible(true);
		return;
	}

	/**
	 * Removes the TreeItem connTreeItem from the tree view that displays
	 * connections and servers. Also removes all of its children. This should be
	 * used to remove a connection from the tree view after a disconnect.
	 * 
	 * Note that connTreeItem is of type TreeItem<ChatRoomManager> with the
	 * ChatRoomManager representing not a connection, but the default chat room
	 * for that connection.
	 * 
	 * @param connTreeItem
	 *            - The TreeItem representing a connection to be removed
	 */
	private void removeConnection(TreeItem<ChatRoomManager> connTreeItem) {
		root.getChildren().remove(connTreeItem);
		return;
	}

	/**
	 * Removes the TreeItem crmTreeItem from the tree view that displays
	 * connections and servers. This should not be used to remove a connection,
	 * for that, use removeConnection()
	 * 
	 * @param crmTreeItem
	 *            - The TreeItem representing a chat room to be removed
	 */
	private void removeChatRoom(TreeItem<ChatRoomManager> crmTreeItem) {
		ChatRoomManager crm = crmTreeItem.getValue();

		if (!crm.isDefaultChatRoom()) {
			// Remove the chatRoomManger from the connection
			crm.getConnection().leaveChatRoom(crm.getName());

			// Remove the chatRoomManager from the tree view
			getSelectedDefaultChatRoom().getChildren().remove(crmTreeItem);

			// Switch the displayed chatRoom to the one selected after the
			// removal
			// Or call clearRooms(), if no connections remain
			if (root.getChildren().size() > 0) {
				switchRooms(serverTreeView.getSelectionModel().getSelectedItem().getValue());
			} else {
				clearRooms();
			}
		}
	}

	/**
	 * Removes all content from the listView displaying messages, users and sets
	 * the visibility of the text displaying the user's nickname to false. This
	 * is called when a user disconnects from a server and there are no other
	 * servers to display content from.
	 */
	private void clearRooms() {
		messagesListView.getItems().clear();
		usersListView.getItems().clear();
		nickNameText.setVisible(false);
	}

	/**
	 * Adds a connection to the TreeView containing the list of servers and chat
	 * rooms.
	 * 
	 * @param c
	 *            - The connection to add
	 */
	public void addConnection(Connection c) {
		root.getChildren().add(new TreeItem<ChatRoomManager>(c.getDefaultChatRoom()));
		serverTreeView.getSelectionModel().select(0);
		return;
	}

	/**
	 * Adds a chat room to the tree view containing the list of servers and chat
	 * rooms as a child of the TreeItem containing the connection conn. If conn
	 * cannot be found in the TreeView, an exception is thrown.
	 * 
	 * @param conn
	 *            - The connection that this chat room belongs to
	 * @param crm
	 *            - The ChatRoomManager to add
	 */
	public void addChatRoom(Connection conn, final ChatRoomManager crm) {
		final MultipleSelectionModel<TreeItem<ChatRoomManager>> sel = serverTreeView.getSelectionModel();
		for (final TreeItem<ChatRoomManager> t : root.getChildren()) {
			if (t.getValue().getConnection().equals(conn)) {
				final TreeItem<ChatRoomManager> treeItem = new TreeItem<ChatRoomManager>(crm);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						t.getChildren().add(treeItem);
						sel.select(treeItem);
						switchRooms(crm);
						t.setExpanded(true);
					}

				});
				return;
			}
		}
		throw new RuntimeException("Could not find connection");
	}

	@Override
	public void start(Stage primaryStage) {
		showChatWindow(primaryStage);
	}
}
