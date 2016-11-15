import java.awt.Point;
import java.time.chrono.ThaiBuddhistEra;
import java.util.ArrayList;

public class Node {
	private Node parent;
	private ArrayList<Node> children;
	private Value value;
	private ChessType chessType;
	//The conponent's step on the previous round.
	private Point point;
	
	public int simuLayer;
	public double Q;
	
	public Node(Node parent,  ChessType chessType, int r, int c) {
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.chessType = chessType;
		this.value = new Value();
		this.point = new Point(r, c);
		
		this.simuLayer = 0;
	}
	
	public Node(Node parent,  ChessType chessType, Point point) {
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.chessType = chessType;
		this.value = new Value();
		this.point = point;
		
		this.simuLayer = 0;
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
		return chessType;
	}
	
	//numOfAllGame is from the parent
	public double getUCB(int numOfAllGame){
		return this.value.getUCB(numOfAllGame);
	}
	
	public int  getNumOfSelected(){
		return this.value.total;
	}
	public double  getNumOfWin(){
		return this.value.win;
	}
	
	
    public void updateStatus(Boolean winAdd) {
    	this.value.addTotal(1);
    	if(winAdd){
    		this.value.addWin(1);
    	}
    	else 
    		this.value.addWin(0);
   		

    	
    }
}
