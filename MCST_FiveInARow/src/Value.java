
public class Value {
	private int win = 0;
	private int total = 0;
	
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
}
