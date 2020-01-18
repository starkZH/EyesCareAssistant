package service;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import component.Modal;

public class TestCamera extends JFrame implements Runnable  {

	String currentDir=System.getProperty("user.dir");
	JLabel label = new JLabel();
	VideoCapture capture = new VideoCapture(0);
	static boolean hasOpened=false;
	public TestCamera() {
		if(hasOpened) {
			super.dispose();
			return;
		}
		setTitle("摄像头测试");
		super.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				capture.release();
				hasOpened=false;
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
    	try {
        	System.load(currentDir+"/dll/"+Core.NATIVE_LIBRARY_NAME+".dll"); 
    	}catch(Exception e) {
            Modal.showModal("请检查环境配置", "Opencv环境加载失败", 8000, Modal.ERROR);
    	}
    	setSize(640,480);
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	getContentPane().add(label);
	}
	
	@Override
	public void run() {
		if(hasOpened)return;
		hasOpened=true;
		capture.set(CAP_PROP_FRAME_WIDTH, 640);
        capture.set(CAP_PROP_FRAME_HEIGHT, 480);
        capture.set(CAP_PROP_FPS, 30);
        Mat frame= new Mat(new Size(640, 480), CV_8UC3);
    	setResizable(false);
        setVisible(true);
        while (true) {
        	try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (!capture.read(frame)) {
            	if(!Modal.hasModal())
                Modal.showModal("请检查您的相机或是否有权限", "读取相机数据失败", 8000, Modal.ERROR);
                continue;
            }
            label.setIcon(new ImageIcon(HighGui.toBufferedImage(frame)));
        }
	}
	
	public static void main(String[] args) {
		new Thread(new TestCamera()).start();
		new Thread(new TestCamera()).start();
	}

}
