import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server{

	HashSet<String> usernames = new HashSet<>();
	HashMap<String, ClientThread>  usernameToClient = new HashMap<>();
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	ClientThread waitingForOpponent = null;

	TheServer server;
	private Consumer<Message> callback;


	Server(Consumer<Message> call){

		callback = call;
		server = new TheServer();
		server.start();
	}

	private void updateUsers() {
		Message userListUpdate = new Message(MessageType.TEXT, String.join(",", usernames));
		callback.accept(userListUpdate);
	}


	public class TheServer extends Thread{

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");


				while(true) {

					ClientThread c = new ClientThread(mysocket.accept());
					clients.add(c);
					c.start();

				}
			}//end of try
			catch(Exception e) {
				callback.accept(new Message(MessageType.TEXT, "Server did not launch"));
			}
		}//end of while
	}




	class ClientThread extends Thread{


		Socket connection;
		ObjectInputStream in;
		ObjectOutputStream out;
		String username;
		Match currMatch;

		ClientThread(Socket s){
			this.connection = s;
		}

		public void sendMessage(Message message) {
			try {
				out.writeObject(message);
			} catch (Exception e) {
				System.out.println("Failed to send message to: " + username);
			}
		}



		private void joinHandler(Message data) throws Exception{
			if (data.username == null || usernames.contains(data.username)){
				sendMessage(new Message(MessageType.ERROR, "username taken"));
				return;
			}

			this.username = data.username;
			usernames.add(username);
			usernameToClient.put(username, this);

			Message joinMsg = new Message(MessageType.NEWUSER, username + " has joined");
			joinMsg.username = username;
			callback.accept(joinMsg);

			updateUsers();

			sendMessage(new Message(MessageType.UNIQUE_JOIN, username));

			if (waitingForOpponent == null || waitingForOpponent.currMatch != null){
				waitingForOpponent = this;
				sendMessage(new Message(MessageType.TEXT, "Waiting for opponent!"));
			}
			else{
				Match match = new Match(Server.this, waitingForOpponent, this, callback);
				waitingForOpponent = null;
				match.start();
			}
		}

		public void run(){

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}


			try{
				while (true){
					Message data = (Message) in.readObject();

					switch (data.type){
						case TEXT:

						case JOIN:
							joinHandler(data);
							break;

						case CHAT:
							if (currMatch != null){
								currMatch.chatHandler(this, data);
							}
							break;

						case MOVE:
							if (currMatch != null){
								currMatch.moveHandler(this, data);
							}
							break;

						case ERROR:
							if (data.username == null || usernames.contains(data.username)) {
								sendMessage(new Message(MessageType.ERROR,"username taken"));
								return;
							}
							break;

						case REPLAY:
							if (currMatch != null){
								currMatch.replayRequest(this);
							}
							break;

					}
				}
			}
			catch (Exception e){
				usernames.remove(username);
				usernameToClient.remove(username);
				clients.remove(this);

				if (waitingForOpponent == this){
					waitingForOpponent = null;
				}
				if (currMatch != null){
					currMatch.disconnectHandler(this);
				}

				Message disconnectMessage = new Message(MessageType.DISCONNECT, username + " has disconnected");
				disconnectMessage.username = username;
				callback.accept(disconnectMessage);
			}


		}//end of run




	}//end of client thread
}


	
	

	
