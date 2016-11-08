import java.awt.Point;
import java.time.chrono.ThaiBuddhistEra;
import java.util.ArrayList;

public class Node {
	private Node parent;
	private ArrayList<Node> children;
	private Value value;
	private ChessBoard chessBoard;
	//The conponent's step on the previous round.
	private Point point;
	
	public Node(Node parent,ChessBoard chessBoard,  int r, int c) {
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.chessBoard = chessBoard;
		this.value = new Value();
		this.point = new Point(r, c);
		
	}
	
	public Node(Node parent,ChessBoard chessBoard,  Point point) {
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.chessBoard = chessBoard;
		this.value = new Value();
		this.point = point;
	}
	
	
	public boolean addAllChild(ArrayList<Node> children){
		for(Node node : children){
			if(!addchild(node)){
				return false;
			}
		}
		
		return true;
	}
	public boolean addchild(Node child) {
		return this.children.add(child);
	}
	
	public ArrayList<Node> getChildren(){
		return this.children;
	}
	
	public Point getPoint(){
		return point;
	}
	public Node getParent(){
		return this.parent;
	}
	
	public Value getValue() {
		return this.value;
	}
	
	
//	public ChessBoard getChessBoard(){
//		return chessBoard;
//	}
	
	public ChessType getChesstype(){
		return this.chessBoard.getPlayer();
	}
	
	//numOfAllGame is from the parent
	public double getUCB(int numOfAllGame){
		return this.value.getUCB(numOfAllGame);
	}
	
	
    public void updateStatus(boolean isWin) {
    	this.value.addTotal(1);

    	if(isWin){
    		this.value.addWin(chessBoard.FIVE);
    	}

    	
    }
}
