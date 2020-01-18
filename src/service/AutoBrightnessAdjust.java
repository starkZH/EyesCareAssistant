package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AutoBrightnessAdjust extends Thread{

	HashMap<String,Integer> timeBrightness=new HashMap<String,Integer>() {{
		put("9:00",20);put("11:00",30);put("14:00",50);put("19:00",30);put("21:00",20);
	}};
	boolean enable=false;
	public AutoBrightnessAdjust(boolean enable) {
		this.enable=enable;
	}
	
	public void setEnable(boolean enable) {
		this.enable=enable;
	}
	
	static void setBrightness(int val) {
		execute("powershell.exe",String.format("(Get-WmiObject -Namespace root\\wmi -Class WmiMonitorBrightnessMethods).wmisetbrightness(0,%d)", val));
	}
	
	static String execute(String cmd1,String cmd2) {
		ProcessBuilder builder = new ProcessBuilder(cmd1,cmd2);
		//System.out.println(cmd2);
	    String fullStatus = null;
	    Process reg;
	    builder.redirectErrorStream(true);
	    try {
	        reg = builder.start();
	        fullStatus = new BufferedReader(new InputStreamReader(reg.getInputStream()))
	        		  .lines().collect(Collectors.joining("\n")); 
	        reg.destroy();
	    } catch (IOException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	    }
	    return fullStatus;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(59000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!enable)continue;
			Date date=new Date();
			String time = date.getHours()+":"+date.getMinutes();
			if(timeBrightness.get(time)!=null)
				setBrightness(timeBrightness.get(time));
		}
	}
	
	public static void main(String[] args) {
		new AutoBrightnessAdjust(true).start();;

	}

}
