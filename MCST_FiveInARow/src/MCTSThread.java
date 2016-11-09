import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import javax.swing.border.EmptyBorder;


public class MCTSThread extends Thread{
	private Node root;
	private ChessBoard chessBoard;
	private ChessType winType;
	
	private static final int SEARCH_RANGE = 1;
	
	public MCTSThread(ChessBoard cb, Node currentNode) {
		super();
		this.chessBoard = cb;
		this.root = currentNode;
	}
	
	public void run() {
	}
	
	public Point next(){
		//Add timer count, when come to 10, stops
		
		//Temporary timer, do 1000 times
		int timer = 50000;
		System.out.println(root.getChesstype());
		while(timer-- > 0) {
			
			//We will use the chessBoard many and many times,
			//and every time we shoud assure it would be the original one
			ChessBoard chessBoard_tmp = chessBoard.clone();
			
			//-----------------------------//
			//------START select-----------//
			//-----------------------------//
			
			//A win type to check if the selection already get a win move,  
			// then there is no need to do next, just back propagate. 
			winType = ChessType.EMPTY;
			//Always Start from the root
			Node selected = select(root, chessBoard_tmp);
			
//			if(winType != ChessType.EMPTY){
//				backpropagate(selected, root.getChesstype()==winType);
//				continue;
//			}
			
			//-----------------------------//
			//------START expand-----------//
			//-----------------------------//
			//pass the selected node, the selected node will grow a list of children
			//then return the list
			ArrayList<Node> childNodes = expand(selected, chessBoard_tmp);
			
			
			//-----------------------------//
			//------START simulate---------//
			//-----------------------------//
			//Select N child nodes to simulate, but here we only do once for testing.
			Node secondSelected = select(selected, chessBoard_tmp);
			//pass the second Selected node, return a result that the node will win or not
			ChessType result = simulate(secondSelected, chessBoard_tmp);

			//-----------------------------//
			//------START simulate---------//
			//-----------------------------//
			//Will do N times, but here we only do once for testing
			//pass the second selected node and the result to back propagate
			backpropagate(secondSelected, (result==root.getChesstype()));
			
			
			//START backpropagate
			//backpropagate(backpropagateTrack);	
//			for(Node n : nodes) {
//				
//				ChessType result = simulate(n);
//				backpropagate(n);	
//			}
			
			
			
		}
		
		printTree(root);
		
		
		Node returnNode = getBest();
		return returnNode.getPoint();
	}
	
	public void printTree(Node root){
		System.out.println(root.getChildren().size());
		ArrayList<Node> children = root.getChildren();
		for(Node n: children){
			System.out.printf("(%f/%d)/%d  ", n.getNumOfWin(), n.getNumOfSelected(), n.getChildren().size());
		}
		ArrayList<Node> list = new ArrayList<Node>();
		System.out.print("\n4rdNodeSize: ");
		System.out.println(root.getChildren().get(0).getChildren().get(0).getChildren().get(0).getChildren().size());
		
		
	}
	
    /**
     * At last, to decide the best move, 
     * compare every child node of root.
     * Return one with best win/num
     *
     * @return Node : the child with best move
     */
	public Node getBest() {
		Node sel = root.getChildren().get(0);
		
		for(Node n : root.getChildren()) {
			if(n.getValue().greaterThan(sel.getValue())) {
				sel = n;
			}
		}
		
		System.out.println("the best of numOfSelected: "+String.valueOf(sel.getNumOfSelected()));
		return sel;
	}
	
    /**
     * Select 
     * 
     * recursivly select the child with the best potential.
     * 
     * @param the node to start the select
     * @param the chessBoard  
     * @return Node : the child with the best potential
     */
	int c = 1;
	
	private Node select(Node currentNode, ChessBoard chessBoard) {
		c++;
		

		Node selected = currentNode;
		ChessType currentChessType = currentNode.getChesstype();
		
		int count = 0;
		while(!selected.getChildren().isEmpty()) {
			count++;

			ArrayList<Node> children = selected.getChildren();
			//get the child node with the biggest value
			double max_ucb = 0;
			int index_max_ucb = 0;
			
			for(Node child : children) {
				double tmp_ucb = child.getUCB(selected.getNumOfSelected());
				//if tmp_ucb > max_ucb is true, replace it 
				if( Double.compare(tmp_ucb, max_ucb) == 1){
					max_ucb = tmp_ucb;
					index_max_ucb = children.indexOf(child);
				}
			}
			
			selected = children.get(index_max_ucb);
			
			if (chessBoard.move(selected.getPoint(), currentChessType) == 1) {
				winType = currentChessType;
				break;
			 }
			
			//change the type
			currentChessType = ChessType.nextType(currentChessType);
		}
//		if(c%2 == 0)
//			System.out.println("--- "+count);

		return selected;
	}
	
    /**
     * Expand 
     * 
     * expand the children node of the parent node,
     * we first search around the current chessboard
     * 
     * @param the parent node to expand
     * @return Node : the child with the best potential
     */
	private ArrayList<Node> expand(Node parentNode, ChessBoard chessBoard) {
		
		//get a list of legal moves
		ArrayList<Point> legalMoves_list = searchTheLegalMoves(chessBoard);
		//the list of child nodes with legal moves
		ArrayList<Node> legalChildNodes = new ArrayList<Node>();
		
		//the type of the node
		ChessType currentChessType = parentNode.getChesstype();
		

		//turn the hash set to the arraylist
		for(Point point : legalMoves_list){
//			ChessBoard cb_tmp = chessBoard.clone();
//			cb_tmp.move(point, currentChessType);
			Node newChildNode = new Node(parentNode, currentChessType, point);
			
			//parent node add the child
			legalChildNodes.add(newChildNode);
			
			currentChessType = ChessType.nextType(currentChessType);
		}

		//add all the new child nodes to the paren node
		parentNode.addAllChild(legalChildNodes);
		
		return legalChildNodes;
	}
	
	
	private ArrayList<Point> searchTheLegalMoves(ChessBoard chessBoard){
		//for each point(not empty), put a range of its neignbor into arraylist
		//a hash set(no duplicate) to record all the legalMoves(empty place, not too far)
		HashSet<Point> legalMoves = new HashSet<Point>();
		
		
		//search all the none empty place to expand 
		//to prevent the move goes too far than the exists, we set a range=3
		for(int i=0; i<chessBoard.maxRow; i++){
			for(int j=0; j<chessBoard.maxCol; j++){
				if(chessBoard.getBoardStatus(i, j) != ChessType.EMPTY){
					legalMoves.addAll(searchTheRange(chessBoard, i, j));
				}
			}
		}
		return new ArrayList<Point>(legalMoves);
		
	}
	private ArrayList<Point> searchTheRange(ChessBoard chessBoard, int r, int c){
		int minR = Math.max(0, r-SEARCH_RANGE);
		int maxR = Math.min(chessBoard.maxRow-1, r+SEARCH_RANGE);
		int minC = Math.max(0, c-SEARCH_RANGE);
		int maxC = Math.min(chessBoard.maxRow-1, c+SEARCH_RANGE);
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
	
    /**
     * Simulate 
     * 
     * simualte the node will finally win or not
     * 
     * @param node to simulate
     * @param chessboard
     * @return ChessType: who wins?
     */
	private ChessType simulate(Node node, ChessBoard chessBoard) {
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
		    
		    if (chessBoard.move(chosePoint, chessType) == 1) {
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
