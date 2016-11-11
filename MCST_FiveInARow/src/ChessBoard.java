import java.awt.Point;
import java.util.Scanner;

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
	
	public static int maxRow = 15;
	public static int maxCol = 15;
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

    
    public int move(int r, int c, ChessType chessType) {
        assert board[r][c] == ChessType.EMPTY;	//make sure (r, c) is empty
        board[r][c] = chessType;  	// Record this move.
        
        this.player = ChessType.nextType(chessType);
        
        
        return isWin(this, chessType, new Point(r, c))==true? 1:0;
    }
    
    public int move(Point point, ChessType chessType) {
    	
        return move(point.x, point.y, chessType);

    }
    
    
    public static void main(String[] args){
    	ChessBoard chessBoard = new ChessBoard();
//    	showScore(new Point(7, 7), ChessType.BLACK);

    	ChessType chessType = ChessType.BLACK;
		Scanner scanner = new Scanner(System.in);
		Point old = new Point(0, 0);
		while(true){
			String input = scanner.nextLine();
			if(input.equals("back")){
				chessType = chessType.nextType(chessType);
				chessBoard.moveQ(old, ChessType.EMPTY);
				Main.showChessboard(chessBoard);
				continue;
			}
			
			String[] text = input.split(" ");
			int r = Main.Alpha.valueOf(text[0]).ordinal();
			int c = Integer.valueOf(text[1]);
			old = new Point(r, c);
			
	    	MyScore myScore = chessBoard.moveQ(old, chessType);
	    	System.out.println("Score: "+myScore.getScore()+", ATK: "+myScore.getAttackScore()+", DEF:"+myScore.getDefenseScore());
	    	Main.showChessboard(chessBoard);
	    		
	    	chessType = ChessType.nextType(chessType);
		}
		
    	
    	
    }
    
    public void showScore(Point p, ChessType chessType){
    	System.out.print(chessType.name()+"-("+p.x+", "+p.y+"): ");
    	System.out.println(this.moveQ(p, chessType));
    }
    public MyScore moveQ(int r, int c, ChessType chessType) {
		assert board[r][c] == ChessType.EMPTY; // make sure (r, c) is empty
		board[r][c] = chessType; // Record this move.
		return QScore(this.board, chessType);

	}

	public MyScore moveQ(Point point, ChessType chessType) {

		return moveQ(point.x, point.y, chessType);

	}
	
	/*Show score when put a player chess on (r, c)*/
	public MyScore QScore(int r, int c, ChessType player) {	
		assert board[r][c] == ChessType.EMPTY; // make sure (r, c) is empty
		board[r][c] = player; //put player on the position to make
		MyScore tmp = QScore(this.board, player);
		board[r][c] = ChessType.EMPTY;
		return tmp;
	}
	/*count score with parameters board and player*/
	public static MyScore QScore(ChessType[][] board2, ChessType player) {
		MyScore tmpScore;
		MyScore score = new MyScore();
		for (int row = 0; row < maxRow; row++) {
			for (int col = 0; col < maxCol; col++) {
				// look at 4 kinds of direction
				// 1. a column going up
				// 2. a row going to the right
				// 3. a diagonal up and to the right
				// 4. a diagonal up and to the left
				if (row < maxRow - 4) {// Look up
					tmpScore = countScore(row, 1, col, 0, player, board2);
					score.refresh(tmpScore);
				}

				if (col < maxCol - 4) { // row to right
					tmpScore = countScore(row, 0, col, 1, player, board2);
					score.refresh(tmpScore);
					if (row < maxRow - 4) { // diagonal up to right
						tmpScore = countScore(row, 1, col, 1, player, board2);
						score.refresh(tmpScore);
					}
				}

				if (col > 3 && row < maxRow - 4) { // diagonal up left
					tmpScore = countScore(row, 1, col, -1, player, board2);
					score.refresh(tmpScore);
				}

			}
		}
		return score;
	}

	private static MyScore countScore(int r, int dr, int c, int dc, ChessType player, ChessType[][] b) {
		MyScore tmpScore = new MyScore();
		int posCount = 1;
		int negCount = 1;
		int posFlag = 1;
		int negFlag = 1;
		/* count same ChessType */
		for (int i = 1; i < 5; i++) {
			
			if (b[r + dr * i][c + dc * i] != ChessType.nextType(player)
					&& posFlag == 1) {
				
				if (b[r + dr * i][c + dc * i] == player)
					posCount++;
			} else {
				posFlag = 0;
			}
			if (b[r + dr * i][c + dc * i] != player && negFlag == 1) {
				if (b[r + dr * i][c + dc * i] == player)
					negCount++;
			} else {
				negFlag = 0;
			}
		}

		/* count attackScore */
		if (posCount == 2) {
			tmpScore.a_two++;
		} else if (posCount == 3) {
			if (r - dr * 1 > 0 && c - dc * 1 > 0) {
				if (b[r - dr * 1][c - dc * 1] == ChessType.EMPTY) {
					tmpScore.a_h_three++;
				}
			} else if (r + dr * 5 < maxRow && c + dc * 5 < maxCol) {
				if (b[r + dr * 5][c + dc * 5] == ChessType.EMPTY) {
					tmpScore.a_h_three++;
				}
			} else {
				tmpScore.a_three++;
			}
		} else if (posCount == 4) {
			if (r - dr * 1 > 0 && c - dc * 1 > 0) {
				if (b[r - dr * 1][c - dc * 1] == ChessType.EMPTY) {
					if(b[r][c]==player && b[r+dr*4][c+dc*4]==player){
						tmpScore.a_four++;
					}
					else{
						tmpScore.a_h_four++;
					}
					
				}
			} else if (r + dr * 5 < maxRow && c + dc * 5 < maxCol) {
				if (b[r + dr * 5][c + dc * 5] == ChessType.EMPTY) {
					if(b[r][c]==player && b[r+dr*4][c+dc*4]==player){
						tmpScore.a_four++;
					}
					else{
						tmpScore.a_h_four++;
					}
				}
			} else {
				tmpScore.a_four++;
			}
		} else if (posCount == 5) {
			tmpScore.a_five++;
		}

		/* count negScore */
		if (negCount == 2) {
			tmpScore.d_two++;
		} else if (negCount == 3) {
			if (r - dr * 1 > 0 && c - dc * 1 > 0) {
				if (b[r - dr * 1][c - dc * 1] == ChessType.EMPTY) {
					tmpScore.d_h_three++;
				}
			} else if (r + dr * 5 < maxRow && c + dc * 5 < maxCol) {
				if (b[r + dr * 5][c + dc * 5] == ChessType.EMPTY) {
					tmpScore.d_h_three++;
				}
			} else {
				tmpScore.d_h_three++;
			}
		} else if (negCount == 4) {
			if (r - dr * 1 > 0 && c - dc * 1 > 0) {
				if (b[r - dr * 1][c - dc * 1] == ChessType.EMPTY) {
					if(b[r][c]==ChessType.nextType(player) && b[r+dr*4][c+dc*4]==ChessType.nextType(player)){
						tmpScore.d_four++;
					}
					else{
						tmpScore.d_h_four++;
					}
				}
			} else if (r + dr * 5 < maxRow && c + dc * 5 < maxCol) {
				if (b[r + dr * 5][c + dc * 5] == ChessType.EMPTY) {
					if(b[r][c]==ChessType.nextType(player) && b[r+dr*4][c+dc*4]==ChessType.nextType(player)){
						tmpScore.d_four++;
					}
					else{
						tmpScore.d_h_four++;
					}
				}
			} else {
				tmpScore.d_four++;
			}
		} else if (negCount == 5) {
			tmpScore.d_five++;
		}

		return tmpScore;
	}
    
    
    
    
   
    
    
    //-------------------------------------------//
    
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