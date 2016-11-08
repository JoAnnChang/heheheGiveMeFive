
public class Value {
	private int win = 0;
	private int total = 0;
	private double ucb = 0;
	
	public Value(){
		this.win = 0;
		this.total = 0;
	}
	
	public void addTotal(int num){
		this.total += num;
	}
	
	public void addWin(int num){
		this.win += num;
	}
	
	
	public double set(int win, int total) {
		this.win = win;
		this.total = total;
		return (double)win/(double)total;
	}
	
	public double toDouble() {
		return (double)win/(double)total;
	}
	
	public boolean greaterThan(Value v) {
		
		if(this.toDouble() > v.toDouble()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public double getUCB(int numOfAllGame){
		return win / total
				+ Math.sqrt(2 * Math.log(numOfAllGame) / (total));
	}
}
