import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    MessageType type;
    String message;
    String username;
    public int[][] board;
    public int moveCol;

    public Message(MessageType type, String message){
        this.type = type;
        this.message = message;
    }

    public Message(String username, boolean connect){
        if(connect) {
            type = MessageType.NEWUSER;
            message = username + " has joined!";
        } else {
            type = MessageType.DISCONNECT;
            message = username + " has disconnected!";
        }
    }

}

