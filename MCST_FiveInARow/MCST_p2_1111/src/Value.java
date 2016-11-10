
public class Value {
	public double win = 0;
	public int total = 0;
	//to prevent /0
	private static double Delta = 0.00000001;
	
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
		return win / (total+Delta)
				+ Math.sqrt(2 * Math.log(numOfAllGame) / (total+Delta));
	}
}
