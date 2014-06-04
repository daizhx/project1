package com.hengxuan.ehealthplatform.lens;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hengxuan.ehealthplatform.lens.CameraSource;
import com.hengxuan.ehealthplatform.lens.SocketCamera;
import com.hengxuan.ehealthplatform.log.Log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LensMonitorView extends SurfaceView implements SurfaceHolder.Callback{

	public static final String TAG = "LensMonitorView";
	private IrisMonitorThread thread;
	private LensMonitorParameter cmPara;
	private Bitmap capture_bitmap = null;
	private boolean retry = true;
    private HttpURLConnection httpURLconnection;
    private float previewRatio = (float) (16.0/9.0);
    private Context mContext;
    
	public LensMonitorView(Context context, AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mContext = context;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		Log.d(TAG, "LensMonitorView get suggested size:"+width+"x"+height);
		if(width == 0 || height == 0){
			return;
		}
		if(((float)height/(float)width) > previewRatio){
			height = (int)(((float)width)*previewRatio);
		}else if(((float)height/(float)width) < previewRatio){
			width = (int)(((float)height)/previewRatio);
		}
		setMeasuredDimension(width, height);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.d(TAG, "LensMonitorView:"+width+"x"+height);
		thread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
        thread = new IrisMonitorThread(holder);
		thread.setRunning(true);
		try{
			setCmPara(initParam());
			thread.start();
		}catch(IllegalThreadStateException e){}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "LensMonitorView--Destroyed");
		thread.closeCameraSource();
		if(null != httpURLconnection)
			httpURLconnection.disconnect(); 
			
		if(capture_bitmap != null) {
			if(!capture_bitmap.isRecycled()){
				capture_bitmap.recycle();   //回收图片所占的内存   
		        System.gc();  //提醒系统及时回收   
			}
		}
		stop();
	}
	
	private LensMonitorParameter initParam()
	{
		LensMonitorParameter param = new LensMonitorParameter();
		param.setId(1);
		param.setConnectType(0);
		param.setIp("10.10.10.254");
		param.setLocal_dir("/sdcard");
		param.setName("192.168.1.102");
		param.setUsername("aaaaa");
		param.setPassword("123456");
		param.setPort(8080);
		param.setTime_out(2000);
		param.setConnectType(mContext.BIND_AUTO_CREATE);
		return param;
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
		
		/**
		 * 从socketcamera获取图片并画到surface上
		 * @param width
		 * @param height
		 * @param httpURLconnection
		 * @return
		 */
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
	
	public void start(){
		//thread initialized in the construction
		this.thread.setRunning(true);
		this.thread.start();
	}
	public void stop(){
		if(thread.mRun){
			this.thread.setRunning(false);
			this.thread.interrupt();
			this.thread = null;
		}
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
