import java.awt.Point;

import javax.net.ssl.ExtendedSSLSession;

import org.omg.CORBA.PUBLIC_MEMBER;

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
//    public int move(int r, int c) {
//        assert board[r][c] == ChessType.EMPTY;	//make sure (r, c) is empty
//        board[r][c] = player;  	// Record this move.
//        player = ChessType.nextType(player);
//        return QScore(r, c, ChessType.nextType(player));
//
//    }
    
//    public int move(Point p){
//    	return move(p.x, p.y);
//    }
    
    public int move(int r, int c, ChessType chessType) {
        assert board[r][c] == ChessType.EMPTY;	//make sure (r, c) is empty
        board[r][c] = chessType;  	// Record this move.
        
        this.player = ChessType.nextType(chessType);
        
        
        return isWin(this, chessType, new Point(r, c))==true? 1:0;
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
    

    
    public boolean isWin(ChessBoard chessBoard, ChessType chessType, Point p){

    	int x = p.x;
    	int y = p.y;
    	int sameCounter = 0;
    	//check column
    	if(checkWin(chessBoard, chessType, x-4, x+4+1, 1, y, y+1, 0)){
    		return true;
    	}
      	if(checkWin(chessBoard, chessType, x, x+1, 0, y-4, y+4+1, 1)){
      		return true;
    	}
      	if(checkWin(chessBoard, chessType, x-4, x+4+1, 1, y-4,y+4+1, 1)){
    		return true;
    	}
      	if(checkWin(chessBoard, chessType, x-4, x+4+1, 1, y+4, y-4-1, -1)){
    		return true;
    	}
    	
    	return false;
    }

//    public static void main(String[] args){
//    	ChessBoard chessBoard = new ChessBoard();
//    	chessBoard.move(1,1, ChessType.WHITE);
//    	chessBoard.move(2,2, ChessType.WHITE);
//    	chessBoard.move(3,3, ChessType.WHITE);
//    	System.out.println(chessBoard.move(0,0, ChessType.WHITE));
//    }
    private boolean checkWin(ChessBoard chessBoard, ChessType chessType, 
    	int startX, int endX, int countX, int startY, int endY, int countY){

    	int sameCounter = 0;
    	    	
    	for(int i=startX, j=startY; i!=endX&&j!=endY; i+=countX, j+=countY){
    		if(i<0 || i>=15 || j<0 || j>=15){
    			continue;
    		}
    		if(chessBoard.getBoardStatus(i, j) != chessType){
    			sameCounter = 0;
    			continue;
    		}
    		sameCounter++;
    		if(sameCounter == 5)
    			return true;
//    		else if(sameCounter == 4){
//    			Point right_chance = new Point(i+countX, j+countY);
//    			Point left_chance = new Point(i-countX*4, j-countY*4);
//    			if((right_chance.x>=0 && right_chance.x<15 && right_chance.y>=0 && right_chance.y<15)
//    					&&(left_chance.x>=0 && left_chance.x<15 && left_chance.y>=0 && left_chance.y<15)){
//        			if(chessBoard.getBoardStatus(right_chance.x, right_chance.y)==ChessType.EMPTY
//        					&& chessBoard.getBoardStatus(left_chance.x, left_chance.y) == ChessType.EMPTY){
//        				
//        				return true;
//        			}
//    			}
//
//    		}
    	}
    	
		return false;
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
