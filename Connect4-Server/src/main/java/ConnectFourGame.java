public class ConnectFourGame {
    private int[][] board;
    private int currTurn;
    private int moveRow;
    private int moveCol;
    private int winner;

    public ConnectFourGame() {
        board = new int[6][7];
        currTurn = 1;
    }

    public int[][] getBoard() {
        int[][] copy = new int[6][7];
        for (int i = 0; i < 6; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, 7);
        }
        return copy;
    }

    public boolean makeMove(int column){
        for (int i = 0; i < 6; i++){
            if (board[i][column] == 0){
                board[i][column] = currTurn;
                moveRow = i;
                moveCol = column;

                return true;
            }
        }
        moveRow = -1;
        moveCol = -1;
        return false;
    };





    public boolean isDraw(){
        return isFullBoard() && winner == 0;
    }

    public boolean isWin(){
        return winner != 0;
    }

    public int getWinner(){
        return winner;
    }

    public int getCurrTurn(){
        return currTurn;
    }

    public boolean checkWin(int row, int col){
        if (horizontalWin(row, col) || verticalWin(row, col) || diagonal1Win(row, col) || diagonal2Win(row, col)){
            winner = currTurn;
            return true;
        }
        else {
            return false;
        }
    };


    private boolean horizontalWin(int row, int col){
        int count = 0;
        for (int i = col; i < 7; i++){
            if (board[row][i] == currTurn){
                count++;
            }
            else{
                break;
            }
        }
        for (int i = col-1; i >= 0; i--){
            if (board[row][i] == currTurn){
                count++;
            }
            else{
                break;
            }
        }
        if (count >= 4){
            return true;
        }
        return false;

    };
    private boolean verticalWin(int row, int col){
        int count = 0;
        for (int i = row; i < 6; i++){
            if (board[i][col] == currTurn){
                count++;
            }
            else{
                break;
            }
        }
        for (int i = row-1; i >= 0; i--){
            if (board[i][col] == currTurn){
                count++;
            }
            else{
                break;
            }
        }
        if (count >= 4){
            return true;
        }
        return false;
    };
    //Top Left to Bottom Right
    private boolean diagonal1Win(int row, int col){
        int count = 1;
        //Check up-left of piece
        int r = row + 1;
        int c = col - 1;
        while (r < 6 && c >= 0 && board[r][c] == currTurn){
            r++;
            c--;
            count++;
        }

        //Check down-right of piece
        r = row - 1;
        c = col + 1;
        while (r >= 0 && c < 7 && board[r][c] == currTurn){
            r--;
            c++;
            count++;
        }

        if (count >= 4){
            return true;
        }
        return false;

    };
    private boolean diagonal2Win(int row, int col){
        int count = 1;
        //Check down-left of piece
        int r = row - 1;
        int c = col - 1;
        while (r >= 0 && c >= 0 && board[r][c] == currTurn){
            r--;
            c--;
            count++;
        }

        //Check up-right of piece
        r = row + 1;
        c = col + 1;
        while (r < 6 && c < 7 && board[r][c] == currTurn){
            r++;
            c++;
            count++;
        }

        if (count >= 4){
            return true;
        }
        return false;
    };
    private boolean isFullBoard(){
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 7; j++){
                if (board[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    };

    public int getMoveRow(){
        return moveRow;
    }
    public int getMoveCol(){
        return moveCol;
    }


    public void setCurrTurn(int turn){
        this.currTurn = turn;
    }


}
