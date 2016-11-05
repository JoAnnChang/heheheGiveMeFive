/*
 * public int getPlayer() 
 * 
 * public int getBaordStatus(int r, int c)
 * 
 * public int getWinner()
 * 
 * public void reset() ---- restart the game
 * 
 * public void move(int r, int c) ---- put a chess piece
 * 
 * 
 * public int QScore(int r, int c, int player) ---- count score after trying to put a chess piece on (r, c)
 * 
 * */



public class ChessBoard {
	
	//Constant
	public static final int EMPTY   = 0;  // The cell is empty.
    private static final int PLAYER1 = 1;
    public static final int PLAYER2 = 2;
    private static final int FIVE = 1000;
    private static final int FOUR = 100;
    private static final int THREE = 10;
    private static final int TWO = 1;
	
	private int maxRow = 15;
	private int maxCol = 15;
	private int[][] board;
	private int nextPlayer;
	private int isWin;
	private int moves;
	
	ChessBoard(int rows, int cols) {
		maxRow = rows;
        maxCol = cols;
        board = new int[maxRow][maxCol];
        reset();
	}
	ChessBoard() {
        board = new int[maxRow][maxCol];
        reset();
	}
	
	
	public int getPlayer() {
		return 3-nextPlayer;
	}
	
	public int getBaordStatus(int r, int c) {
		assert r >= 0 && r<=maxCol && c>=0 && c<=maxCol;
		return board[r][c];
	}
	
	public int getWinner(){
		return isWin;
	}

    public void reset() {
        for (int r=0; r<maxRow; r++) {
            for (int c=0; c<maxCol; c++) {
                board[r][c] = EMPTY;
            }
        }
        moves = 0;  // No moves so far.
        isWin = 0;	// So far no winner
        nextPlayer = PLAYER1;  
    }
    
    public void move(int r, int c) {
        assert board[r][c] == EMPTY;	//make sure (r, c) is empty
        board[r][c] = nextPlayer;  	// Record this move.
        nextPlayer = 3-nextPlayer; 	// Flip players
        moves++;                    // Increment number of moves.
    }
    
    public int QScore(int r, int c, int player) {
    	int score = 0;
    	assert board[r][c] == EMPTY;	//make sure (r, c) is empty
    	board[r][c] = player;	//put player on the position to make calculation
	
    	for (int row = 0; row < maxRow; row++) {
    		for (int col = 0; col < maxCol; col++) {
    			int p = board[row][col];
    			if (p != EMPTY) {
                // look at 4 kinds of direction
                //  1. a column going up
                //  2. a row going to the right
                //  3. a diagonal up and to the right
                //  4. a diagonal up and to the left

    				if (row < maxRow-4) {// Look up
    					score += countScore(row, 1, col, 0, player);
    				}

    				if (col < maxCol-4) { // row to right
    					score += countScore(row, 0, col, 1, player);

    					if (row < maxRow-4) { // diagonal up to right
    						score += countScore(row, 1, col, 1, player);
    					}
    				}

    				if (col > 3 && row < maxRow-4) { // diagonal up left
    					score += countScore(row, 1, col, -1, player);
    				}
    			}
    		}
    	}
    	
    	board[r][c] = EMPTY;	//recover the board
    	return score;
    }
    
    
    private int countScore(int r, int c, int dr, int dc, int player) {
    	int count = 1;
        for (int i=1; i<5; i++) {
        	if(board[r+dr*i][c+dc*i] != 3-player) {
        		if(board[r+dr*i][c+dc*i] == player)	count++;
        	}
        	else break;
        }
        if(count==2)	return TWO;
        else if(count==3)	return THREE;
        else if(count==4)	return FOUR;
        else if(count==5) {
        	isWin = player;
        	return FIVE;
        }
        else return 0;
    }
}
