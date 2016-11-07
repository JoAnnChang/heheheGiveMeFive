import java.util.ArrayList;

public class Main {
	private ChessBoard chessBoard;
	
	public static void main(String args) {
		new Main();
	}
	
	public Main() {
		MCTSThread mctsThread;
		
		init();
		
		while(true) {
			//input 
			Node n = input();
			
			
			// mcts thread
			mctsThread = new MCTSThread(chessBoard.clone(), new Node(null, chessBoard.clone(), 7, 7));
			mctsThread.start();
			
			//chessBoard.move(r, c, chessType);
			
			// start timer
			// loop
				// time check mcts thread
				// mctsThread.getBest();
				//
			// end loop
			
			// output
		}
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
