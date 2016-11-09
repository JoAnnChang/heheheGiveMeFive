import java.awt.Point;
import java.util.ArrayList;
import java.util.Scanner;




public class Main {
	private ChessBoard chessBoard;
	public enum Alpha{
		a,b,c,d,e,f,g,h,i,j,k,l,m,n,o;
		public static String getAlpha(int num){
			Character character = 'a';
			character = (char) (character+num);
			return character.toString();
		}
	}
	
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {

		ChessType chessType = ChessType.BLACK;
		
		int count = 200;
		MCTSThread mctsThread;
		
		init();
		
		while(true) {
			//input 
			Scanner scanner = new Scanner(System.in);
			String input = scanner.nextLine();
			
			if(input.equals("show")){
				showChessboard(chessBoard);
				continue;
			}
			
			String[] text = input.split(" ");
			int r = 15 - Integer.valueOf(text[0]);
			int c = Alpha.valueOf(text[1]).ordinal();
			Point p = new Point(r, c);
			System.out.println("ME: "+text[0]+", "+text[1]);
			
			if(chessBoard.move(p, ChessType.BLACK) == 1){
				System.out.println("BLACK WIN");
				showChessboard(chessBoard);
				break;
			}
			
			
			// mcts thread
			ChessBoard chessBoard_tmp = chessBoard.clone();
			mctsThread = new MCTSThread(chessBoard_tmp, new Node(null, ChessType.WHITE, p));
			Point point = mctsThread.next();
			
			if(chessBoard.move(p, ChessType.BLACK) == 1){
				System.out.println("BLACK WIN");
				showChessboard(chessBoard);
				break;
			}
			
			int r2 = point.x;
			String cc = Alpha.getAlpha(point.y);
			System.out.println("WHITE: "+String.valueOf(15 - r2)+", "+cc);
			
			if(chessBoard.move(point, ChessType.WHITE) == 1){
				System.out.println("WHITE WIN");
				showChessboard(chessBoard);
				break;
			}
		
		}
	}
	
	public static void showChessboard(ChessBoard chessboard){
		for(int i=0; i<chessboard.maxRow; i++){
			for(int j=0; j<chessboard.maxCol; j++){
				//System.out.print(chessboard.getChess(i, j));
				if(chessboard.getBoardStatus(i, j) == ChessType.BLACK){
					System.out.print("O");
				}
				else if(chessboard.getBoardStatus(i, j) == ChessType.WHITE){
					System.out.print("X");
				}
				else if(chessboard.getBoardStatus(i, j) == ChessType.EMPTY){
					System.out.print("-");
				}
			}
			System.out.println("");
		}
		System.out.println("");
	}
	
	private Node input() {
		/*
		 * format ex: [Black (H,7)]
		 */
		
		return null;
	}
	
	private void output(Node n) {
		/*
		 * format ex: [Black (H,7)]
		 */
	}
	
	private void init() {
		chessBoard = new ChessBoard();
	}
	
	
}
