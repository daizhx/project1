package com.hengxuan.ehealthplatform.lens.activity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hengxuan.ehealthplatform.lens.CameraSource;
import com.hengxuan.ehealthplatform.lens.SocketCamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LensMonitorView extends SurfaceView implements SurfaceHolder.Callback{

	public IrisMonitorThread thread;
	public static final String TAG = "CamMonitorView";
	private LensMonitorParameter cmPara;
	private Bitmap capture_bitmap = null;
	private boolean retry = true;
    private HttpURLConnection httpURLconnection;
	public LensMonitorView(Context context, AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);        
        thread = new IrisMonitorThread(holder);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.setRunning(true);
		try{
			thread.start();
		}catch(IllegalThreadStateException e){}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		thread.closeCameraSource();
		if(null != httpURLconnection)
			httpURLconnection.disconnect(); 
			
		if(capture_bitmap != null) {
			if(!capture_bitmap.isRecycled()){   
				capture_bitmap.recycle();   //回收图片所占的内存   
		        System.gc();  //提醒系统及时回收   
			}
		}
			
		/*
	     while (retry) {
	    	 try {
	    		 thread.join();
	             retry = false;

	         } catch (InterruptedException e) {
	         }
	    }
	    */
	}
	
	public void setRetry(boolean flag) {
		retry = flag;
	}

	public class IrisMonitorThread extends Thread{
		
		private SurfaceHolder mSurfaceHolder;
		
		private int mCanvasHeight = 1;
		
		private int mCanvasWidth = 1;
		
		private boolean mRun = false;
	        
	    private CameraSource cs;
	        
	    private Canvas c = null;	    
	    
	    public IrisMonitorThread(SurfaceHolder surfaceHolder) {
			super();
			mSurfaceHolder = surfaceHolder;
		}
	    
	    public void setRunning(boolean b) {
            mRun = b;

            if (mRun == false) {
                
            }
        }

		@Override
		public void run() {
			// TODO Auto-generated method stub
				URL url;
				try {
					url = new URL("http://"+cmPara.getIp()+":"+cmPara.getPort());
					
					while(mRun){
						httpURLconnection = (HttpURLConnection)url.openConnection();
						httpURLconnection.setRequestMethod("GET"); 
						httpURLconnection.setReadTimeout(2*1000);
						
						// Log.e("isrun", "run capture");
						try {
							c = mSurfaceHolder.lockCanvas(null);
							
							captureImage(mCanvasWidth, mCanvasHeight, httpURLconnection);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						finally {
							if (c != null) {
								// Log.e("isrun", "run finally");
								mSurfaceHolder.unlockCanvasAndPost(c);
								c = null;
							}
						}
						
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					mRun = false;
					// Toast.makeText(context, text, duration)
					e1.printStackTrace();
				}
		}
	    
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;    
            }
		}
		private boolean captureImage(int width, int height,HttpURLConnection httpURLconnection){		
			
			cs = new SocketCamera(width, height, true);
	        cs.capture(c, httpURLconnection); //capture the frame onto the canvas
	        capture_bitmap = cs.getCaptureImage();

	        return true;
		}
		
		public boolean saveImage(){
			
			String now = String.valueOf(System.currentTimeMillis());
			if(cs == null){
				return false;
			}
			cs.saveImage(cmPara.getLocal_dir()+"/ehealthtec/image", now+".PNG");
			
			return true;
		}
		
		public void closeCameraSource(){
			if(null != cs)
				cs.close();
		}
		
		public CameraSource getCameraSource() {
			return cs;
		}
	}
	
	public LensMonitorParameter getCmPara() {
		return cmPara;
	}
	
	public void setCmPara(LensMonitorParameter cmPara) {
		this.cmPara = cmPara;
	}
	
	public void setRunning(boolean b) {
        this.thread.setRunning(b);
    }
	
	public boolean getRunning(){
		return thread.mRun;
	}

	public IrisMonitorThread getThread() {
		return thread;
	}
	
	public Bitmap getCaptureImage()
	{
		return capture_bitmap;
	}

}
