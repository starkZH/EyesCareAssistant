package main;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import component.Modal;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import service.AutoBrightnessAdjust;
import service.WinkDetect;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

import java.awt.AWTException;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.Font;
import java.awt.Image;
import java.awt.SystemTray;

import javax.swing.JButton;
import java.awt.Toolkit;
import java.awt.TrayIcon;

import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JTextArea;
public class Main extends JFrame{
	JButton winkButton = new JButton("开启眨眼提醒");
	JButton brightButton = new JButton("开启屏幕亮度调节");
	boolean enable_wink=false,enable_bright=false,enable_test=false;
	WinkDetect wink = null;
	AutoBrightnessAdjust briAdjust = new AutoBrightnessAdjust(false);
	JLabel winkTip = new JLabel("今日已眨眼 0 次 ");
	static SystemTray tray = SystemTray.getSystemTray();
	int lastX=-1,lastY=-1;
	private final JLabel minIcon = new JLabel("—");
	private final JButton userButton = new JButton("  数据统计");
	private final JButton testButton = new JButton("摄像头测试");
	private UserCenter userCenter = new UserCenter();
	
	void initWinkDect() {
    	try {
			wink = new WinkDetect(30,false);
			//wink.setShowWindows(true);
			new Thread(wink).start();
		} catch (Exception e) {
            Modal.showModal(e.getMessage(),"请检查环境配置", 8000, Modal.ERROR);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Main() {
		getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				int x=arg0.getX(),y=arg0.getY();
				lastX=x;lastY=y;
			}
		});
		getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				int x=arg0.getX(),y=arg0.getY();
				if(lastX<0) {
					lastX=x;lastY=y;
					return;
				}
				setBounds(getX()+x-lastX,getY()+y-lastY,getWidth(),getHeight());
			}
		});
		briAdjust.start();
		initWinkDect();
		setUndecorated(true);
		setSize(699,600);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("程序员护眼助手v1.0");
		getContentPane().setBackground(Color.DARK_GRAY);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/resource/eye.png")));
		getContentPane().setLayout(null);
		
		JLabel label = new JLabel("程序员护眼助手");
		label.setFont(new Font("微软雅黑", Font.BOLD, 21));
		label.setForeground(Color.WHITE);
		label.setBounds(27, 13, 386, 49);
		getContentPane().add(label);
		
		JLabel closeIcon = new JLabel("X");
		closeIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeIcon.setBackground(Color.DARK_GRAY);
		closeIcon.setOpaque(true);
		closeIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getButton()==MouseEvent.BUTTON1)
					System.exit(0);
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				closeIcon.setBackground(Color.RED);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				closeIcon.setBackground(Color.DARK_GRAY);
			}
		});
		closeIcon.setFont(new Font("微软雅黑", Font.BOLD, 15));
		closeIcon.setHorizontalAlignment(SwingConstants.CENTER);
		closeIcon.setForeground(Color.WHITE);
		closeIcon.setBounds(634, 13, 48, 33);
		getContentPane().add(closeIcon);
		winkButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(wink==null) {
					initWinkDect();
					return;
				}
				if(enable_wink) {
					Modal.showModal("", "已关闭眨眼提醒", 3000, Modal.PAUSE);
					winkButton.setIcon(new ImageIcon(Main.class.getResource("/resource/dis_eye_48.png")));
					winkButton.setText("开启眨眼提醒");
				}else {
					Modal.showModal("", "已开启眨眼提醒", 3000, Modal.SUCCESS);
					winkButton.setIcon(new ImageIcon(Main.class.getResource("/resource/eye_48.png")));
					winkButton.setText("关闭眨眼提醒");
					wink.setShowWindows(false);
				}
				enable_wink=!enable_wink;
				wink.setEnable(enable_wink);
			}
		});
		
		winkButton.setHorizontalAlignment(SwingConstants.LEADING);
		winkButton.setIconTextGap(25);
		winkButton.setIcon(new ImageIcon(Main.class.getResource("/resource/dis_eye_48.png")));
		winkButton.setForeground(Color.LIGHT_GRAY);
		winkButton.setBackground(Color.DARK_GRAY);
		winkButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		winkButton.setBounds(27, 264, 262, 74);
		getContentPane().add(winkButton);
		brightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(enable_bright) {
					Modal.showModal("", "已关闭屏幕亮度调节", 3000, Modal.PAUSE);
					brightButton.setIcon(new ImageIcon(Main.class.getResource("/resource/dis_brightness_48.png")));
					brightButton.setText("开启屏幕亮度调节");
				}else {
					Modal.showModal("", "已开启屏幕亮度智能调节", 3000, Modal.SUCCESS);
					brightButton.setIcon(new ImageIcon(Main.class.getResource("/resource/brightness_48.png")));
					brightButton.setText("关闭屏幕亮度调节");
				}
				enable_bright=!enable_bright;
				briAdjust.setEnable(enable_bright);
			}
		});
		
		brightButton.setIconTextGap(25);
		brightButton.setIcon(new ImageIcon(Main.class.getResource("/resource/dis_brightness_48.png")));
		brightButton.setBackground(Color.DARK_GRAY);
		brightButton.setForeground(Color.LIGHT_GRAY);
		brightButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		brightButton.setBounds(370, 264, 262, 74);
		getContentPane().add(brightButton);
		
		winkTip.setFont(new Font("微软雅黑 Light", Font.PLAIN, 14));
		winkTip.setForeground(Color.WHITE);
		winkTip.setBounds(27, 210, 515, 24);
		getContentPane().add(winkTip);
		minIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		minIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				minIcon.setBackground(Color.GRAY);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				minIcon.setBackground(Color.DARK_GRAY);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
	        	fadeOut();
			}
		});
		minIcon.setOpaque(true);
		minIcon.setHorizontalAlignment(SwingConstants.CENTER);
		minIcon.setForeground(Color.WHITE);
		minIcon.setFont(new Font("微软雅黑", Font.BOLD, 15));
		minIcon.setBackground(Color.DARK_GRAY);
		minIcon.setBounds(584, 13, 48, 33);
		
		getContentPane().add(minIcon);
		userButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				userCenter.setVisible(true);
			}
		});
		userButton.setHorizontalAlignment(SwingConstants.LEADING);
		userButton.setIcon(new ImageIcon(Main.class.getResource("/resource/staticis_48.png")));
		userButton.setIconTextGap(25);
		userButton.setForeground(Color.LIGHT_GRAY);
		userButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		userButton.setBackground(Color.DARK_GRAY);
		userButton.setBounds(27, 379, 262, 74);
		
		getContentPane().add(userButton);
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				HighGui.destroyAllWindows();
				wink.setShowWindows(true);
			}
		});
		testButton.setHorizontalAlignment(SwingConstants.LEADING);
		testButton.setIcon(new ImageIcon(Main.class.getResource("/resource/camera.png")));
		testButton.setIconTextGap(25);
		testButton.setForeground(Color.LIGHT_GRAY);
		testButton.setFont(new Font("微软雅黑", Font.PLAIN, 18));
		testButton.setBackground(Color.DARK_GRAY);
		testButton.setBounds(370, 379, 262, 74);
		
		getContentPane().add(testButton);
		
		JTextArea tipArea = new JTextArea();
		tipArea.setLineWrap(true);
		tipArea.setEnabled(false);
		tipArea.setEditable(false);
		tipArea.setBackground(Color.DARK_GRAY);
		tipArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
		tipArea.setForeground(Color.LIGHT_GRAY);
		tipArea.setText("本软件可提醒您及时眨眼，尽量避免眼睛干涩、酸痛。\r\n同时还可根据日照规律自动调节屏幕亮度，为您的眼睛健康保驾护航。");
		tipArea.setBounds(27, 96, 629, 85);
		getContentPane().add(tipArea);
		
		JLabel littleTip = new JLabel("小贴士：眨眼时,可以让泪液均匀地湿润角膜,使眼球不至于干燥,保持角膜光泽,清除结膜囊灰尘及细菌。\r\n");
		littleTip.setForeground(Color.WHITE);
		littleTip.setFont(new Font("微软雅黑 Light", Font.PLAIN, 14));
		littleTip.setBounds(18, 558, 688, 24);
		getContentPane().add(littleTip);
