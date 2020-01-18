package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;

import javax.swing.JFrame;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.ba;
import com.teamdev.jxbrowser.chromium.dom.*;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import java.io.*;

public class UserCenter extends JFrame{
	 static {
	        try {
	            Class claz = null;
	            //6.5.1版本破解 兼容xp
	            claz =  Class.forName("com.teamdev.jxbrowser.chromium.ba");
	            //6.21版本破解 默认使用最新的6.21版本
	            // claz =  Class.forName("com.teamdev.jxbrowser.chromium.ba");

	            Field e = claz.getDeclaredField("e");
	            Field f = claz.getDeclaredField("f");


	            e.setAccessible(true);
	            f.setAccessible(true);
	            Field modifersField = Field.class.getDeclaredField("modifiers");
	            modifersField.setAccessible(true);
	            modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
	            modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
	            e.set(null, new BigInteger("1"));
	            f.set(null, new BigInteger("1"));
	            modifersField.setAccessible(false);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.err.println("执行jxbrowser破解程序时出现异常" );
	        }
	    }

	 final String url = "http://120.27.247.9/Program/RegisterLogin/sessionCheck.php"; 
     Browser browser = new Browser();
     BrowserView view = new BrowserView(browser);  
     static BrowserService browserService=new BrowserService();
	 public UserCenter() {
		 super("用户中心");//自动更新首页
		 setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/resource/eye.png")));
	        browserService.setWebEngine(browser);
	        browser.addLoadListener(new LoadAdapter() {
	            @Override
	            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
	                super.onFinishLoadingFrame(finishLoadingEvent);
	                JSValue window = browser.executeJavaScriptAndReturnValue("window");
	                //给jswindows对象添加一个扩展的属性
	                window.asObject().setProperty("openAppUtil",browserService ); 
	            }
	        });
	        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);        
	        getContentPane().add(view, BorderLayout.CENTER);
	        super.setResizable(false);
	        browser.loadURL(url);
	        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	        setSize((int)dim.getWidth(),(int)dim.getHeight());
	        setExtendedState(JFrame.MAXIMIZED_BOTH);  
	        setLocationRelativeTo(null);
	 }
	 
	public static void saveTimes(int times) {
		browserService.execJS(String.format("getNum(%d)", times));
	}
	 
	public static void main(String[] args) {
		new UserCenter().setVisible(true);;
	}
	

}
