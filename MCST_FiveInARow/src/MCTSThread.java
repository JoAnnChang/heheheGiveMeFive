import java.util.ArrayList;

public class MCTSThread extends Thread{
	private Node root;
	
	public MCTSThread(ChessBoard cb) {
		super();
	}
	
	public void run() {
		while(true) {
			Node sel = selection();
			
			ArrayList<Node> nodes = expansion(sel);
			
			for(Node n : nodes) {
				ChessBoard.ChessType result = simulation(n);
				backpropagation(n);	
			}
		}
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
	
	private Node selection() {
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
	
	private ArrayList<Node> expansion(Node n) {
		return null;
	}
	
	// return chess type. 1->player1, 2->player2
	private ChessBoard.ChessType simulation(Node n) {
		return ChessBoard.ChessType.EMPTY;
	}
	
	private void backpropagation(Node n) {
		// select best node at same time
	}
}
