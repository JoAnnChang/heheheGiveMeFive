import java.util.ArrayList;

public class Node {
	private Node parent;
	private ArrayList<Node> children;
	private Value value;
	private int r, c;
	
	public Node(Node parent, int r, int c) {
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.value = new Value();
		this.r = r;
		this.c = c;
	}
	
	public boolean addchild(Node child) {
		return this.children.add(child);
	}
	
	public ArrayList<Node> getChildren(){
		return this.children;
	}
	
	public Value getValue() {
		return this.value;
	}
}
