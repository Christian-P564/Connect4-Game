

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application{

	Stage mainStage;
	BorderPane main;
	Client clientConnection;
	Label currentTurn;
	Label usernameTaken;
	Button replay;

	ListView<String> chatMessages;
	TextField chatInput;
	Button chatSend;

	ListView<String> listItems;


	Button[][] boardButtons = new Button[6][7];

	Scene waitingRoom;
	Scene gameScene;

	VBox yourInfo;
	VBox opponentInfo;
	Label yourNameLabel;
	Label opponentNameLabel;
	Label playerInd1;
	Label playerInd2;
	String yourName;
	String opponentName;

	TextField usernameInput;


	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;

		clientConnection = new Client(data->{
			Platform.runLater(()->{
				switch (data.type){
					case NEWUSER:
						listItems.getItems().add(data.username + " has joined!");
						break;
					case DISCONNECT:
						listItems.getItems().add(data.username + " has disconnected!");
						break;
					case TEXT:
						listItems.getItems().add(data.message);
						currentTurn.setText(data.message);
						break;

					case GAMESTART:
						listItems.getItems().add(data.message);
						currentTurn.setText(data.message);
						replay.setVisible(false);

						yourNameLabel.setText(yourName);
						opponentName = data.username;
						opponentNameLabel.setText(opponentName);

						gameScene = new Scene(main, 1000, 675);
						mainStage.setScene(gameScene);
						mainStage.setTitle("Connect Four Game");
						break;

					case UPDATEGAME:
						createBoard(data.board);
						break;

					case CHAT:
						chatMessages.getItems().add(data.username + ": " + data.message);
						break;

					case ERROR:
						if (data.message.contains("username taken")) {
							usernameTaken.setText("Username is taken, try a new one.");
							usernameInput.clear();

						} else {
							listItems.getItems().add("Error: " + data.message);
						}
						break;

					case UNIQUE_JOIN:
						createWaitingRoom();
						mainStage.setScene(waitingRoom);
						mainStage.setTitle("Connect Four Waiting Room");
						break;

					case RESULT:
						listItems.getItems().add(data.message);
						currentTurn.setText("Game over " + data.message);
						disableBoard();
						if (data.message.contains("has left")){
							replay.setVisible(false);
							currentTurn.setText("Game over " + data.message + "\nClose and restart game to play more");
						}
						else {
						}
						break;

				}
			});
		});

		clientConnection.start();



		usernameInput = new TextField();
		usernameInput.setPromptText("Enter your username");
		usernameInput.setMaxWidth(200);

		Button confirmUsername = new Button("Play");
		Label TitleCard = new Label("Connect 4");
		TitleCard.setStyle("-fx-font-weight: bold; -fx-font-size: 24");
		usernameTaken = new Label(" ");
		usernameTaken.setStyle("-fx-text-fill: red; -fx-font-weight: bold");

		VBox nameBox = new VBox(10, TitleCard, usernameInput, usernameTaken, confirmUsername);
		nameBox.setStyle("-fx-background-color: #DCE3F2; -fx-font-weight: bold");
		nameBox.setAlignment(Pos.CENTER);


		Scene nameScene = new Scene(nameBox, 500, 337);


		mainStage.setScene(nameScene);
		mainStage.setTitle("Connect Four Login");
		mainStage.show();



		confirmUsername.setOnAction(e -> {
			String username = usernameInput.getText().trim();
			if (!username.isEmpty()) {
				yourName = username;
				Message join = new Message(MessageType.JOIN, "Requesting to join");
				join.username = username;
				clientConnection.send(join);

			}
		});


		listItems = new ListView<String>();


		chatMessages = new ListView<>();
		chatMessages.setPrefHeight(100);

		chatInput = new TextField();
		chatInput.setPromptText("Message to opponent");



		chatSend = new Button("Send");
		chatSend.setOnAction(e -> {
			String msg = chatInput.getText();
			if (!msg.isEmpty()) {
				Message chat = new Message(MessageType.CHAT, msg);
				clientConnection.send(chat);
				chatInput.clear();
			}
		});

		HBox inputBox = new HBox(10, chatInput, chatSend);
		inputBox.setPadding(new Insets(10, 10, 10, 10));

		VBox chatBox = new VBox(10, chatMessages, inputBox);
		chatBox.setPadding(new Insets(10, 10, 10, 10));



		currentTurn = new Label("Waiting for game to start...");
		playerInd1 = new Label("You");
		yourNameLabel = new Label();
		yourInfo = new VBox(10, playerInd1, yourNameLabel);
		playerInd2 = new Label("Opponent");
		opponentNameLabel = new Label("HELLO");
		opponentInfo = new VBox(10, playerInd2, opponentNameLabel);

		Label spacer = new Label(" ");
		VBox spacedInfo = new VBox(10, spacer, currentTurn);
		replay = new Button("Play Again?");
		replay.setVisible(false);
		replay.setOnAction((e)->{
			clientConnection.send(new Message(MessageType.REPLAY, "Want to play again."));
			currentTurn.setText("Waiting for opponent response...");
		});

		VBox topLayout = new VBox(10);


		HBox topInfo = new HBox(10);
		topInfo.setAlignment(Pos.CENTER);
		topInfo.setPadding(new Insets(10, 10, 10, 10));
		topInfo.getChildren().addAll(yourInfo, spacedInfo, opponentInfo);
		topLayout.setPadding(new Insets(10, 10, 10, 10));
		topLayout.getChildren().addAll(topInfo, replay);
		topLayout.setAlignment(Pos.CENTER);
		topLayout.setStyle("-fx-font-weight: bold");

		main = 	new BorderPane();
		main.setTop(topLayout);
		main.setBottom(chatBox);
		main.setStyle("-fx-background-color: #DCE3F2;");




		mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

	}



	private void createWaitingRoom() {
		VBox waitingLayout = new VBox(20);
		waitingLayout.setPadding(new Insets(20));
		waitingLayout.setStyle("-fx-background-color: #DCE3F2;");


		Label waitingRoomUsername = new Label("Your name is: " + yourName);
		Label waitingLabel = new Label("Waiting for an opponent...");
		waitingLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold");
		waitingLayout.setAlignment(Pos.CENTER);

		waitingLayout.getChildren().addAll(waitingRoomUsername, waitingLabel);

		waitingRoom = new Scene(waitingLayout, 1000, 675);
	}
	private void disableBoard(){
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 7; j++){
				boardButtons[i][j].setDisable(true);
			}
		}
	}

	private void createBoard(int[][] board) {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setHgap(5);
		grid.setVgap(5);
		grid.setStyle("-fx-background-color: #0077be;");
		grid.setPrefSize(475, 400);
		grid.setMaxSize(475, 400);


		int boardRow = 0;
		for (int row = 5; row >= 0; row--) {

			for (int col = 0; col < 7; col++) {

				Button spot = new Button();
				spot.setPrefSize(60, 60);
				spot.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

				if (board[row][col] == 0) {
					spot = new Button();
					spot.setPrefSize(60, 60);
					spot.setStyle("-fx-background-color: #DCE3F2; -fx-background-radius: 30px;");
					grid.add(spot, col, boardRow);
				}
				else if (board[row][col] == 1) {
					spot = new Button();
					spot.setPrefSize(60, 60);
					spot.setStyle("-fx-background-color: red; -fx-background-radius: 30px;");
					grid.add(spot, col, boardRow);
				}
				else{
					spot = new Button();
					spot.setPrefSize(60, 60);
					spot.setStyle("-fx-background-color: yellow; -fx-background-radius: 30px;");
					grid.add(spot, col, boardRow);
				}
				boardButtons[row][col] = spot;

				int colForMove = col;
				spot.setOnAction(e -> {
					Message move = new Message(MessageType.MOVE, "Move made");
					move.moveCol = colForMove;
					clientConnection.send(move);
				});

			}
			boardRow++;
		}

		main.setCenter(grid);
	}


}
