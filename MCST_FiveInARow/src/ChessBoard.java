import java.awt.Point;

import javax.net.ssl.ExtendedSSLSession;

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
 **/



public class ChessBoard implements Cloneable{
	

	
	//Constant
	public static final int EMPTY   = 0;  // The cell is empty.
	public static final int BLACK = 1;
    public static final int WHITE = 2;
    
    public static final int FIVE = 1000;
    public static final int FOUR = 100;
    public static final int THREE = 10;
    public static final int TWO = 1;
    public static final int LOSE = -1000000;
	
	public int maxRow = 15;
	public int maxCol = 15;
	private ChessType[][] board;
	private ChessType player;
	private ChessType isWin;
	private int moves;
	
	ChessBoard(int rows, int cols) {
		maxRow = rows;
        maxCol = cols;
        board = new ChessType[maxRow][maxCol];
        reset();
	}
	ChessBoard() {
		board = new ChessType[maxRow][maxCol];
        reset();
	}	
	
	public ChessType getPlayer() {
		
		return player;
	}
	

	public ChessType getBoardStatus(int r, int c) {
		assert r >= 0 && r<=maxCol && c>=0 && c<=maxCol;
		return board[r][c];
	}
	
	public ChessType getWinner(){
		return isWin;
	}

    public void reset() {
        for (int r=0; r<maxRow; r++) {
            for (int c=0; c<maxCol; c++) {
                board[r][c] = ChessType.EMPTY;
            }
        }
        moves = 0;  // No moves so far.
        isWin = ChessType.EMPTY;	// So far no winner
        player = ChessType.BLACK;  
    }
    public int move(int r, int c) {
        assert board[r][c] == ChessType.EMPTY;	//make sure (r, c) is empty
        board[r][c] = player;  	// Record this move.
        player = ChessType.nextType(player);
        return QScore(r, c, ChessType.nextType(player));

    }
    
    public int move(Point p){
    	return move(p.x, p.y);
    }
    
    public int move(int r, int c, ChessType chessType) {
        assert board[r][c] == ChessType.EMPTY;	//make sure (r, c) is empty
        board[r][c] = chessType;  	// Record this move.
        
        this.player = ChessType.nextType(chessType);
        return QScore(r, c, chessType);

    }
    
    public void remove(int r, int c) {
        board[r][c] = ChessType.EMPTY;  	// Record this move.
    }
    
    public int move(Point point, ChessType chessType) {
    	
        return move(point.x, point.y, chessType);

    }
//    public void move(int r, int c) {
//        assert board[r][c] == ChessType.EMPTY;	//make sure (r, c) is empty
//        board[r][c] = player;  	// Record this move.
//        player = ChessType.nextType(player); 	// Flip players
//        moves++;                  // Increment number of moves.
//    }
    
    public int QScore(int r, int c, ChessType player) {
    	int score = 0;
    	assert board[r][c] == ChessType.EMPTY;	//make sure (r, c) is empty
    	//board[r][c] = player;	//put player on the position to make calculation
    	for (int row = 0; row < maxRow; row++) {
    		for (int col = 0; col < maxCol; col++) {
    			ChessType p = board[row][col];
    			if (p != ChessType.EMPTY) {
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
    	
    	//board[r][c] = ChessType.EMPTY;	//recover the board
    	return score;
    }
    
    
    private int countScore(int r, int c, int dr, int dc, ChessType player) {
    	int count = 1;
        for (int i=1; i<5; i++) {
        	if(board[r+dr*i][c+dc*i] != ChessType.nextType(player)) {
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
    
    
    @Override
    public ChessBoard clone() {
    	ChessBoard clonedChessboard = new ChessBoard();
	for (int row = 0; row < maxRow; ++row) {
	    for (int column = 0; column < maxRow; ++column) {
	    	clonedChessboard.move(row, column, this.board[row][column]);
	    }
	}
	return clonedChessboard;
    }
}
