import java.util.TimerTask;

public class TimeCount extends TimerTask{
    public static int count = 0;

    public TimeCount() {
		// TODO Auto-generated constructor stub
    	count = 0;
	}
	//����k�n�мg
	//�Q�n�w�ɰ��檺�u�@�g�b��method��
	public void run(){
		count++;
	}
	
	public static int getTimeCount(){
		return count;
	}
}