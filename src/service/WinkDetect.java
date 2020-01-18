package service;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.rectangle;
import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

import java.awt.Image;
import java.io.*;
import java.net.URLDecoder;
import java.util.Date;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import component.Modal;
import main.UserCenter;

public class WinkDetect implements Runnable{

    static final long standard=3000;
    int winkTimes=0;
    int fps=30;
    int delay=15;
    boolean showWindow=true;
    boolean enable=false;
    long startTime=System.currentTimeMillis();
    Mat frame = null;
    CascadeClassifier cascadeClassifier = null, eyeClassifier = null;
	String currentDir=URLDecoder.decode(System.getProperty("user.dir"), "utf-8");
    VideoCapture capture = null;
    String todayDate="";
    void init() throws Exception {
    	try {
        	System.load(currentDir+"/dll/"+Core.NATIVE_LIBRARY_NAME+".dll"); 
    	}catch(Exception e) {
            Modal.showModal("请检查环境配置", "Opencv环境加载失败", 8000, Modal.ERROR);
            throw new Exception("Opencv环境加载失败");
    	}
    	frame=new Mat(new Size(640, 480), CV_8UC3);
    	String faceXmlPath=currentDir+"/xml/lbpcascade_frontalface_improved.xml",
    			eyeXmlPath=currentDir+"/xml/haarcascade_eye_tree_eyeglasses.xml";
    	cascadeClassifier = new CascadeClassifier(faceXmlPath);
    	eyeClassifier = new CascadeClassifier(eyeXmlPath);
    	if (cascadeClassifier.empty()) {
            Modal.showModal("请检查环境配置", "加载人脸xml模型文件失败", 8000, Modal.ERROR);
            throw new Exception("加载人脸xml模型文件失败！"+faceXmlPath);
        }
    	if (eyeClassifier.empty()) {
            Modal.showModal("请检查环境配置", "加载眼睛xml模型文件失败", 8000, Modal.ERROR);
            throw new Exception("加载眼睛xml模型文件失败！"+eyeXmlPath);
        }
    	readTodayWinkTimes();
    }
    
    public WinkDetect() throws Exception {
    	init();
    }
    
    public WinkDetect(int fps,boolean showWindow) throws Exception {
    	init();
    	this.setFps(fps);
    	this.setShowWindows(showWindow);
    }
    
    public int getWinkTimes() {
    	return winkTimes;
    }

    public void setEnable(boolean enable) {
    	this.enable=enable;
   		startTime=System.currentTimeMillis();
    }
    
    public void setFps(int fps) {
    	this.fps=fps;
    }

    public void setDelay(int delay) {
    	this.delay=delay;
    }
    
    public void setShowWindows(boolean showWindow) {
    	this.showWindow=showWindow;
    }

    public Image getImage() {
    	return HighGui.toBufferedImage(frame);
    }
    
    public int getFps() {
    	return fps;
    }

    public int getDelay() {
    	return delay;
    }
    
    void readTodayWinkTimes() {
    	try {
    		Date date = new Date();
    		todayDate=date.getYear()+""+date.getMonth()+""+date.getDate();
			BufferedReader br = new BufferedReader(new FileReader(currentDir+"/cache_"+todayDate));
			winkTimes=Integer.parseInt(br.readLine());
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    }
    
    void cacheWinkTimes() {
    	try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(currentDir+"/cache_"+todayDate));
			bw.write(winkTimes+"");
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
    	
    }
    
	@Override
	public void run() { 
		capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            Modal.showModal("请检查您的相机或是否有权限", "打开相机失败", 8000, Modal.ERROR);
        } else {
        	//设置相机参数
            capture.set(CAP_PROP_FRAME_WIDTH, 640);
            capture.set(CAP_PROP_FRAME_HEIGHT, 480);
            capture.set(CAP_PROP_FPS, fps);
            Mat frameGray = new Mat(new Size(640, 480), CV_8UC1);
            MatOfRect objectsRect = new MatOfRect(),eyeRect= new MatOfRect();
            int times=0;
            boolean winkFlag=false;
    		Modal.showModal("", "欢迎使用", 3000,Modal.WELCOME);
            while (true) { 
            	try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	if(!(enable||showWindow)) {
            		continue;
            	}
                if (!capture.read(frame)) {
                	if(!Modal.hasModal())
                    Modal.showModal("请检查您的相机或是否有权限", "读取相机数据失败", 8000, Modal.ERROR);
                    continue;
                }
              //转为灰度
                cvtColor(frame, frameGray, COLOR_BGR2GRAY);
              //人脸检测
                cascadeClassifier.detectMultiScale(frameGray, objectsRect);
                if (!objectsRect.empty()) { //绘制矩形
                    Rect[] rects = objectsRect.toArray();
                    if(showWindow)
                    for (Rect r : rects) {
                        rectangle(frame, r.tl(), r.br(), new Scalar(0, 255, 255), 2);
                    }
                    Rect r=rects[0];
                    //检查第一张人脸的眼睛
                    eyeClassifier.detectMultiScale(frameGray.submat(r),eyeRect);
                    //如果超过两只眼睛则不检查
                    if(eyeRect.rows()<=2) {
                    if(!eyeRect.empty()){
                    	if(enable) {
                            if(winkFlag) {
                            	winkTimes++;
                            	cacheWinkTimes();
                            	UserCenter.saveTimes(winkTimes);
                            	System.out.println("又眨眼啦，已经眨"+winkTimes+"次了");
                            }
                        	//不在正常范围内
                        	long dis = System.currentTimeMillis()-startTime;
                        	if(dis>standard) {
                        		if(!Modal.hasModal()) {
                        			Modal.showModal("您已经"+dis/1000+"s没有眨眼了", "您该眨眼啦", 3000,Modal.EYE);
                        		}
                        		System.out.println("该眨眼了 老弟，你已经"+dis+"ms没有眨眼了");
                        	}else {
                           	 	//startTime=System.currentTimeMillis();
                        	}
                    		
                    	}
                        if(showWindow) {
                            rects =eyeRect.toArray();
                            for (Rect r2 : rects) {
                                Point p1=new Point(r.x+r2.x,r.y+r2.y),p2 = new Point(r.x+r2.x+r2.width,r.y+r2.y+r2.height);
                                rectangle(frame, p1, p2, new Scalar(255, 255, 0), 2);
                            }
                        }
                        winkFlag=false;
                    }else {
                    	//没眼睛，表示已眨眼，清空起始时间
                    	 startTime=System.currentTimeMillis();
                    	 //System.out.println("很好你眨眼了");
                    	 winkFlag=true;
                    }
                }
                }else if(times++>30) {
                	times=0;
               		startTime=System.currentTimeMillis();
            		if(!Modal.hasModal()) {
            			Modal.showModal("请调整好您的位置和环境的光线亮度", "检测不到您的脸部", 3000,Modal.WARNING);
            		}
               		System.out.println("已重置初始时间");
                }
                //显示
                if(showWindow) {
                    HighGui.imshow("护眼助手摄像头测试", frame);
                    HighGui.waitKey(33);
                }else {
                	HighGui.destroyAllWindows();
                }
            }
        }

		
	}
    
    
}
