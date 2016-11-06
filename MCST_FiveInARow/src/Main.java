import java.util.ArrayList;

public class Main {
	private static Node root;
	
	public static void main(String args) {
		init();
		
		while(true) {
			selection();
			expansion();
			simulation();
			backpropagation();
		}
	}
	
	private static void init() {
		
	}
	
	private static Node selection() {
		Node sel = root;
		while(!sel.getChildren().isEmpty()) {
			ArrayList<Node> children = sel.getChildren();
			sel = children.get(0);
			for(Node child : children) {
				if(child.getValue().greaterThan(sel.getValue())) {
					sel = child;
				}
			}
		}
		
		return sel;
	}
	
	private static void expansion() {
		
	}
	
	private static void simulation() {
		
	}
	
	private static void backpropagation() {
		
	}
}
