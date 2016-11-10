import java.util.TimerTask;

public class TimeCount extends TimerTask{
    public static int count = 0;

    public TimeCount() {
		// TODO Auto-generated constructor stub
    	count = 0;
	}
	//此方法要覆寫
	//想要定時執行的工作寫在該method中
	public void run(){
		count++;
	}
	
	public static int getTimeCount(){
		return count;
	}
}