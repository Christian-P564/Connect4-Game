import java.util.function.Consumer;

public class Match extends Thread {

    private Server.ClientThread player1;
    private Server.ClientThread player2;
    private Server.ClientThread currTurn;
    private ConnectFourGame game;
    private boolean isGameOver = false;

    private Consumer<Message> callback;
    private Server server;

    public Match(Server server, Server.ClientThread player1, Server.ClientThread player2, Consumer<Message> callback) {
        this.server = server;
        this.player1 = player1;
        this.player2 = player2;
        this.currTurn = player1;
        this.game = new ConnectFourGame();
        this.callback = callback;

        player1.currMatch = this;
        player2.currMatch = this;
    }

    public void run(){
        Message p1_info = new Message(MessageType.GAMESTART, "Game started. Your Turn!");
        p1_info.username = getOpponent(player1).username;
        Message p2_info = new Message(MessageType.GAMESTART, "Game started. Opponent's move.");
        p2_info.username = getOpponent(player2).username;

        player1.sendMessage(p1_info);
        player2.sendMessage(p2_info);
        sendBoardUpdate();
    }

    public void moveHandler(Server.ClientThread sender, Message message) {
        if (isGameOver || sender != currTurn){
            sender.sendMessage(new Message(MessageType.TEXT, "Wait for your turn"));
            return;
        }

        int col = message.moveCol;

        if (currTurn == player1) {
            game.setCurrTurn(1);
        } else {
            game.setCurrTurn(2);
        }

        boolean validMove = game.makeMove(col);


        if (!validMove){
            sender.sendMessage(new Message(MessageType.ERROR, "Invalid Move"));
            return;
        }

        sendBoardUpdate();

        if (game.checkWin(game.getMoveRow(), game.getMoveCol())){
            isGameOver = true;
            Server.ClientThread winner = currTurn;
            Server.ClientThread loser;
            if (currTurn == player1){
                loser = player2;
            }
            else{
                loser = player1;
            }
            winner.sendMessage(new Message(MessageType.RESULT, "You won"));
            loser.sendMessage(new Message(MessageType.RESULT, "You lost"));

            callback.accept(new Message(MessageType.TEXT, winner.username + " won the game"));
        }
        else if (game.isDraw()){
            isGameOver = true;
            player1.sendMessage(new Message(MessageType.RESULT, "Draw!"));
            player2.sendMessage(new Message(MessageType.RESULT, "Draw!"));
            callback.accept(new Message(MessageType.TEXT, "Game is a draw"));
        }
        else {
            switchTurn();
            currTurn.sendMessage(new Message(MessageType.TEXT, "Your Turn"));
            getOpponent(currTurn).sendMessage(new Message(MessageType.TEXT, "Wait for your turn"));

            callback.accept(new Message(MessageType.TEXT, currTurn.username + ": Placed a piece"));
        }


    }

    public void chatHandler(Server.ClientThread sender, Message message) {
        Server.ClientThread recipient = getOpponent(sender);
        Message chat = new Message(MessageType.CHAT, message.message);
        chat.username = sender.username;
        sender.sendMessage(chat);
        recipient.sendMessage(chat);
        callback.accept(new Message(MessageType.TEXT, sender.username + " sent chat: " + message.message));
        callback.accept(new Message(MessageType.TEXT, "Delivered to: " + recipient.username));

    }

    public void disconnectHandler(Server.ClientThread player) {
        Server.ClientThread paired = getOpponent(player);
        paired.sendMessage(new Message(MessageType.RESULT, player.username + " has left"));

    }



    public Server.ClientThread getOpponent(Server.ClientThread player) {
        if (player == player1){
            return player2;
        }
        else{
            return player1;
        }
    }

    public void switchTurn() {
        if (currTurn == player1){
            currTurn = player2;
            game.setCurrTurn(2);
        }
        else{
            currTurn = player1;
            game.setCurrTurn(1);
        }
    }

    public void sendBoardUpdate() {
        Message updatedBoard = new Message(MessageType.UPDATEGAME, "Board Updated");
        updatedBoard.board = game.getBoard();
        player1.sendMessage(updatedBoard);
        player2.sendMessage(updatedBoard);
    }

    private boolean player1Replay = false;
    private boolean player2Replay = false;

    public void replayRequest(Server.ClientThread sender) {
        if (sender == player1){
            player1Replay = true;
            player1.sendMessage(new Message(MessageType.TEXT, "Waiting for Opponent Response..."));

        }
        if (sender == player2){
            player2Replay = true;
            player1.sendMessage(new Message(MessageType.TEXT, "Waiting for Opponent Response..."));
        }

        if (player1Replay && player2Replay){
            this.game = new ConnectFourGame();
            this.isGameOver = false;
            this.currTurn = player1;
            this.player1Replay = false;
            this.player2Replay = false;

            callback.accept(new Message(MessageType.TEXT, player1.username + " and " + player2.username + " play again."));


            Message p1Info = new Message(MessageType.GAMESTART, "New Game Started. Your Turn!");
            p1Info.username = getOpponent(player1).username;
            player1.sendMessage(p1Info);
            Message p2Info = new Message(MessageType.GAMESTART, "New Game Started. Opponents Turn");
            p2Info.username = getOpponent(player2).username;
            player2.sendMessage(p2Info);
            sendBoardUpdate();
        }
    }


}
