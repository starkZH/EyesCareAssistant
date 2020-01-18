package component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.SwingConstants;

public class Modal extends JWindow implements Runnable{

	static int count=0;
	
	static Integer WIDTH=450,HEIGHT=128;
	public static Integer INFO=0,WARNING=1,WELCOME=2,EYE=3,ERROR=4,PAUSE=5,SUCCESS=6;
	static Dimension screenSize =  Toolkit.getDefaultToolkit().getScreenSize();
	
	int duration=5000;
	int iconType=0;
	boolean closeFlag=false;
	private static String iconName[] = {"tip.png","warning.png","welcome.png","eye.png","error.png","pause.png","success.png"};
	JLabel icon = new JLabel("");
	JLabel titleLabel = new JLabel("\u8BF7\u6CE8\u610F\u7728\u773C\u4E86~");
	JLabel descLabel = new JLabel("\u60A8\u5DF2\u7ECF1s\u6CA1\u6709\u7728\u773C\u4E86~");
	
	static HashMap<String,Modal> modalMap=new HashMap<>();;
	
	public Modal(String message,String title,int duration,int iconType) {
		this.duration=duration;
		this.iconType=iconType;
		setSize(new Dimension(450, 128));
		getContentPane().setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(null);
		super.setName("MODEL_"+count);
		modalMap.put(super.getName(), this);
		
		icon.setIcon(new ImageIcon(Modal.class.getResource("/resource/"+iconName[iconType])));
		icon.setBounds(26, 24, 72, 72);
		getContentPane().add(icon);
		
		titleLabel.setText(title);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 19));
		titleLabel.setBounds(112, 24, 297, 32);
		getContentPane().add(titleLabel);
		
		descLabel.setText(message);
		descLabel.setForeground(Color.LIGHT_GRAY);
		descLabel.setFont(new Font("Î¢ÈíÑÅºÚ Light", Font.BOLD, 15));
		descLabel.setBounds(112, 59, 273, 37);
		getContentPane().add(descLabel);
		JLabel closeIcon = new JLabel("X");
		closeIcon.setBackground(Color.DARK_GRAY);
		closeIcon.setHorizontalAlignment(SwingConstants.CENTER);
		closeIcon.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 15));
		closeIcon.setForeground(Color.WHITE);
		closeIcon.setOpaque(true);
		closeIcon.setName(super.getName());
		closeIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(arg0.getButton()==MouseEvent.BUTTON1) {
					Modal.closeModal(closeIcon.getName());
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				closeIcon.setBackground(Color.GRAY);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				closeIcon.setBackground(Color.DARK_GRAY);
			}
		});
		closeIcon.setBounds(416, 0, 32, 32);
		getContentPane().add(closeIcon);
		super.setBounds((int)screenSize.getWidth()-10-WIDTH, count*(HEIGHT+20)+20, WIDTH, HEIGHT);
		count++;
	}

	
	void init() {
		
		super.setAlwaysOnTop(true);
		enterAnimat();
	}
	
	void enterAnimat() {
		int ix=super.getX(),iy=super.getY(),width=super.getWidth(),height=super.getHeight();
		float opacity=0.1f;
		super.setOpacity(opacity);
		super.setVisible(true);
		for(int i=iy+50;i>=iy;i--) {
			opacity+=0.9/40;
			if(opacity>1)break;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.setBounds(ix, iy+(int)Math.pow(((1-opacity)*(8)), 1.8), width, height);
			super.setOpacity(opacity);
		}
	}
	
	private void close() {
		if(closeFlag) {
			super.dispose();
			return;
		}
		closeFlag=true;
		int ix=super.getX(),iy=super.getY(),width=super.getWidth(),height=super.getHeight();
		float opacity=1f;
		super.setOpacity(opacity);
		super.setVisible(true);
		for(int i=iy+50;i>=iy;i--) {
			opacity-=1.0/30;
			if(opacity<0)break;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			super.setBounds(ix, iy+(int)((1-opacity)*50), width, height);
			super.setOpacity(opacity);
		}
		super.dispose();
		count--;
	}
	
	public static boolean hasModal() {
		return count>0;
	}
	
	public static void closeModal(String name) {
		Modal modal = modalMap.remove(name);
		if(modal!=null)
			modal.close();
	}
	
	public static String showModal(String message,String title,int duration,int iconType) {
		Modal modal=new Modal(message,title,duration,iconType);
		new Thread(modal).start();
		return modal.getName();
	}


	@Override
	public void run() {
		init();
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		close();
	}
	
	public static void main(String[] args) {
		Modal.showModal("123", "123", 3000,Modal.INFO);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Modal.showModal("123", "123", 3000,Modal.WELCOME);
	}
}