//
//		 try {
//		        org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
//		        UIManager.put("RootPane.setupButtonVisible", false);
//		    }
//		    catch(Exception e)
//		    {
//		        //TODO exception
//		    }
		autoSetWinkTimes();
		createTray();
 	   	fadeIn();
	}
	
	void fadeIn() {
		super.setVisible(true);
		float opacity=0.2f;
		super.setOpacity(opacity);
		for(int i=0;i<20;i++) {
			opacity+=0.8/16;
			if(opacity>1) {
				super.setOpacity(1);
				break;
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.setOpacity(opacity);
		}
	}
	
	void fadeOut() {
		float opacity=1.0f;
		for(int i=0;i<20;i++) {
			opacity-=1.0/15;
			if(opacity<0)break;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.setOpacity(opacity);
		}
		super.setVisible(false);
	}
	
	void createTray() {
        // 获得Image对象
        Image image = new ImageIcon(Main.class.getResource("/resource/eye.png")).getImage();
        // 创建托盘图标
        TrayIcon trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        // 为托盘添加鼠标适配器
        trayIcon.addMouseListener(new MouseAdapter()
        {
           // 鼠标事件
           public void mouseClicked(MouseEvent e)
           {
        	   fadeIn();
           }
        });
        try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	void autoSetWinkTimes() {
		new Thread(()-> {
			while(true) {
				try {
					Thread.sleep(600);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				winkTip.setText(String.format("今日已眨眼 %d 次 ", wink.getWinkTimes()));
				
			}
		}).start();
	}
	
    public static void main(String[] args) {
    	new Main();
    	
    }
}
