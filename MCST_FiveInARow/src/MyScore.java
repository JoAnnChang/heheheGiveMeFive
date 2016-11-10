public class MyScore {
	int a_two;
	int d_two;
	int a_three;
	int d_three;
	int a_four;
	int d_four;
	int a_h_three;
	int d_h_three;
	int a_h_four;
	int d_h_four;
	int a_five;
	int d_five;
	final static double aRatio = 1.2;
	final static double dRatio = 1.0;

	public MyScore() {
		a_two = 0;
		d_two = 0;
		a_three = 0;
		d_three = 0;
		a_four = 0;
		d_four = 0;
		a_h_three = 0;
		d_h_three = 0;
		a_h_four = 0;
		d_h_four = 0;
		a_five = 0;
		d_five = 0;
	}
	public void refresh(MyScore ms){
		this.a_five += ms.a_five;
		this.d_five += ms.d_five;
		this.a_four += ms.a_four;
		this.d_four += ms.d_four;
		this.a_h_four += ms.a_h_four;
		this.d_h_four += ms.d_h_four;
		this.a_three += ms.a_three;
		this.d_three += ms.d_three;
		this.a_h_three += ms.a_h_three;
		this.d_h_three += ms.d_h_three;
		this.a_two += ms.a_two;
		this.d_two += ms.d_two;
		return;
	}
	
	public int getAttackScore() {
		int pos = a_five*10000 + a_h_four*5000 + a_four*1000 + a_h_three*500 + a_three*100 + a_two*10;
		return pos;
	}
	public int getDefenseScore() {
		int neg = d_five*10000 + d_h_four*5000 + d_four*1000 + d_h_three*500 + d_three*100 + d_two*10;
		return neg;
	}
	public double getScore() {
		return aRatio*getAttackScore()-dRatio*getDefenseScore();
	}
	
}