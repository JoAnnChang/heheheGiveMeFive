import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.border.EmptyBorder;


public class MCTSThread extends Thread{
	private Node root;
	private ChessBoard chessBoard;
	private ChessType winType;
	
	private static final int SEARCH_RANGE = 3;
	
	public MCTSThread(ChessBoard cb, Node currentNode) {
		super();
		this.chessBoard = cb;
		this.root = currentNode;
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
	
	
	// return the winning chess type.
	private ChessType simulate(Node node) {
		ChessBoard chessBoard = node.getChessBoard();
		ChessType chessType = node.getChesstype();
		ChessType winType = ChessType.EMPTY;
		
		
		HashSet<Point> legalMoves_set = new HashSet<Point>();
		ArrayList<Point> legalMoves_list = new ArrayList<Point>();
		List<Point> choseList = new ArrayList<>();
		
		//the type of the node
		ChessType type = node.getChesstype();
		
		for(int i=0; i<chessBoard.maxRow; i++){
			for(int j=0; j<chessBoard.maxCol; j++){
				if(chessBoard.getBoardStatus(i, j) != ChessType.EMPTY){
					//to prevent repeat
					legalMoves_set.addAll(searchTheRange(chessBoard, i, j));
				}
			}
		}
		for(Point point : legalMoves_set){
			legalMoves_list.add(point);
		}
		
		
		while(!legalMoves_list.isEmpty()){
			Random random = new Random();
			int size = legalMoves_list.size();
			int randomIndex = random.nextInt(size);
			Point chosePoint = legalMoves_list.get(randomIndex);
		    choseList.add(chosePoint);
		    
		    if (chessBoard.move(chosePoint, chessType) == ChessBoard.FIVE) {
				winType = chessType;
				return winType;
		    }
		    legalMoves_list.remove(randomIndex);

		    List<Point> aroundList = searchTheRange(chessBoard, chosePoint.x, chosePoint.y);
		    for (Point point : aroundList) {
				if (!legalMoves_set.contains(point)) {
					legalMoves_set.add(point);
					legalMoves_list.add(point);
				}
		    }

		    chessType = ChessType.nextType(chessType);
		}
		
		
		return winType;
	}
	
	private void backpropagate(Node node, boolean isWin) {
		// select best node at same time
		
		//update until the root (the parent of root is null)
		while(node.getParent() != null){
			node.updateStatus(isWin);
			node = node.getParent();
		}
		
		//update the root
		node.updateStatus(isWin);
		
	}
}
