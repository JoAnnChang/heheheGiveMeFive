import java.awt.Point;
import java.lang.reflect.Array;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.border.EmptyBorder;



public class MCTSThread extends Thread{
	private Node root;
	private ChessBoard chessBoard;
	private ChessType winType;
	
	private static int SEARCH_RANGE = 2;
	private static int SIMU_RANGE = 5;
	private static int MAX_SIMULAYER = 5;
	private static int timeLimit = 10;
//	private int timer = 100000;
	
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
//		int timer = 50000;
		
		Timer timer = new Timer(true);
		TimeCount timeCount = new TimeCount();
		timer.schedule(new TimeCount(), 0, 1000);
		int start = timeCount.getTimeCount();
		if(root.getPoint().x == 7 && root.getPoint().y==7){
			SEARCH_RANGE = 1;
		}
	
		int count =0;
		
		while(true) { 
			count++;
			int end = timeCount.getTimeCount();
			if(end - start == timeLimit){
				start = end;
				break;
			}
			
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
			if(winType != ChessType.EMPTY){
				backpropagate(selected, root.getChesstype()==winType);
				continue;
			}
			
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
//			Node secondSelected = select(selected, chessBoard_tmp);
			Node secondSelected = childNodes.get((new Random()).nextInt(childNodes.size()));
			if(winType != ChessType.EMPTY){
				backpropagate(secondSelected, root.getChesstype()==winType);
				continue;
			}
			
			//pass the second Selected node, return a result that the node will win or not
			ChessType result = simulate_greedy(secondSelected, chessBoard_tmp, secondSelected);
			System.out.println("simulation");

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
		System.out.println("while loop go "+count+" times");
		printTree(root);
		timer.cancel();
		Node returnNode = getBest();
		return returnNode.getPoint();
	}
	
	public void printTree(Node root){
		System.out.println("root childern size:" + root.getChildren().size());
		ArrayList<Node> children = root.getChildren();
		for(Node n: children){
			System.out.printf("(%f/%d)/%d  ", n.getNumOfWin(), n.getNumOfSelected(), n.getChildren().size());
		}
		System.out.println("\nDepth: "+maxCount);
		ArrayList<Node> list = new ArrayList<Node>();
		
		
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
		
		System.out.printf("(%f/%d)/%d  ", sel.getNumOfWin(), sel.getNumOfSelected(), sel.getChildren().size());
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
	int maxCount = 0;
	
	private Node select(Node currentNode, ChessBoard chessBoard) {
		
		

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
		if(count > maxCount){
			maxCount = count;
		}
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
		currentChessType = ChessType.nextType(currentChessType);

		//turn the hash set to the arraylist
		int count = 0;
		for(Point point : legalMoves_list){
//			ChessBoard cb_tmp = chessBoard.clone();
//			cb_tmp.move(point, currentChessType);
			Node newChildNode = new Node(parentNode, currentChessType, point);
			
			//parent node add the child
			legalChildNodes.add(newChildNode);
			
//!!!!!!!!!!!BUG			
//			currentChessType = ChessType.nextType(currentChessType);
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
		return searchTheRange(chessBoard, r, c, SEARCH_RANGE);
	}
	private ArrayList<Point> searchTheRange(ChessBoard chessBoard, int r, int c, int search_range){
		int minR = Math.max(0, r-search_range);
		int maxR = Math.min(chessBoard.maxRow-1, r+search_range);
		int minC = Math.max(0, c-search_range);
		int maxC = Math.min(chessBoard.maxRow-1, c+search_range);
		ArrayList<Point> aroundList = new ArrayList<Point>();
		
//		for(int i=minR; i<=maxR; i++){
//			for(int j=minC; j<=maxC; j++){
//				if(chessBoard.getBoardStatus(i, j) == ChessType.EMPTY){
//					aroundList.add(new Point(i,j));
//				}
//			}
//		}
		for(int i=minR; i<=maxR; i++){
			if(chessBoard.getBoardStatus(i, c) == ChessType.EMPTY){
				aroundList.add(new Point(i,c));
			}
		}
		for(int j=minC; j<=maxC; j++){
			if(chessBoard.getBoardStatus(r, j) == ChessType.EMPTY){
				aroundList.add(new Point(r,j));
			}
		}
		for(int i=r-search_range, j=c-search_range; i<=r+search_range && j<=c+search_range; i++,j++){
			if(i>=0 && i<15 && j>=0 && j<15){
				if(chessBoard.getBoardStatus(i, j) == ChessType.EMPTY){
					aroundList.add(new Point(i,j));
				}
			}

		}
		for(int i=r-search_range, j=c+search_range; i<=r+search_range && j>=c-search_range; i++,j--){
			if(i>=0 && i<15 && j>=0 && j<15){
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
	
	private ChessType simulate_greedy(Node node, ChessBoard chessBoard, Node root) {
		if(node.simuLayer < MAX_SIMULAYER && !chessBoard.isWin(chessBoard, node.getChesstype(), node.getPoint())) {
			node.addAllChild(findPossibleMove(node, chessBoard));
			
			for(Node c : node.getChildren()){
				c.simuLayer = node.simuLayer + 1;
//				System.out.println(c.simuLayer + "");
				
				chessBoard.move(c.getPoint(), c.getChesstype());
				simulate_greedy(c, chessBoard, root);
				chessBoard.remove(c.getPoint());
			}
		}
		
		if(node.getChildren().size() == 0) {
			chessBoard.remove(node.getPoint());
			root.Q += chessBoard.QScore(node.getPoint().x, node.getPoint().y, node.getChesstype()).getScore();
			chessBoard.move(node.getPoint(), node.getChesstype());
		}
		
		if(node == root) {
			return (root.Q >= 0) ? root.getChesstype() : ChessType.nextType(root.getChesstype());
		}
		else {
			return null;
		}
	}
	
	private ArrayList<Node> findPossibleMove(Node parent, ChessBoard chessBoard) {
		ArrayList<Node> possible = new ArrayList<Node>();
		
		for(int i=0; i<chessBoard.maxRow; i++) {
			for(int j=0; j<chessBoard.maxCol; j++) {
				if(chessBoard.getBoardStatus(i,j) == ChessType.EMPTY) {
					possible.add(new Node(parent, ChessType.nextType(parent.getChesstype()), i, j));
				}
			}
		}
		
		for(Node node : possible) {
//			System.out.println(node.getPoint().x + " " + node.getPoint().y + " " + node.getChesstype());
			node.Q = chessBoard.QScore(node.getPoint().x, node.getPoint().y, node.getChesstype()).getScore();
		}
		possible.sort(new Comparator<Node>(){
			@Override
			public int compare(Node o1, Node o2) {
				return o1.Q > o2.Q ? 0 : 1;
			}
		});
		while(possible.size() > SIMU_RANGE) {
			possible.remove(possible.size()-1);
		}
		
		return possible;
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
					legalMoves_set.addAll(searchTheRange(chessBoard, i, j, 1));
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
