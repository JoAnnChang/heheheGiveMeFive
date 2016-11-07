import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.border.EmptyBorder;

public class MCTSThread extends Thread{
	private Node root;
	private ChessBoard chessBoard;
	private ChessType winType;
	private Object lock;
	
	private static final int SEARCH_RANGE = 3;
	
	public MCTSThread(ChessBoard cb, Node currentNode) {
		super();
		this.chessBoard = cb;
		this.root = currentNode;
		this.lock = new Object();
	}
	
	public void run() {
		//Add timer count, when come to 10, stops
		//Temp:
		int timer = 10000;
		while(timer-- > 0) {
			//to memorize the backpropagate path

			//START select
			//Always Start from the root
			winType = ChessType.EMPTY;
			Node selected = select(root, chessBoard);
			
			
			if(winType != ChessType.EMPTY){
				
			}
			
			//START expand
			//pass into the selected node, return the result of the children of the node
			ArrayList<Node> nodes = expand(selected);
			
			//START simulate 
			//will be n times... here just simulate 1 time to test
			Node secondSelected = select(selected, chessBoard);
			ChessType result = simulate(secondSelected);

			
			backpropagate(secondSelected, (result==root.getChesstype()));
			//START backpropagate
			//backpropagate(backpropagateTrack);	
//			for(Node n : nodes) {
//				
//				ChessType result = simulate(n);
//				backpropagate(n);	
//			}
			
			
			
		}
		Node returnNode = getBest();
	}
	
	public Node getBest() {
		Node sel = root.getChildren().get(0);
		
		for(Node n : root.getChildren()) {
			if(n.getValue().greaterThan(sel.getValue())) {
				sel = n;
			}
		}
		
		return sel;
	}
	
	public Point getOutput() {
		try {
            sleep(10 * 1000 - 100);
        } 
		catch (InterruptedException e) {}
		
		synchronized(lock) {
			return getBest().getPoint();
		}
	}
	
	private Node select(Node currentNode, ChessBoard chessBoard) {
		
		Node selected = currentNode;
		ArrayList<Node> children = selected.getChildren();
		ChessType currentChessType = currentNode.getChesstype();
		
		while(!selected.getChildren().isEmpty()) {
			
			//get the child node with the biggest value
			selected = children.get(0);
			
			for(Node child : children) {
				if(child.getValue().greaterThan(selected.getValue())) {
					selected = child;
				}
			}
			
			if (chessBoard.move(selected.getPoint(), currentChessType) == chessBoard.FIVE) {
				winType = currentChessType;
				break;
			 }
			
			//change the type
			currentChessType = ChessType.nextType(currentChessType);
		}
		
		return selected;
	}
	
	private ArrayList<Node> expand(Node parentNode) {
		//for each point(not empty), put a range of its neignbor into arraylist
		ChessBoard chessBoard = parentNode.getChessBoard();
		//a hash set(no duplicate) to record all the legalMoves(empty place, not too far)
		HashSet<Point> legalMoves = new HashSet<Point>();
		//turn the hash set to the arraylist
		ArrayList<Node> legalChildNodes = new ArrayList<Node>();
		
		//the type of the node
		ChessType type = parentNode.getChesstype();
		
		//search all the none empty place to expand (to prevent the move goes too far than the exists)
		for(int i=0; i<chessBoard.maxRow; i++){
			for(int j=0; j<chessBoard.maxCol; j++){
				if(chessBoard.getBoardStatus(i, j) != ChessType.EMPTY){
					legalMoves.addAll(searchTheRange(chessBoard, i, j));
				}
			}
		}
		
		//turn the hash set to the arraylist
		for(Point point : legalMoves){
			ChessBoard cb_tmp = chessBoard.clone();
			Node newChildNode = new Node(parentNode, cb_tmp, point);
			
			//parent node add the child
			legalChildNodes.add(newChildNode);
		}
		parentNode.addAllChild(legalChildNodes);
		
		return legalChildNodes;
	}
	
	private ArrayList<Point> searchTheRange(ChessBoard chessBoard, int r, int c){
		int minR = Math.max(0, r-SEARCH_RANGE);
		int maxR = Math.min(chessBoard.maxRow, r+SEARCH_RANGE);
		int minC = Math.max(0, c-SEARCH_RANGE);
		int maxC = Math.min(chessBoard.maxRow, c+SEARCH_RANGE);
		ArrayList<Point> aroundList = new ArrayList<Point>();
		
		for(int i=minR; i<=maxR; i++){
			for(int j=minC; j<=maxC; j++){
				if(chessBoard.getBoardStatus(i, j) == ChessType.EMPTY){
					aroundList.add(new Point(i,j));
				}
			}
		}
		
		return aroundList;
		
	}
	private ArrayList<Point> searchTheRange(ChessBoard chessBoard, Point point){
		return searchTheRange(chessBoard, point.x, point.y);
	}
	
	// return chess type. 1->player1, 2->player2
	private ChessType simulate(Node node) {
		ChessBoard chessBoard = node.getChessBoard();
		HashSet<Point> legalMoves = new HashSet<Point>();
		ArrayList<Node> legalChildNodes = new ArrayList<Node>();
		
		//the type of the node
		ChessType type = node.getChesstype();
		
		for(int i=0; i<chessBoard.maxRow; i++){
			for(int j=0; j<chessBoard.maxCol; j++){
				if(chessBoard.getBoardStatus(i, j) != ChessType.EMPTY){
					legalMoves.addAll(searchTheRange(chessBoard, i, j));
				}
			}
		}
		for(Point point : legalMoves){
			ChessBoard cb_tmp = chessBoard.clone();
			legalChildNodes.add(new Node(node, cb_tmp, point));
		}
		
		
		return ChessType.EMPTY;
	}
	
	private void backpropagate(Node node, boolean isWin) {
		// select best node at same time
		
		// lock for main thread
		synchronized(lock) {
			//update until the root (the parent of root is null)
			while(node.getParent() != null){
				node.updateStatus(isWin);
				node = node.getParent();
			}
			
			//update the root
			node.updateStatus(isWin);
		}
		
	}
}
