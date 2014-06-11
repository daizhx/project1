/****************************************************************************
Copyright (c) 2010-2011 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.lib;

import com.hengxuan.ehealthplatform.bluetooth.BluetoothServiceProxy;
import com.hengxuan.ehealthplatform.log.Log;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Handler;

public class Cocos2dxMusic {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String TAG = Cocos2dxMusic.class.getSimpleName();

	// ===========================================================
	// Fields
	// ===========================================================

	private final Context mContext;
	private MediaPlayer mBackgroundMediaPlayer;
	private float mLeftVolume;
	private float mRightVolume;
	private boolean mPaused;
	private String mCurrentPath;
	protected short averagenum;
	private Visualizer mVisualizer;
	
	private int count;	
	
	public static int SENDMESSAGE_TIME_GAP = 500;
	// ===========================================================
	// Constructors
	// ===========================================================

	public Cocos2dxMusic(final Context pContext) {
		this.mContext = pContext;

		this.initData();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public void preloadBackgroundMusic(final String pPath) {
		if ((this.mCurrentPath == null) || (!this.mCurrentPath.equals(pPath))) {
			// preload new background music

			// release old resource and create a new one
			if (this.mBackgroundMediaPlayer != null) {
				this.mBackgroundMediaPlayer.release();
			}

			this.mBackgroundMediaPlayer = this.createMediaplayerFromAssets(pPath);

			// record the path
			this.mCurrentPath = pPath;
		}
	}

	public void playBackgroundMusic(final String pPath, final boolean isLoop) {
		if (this.mCurrentPath == null) {
			// it is the first time to play background music or end() was called
			this.mBackgroundMediaPlayer = this.createMediaplayerFromAssets(pPath);
			this.mCurrentPath = pPath;
		} else {
			if (!this.mCurrentPath.equals(pPath)) {
				// play new background music

				// release old resource and create a new one
				if (this.mBackgroundMediaPlayer != null) {
					this.mBackgroundMediaPlayer.release();
				}
				this.mBackgroundMediaPlayer = this.createMediaplayerFromAssets(pPath);

				// record the path
				this.mCurrentPath = pPath;
			}
		}

		if (this.mBackgroundMediaPlayer == null) {
			Log.e(Cocos2dxMusic.TAG, "playBackgroundMusic: background media player is null");
		} else {
			// if the music is playing or paused, stop it
			this.mBackgroundMediaPlayer.stop();

			this.mBackgroundMediaPlayer.setLooping(isLoop);

			try {
				this.mBackgroundMediaPlayer.prepare();
				this.mBackgroundMediaPlayer.seekTo(0);
				this.mBackgroundMediaPlayer.start();
				if(BluetoothServiceProxy.isconnect()){
					BluetoothServiceProxy.music_flag = true;
					this.getVisualizerdata();
				}else{
					BluetoothServiceProxy.music_flag = false;
				}		
				this.mPaused = false;
			} catch (final Exception e) {
				Log.e(Cocos2dxMusic.TAG, "playBackgroundMusic: error state");
			}
		}
	}

	public void stopBackgroundMusic() {
		if (this.mBackgroundMediaPlayer != null) {
			this.mBackgroundMediaPlayer.stop();

			// should set the state, if not, the following sequence will be error
			// play -> pause -> stop -> resume
			this.mPaused = false;
			BluetoothServiceProxy.music_flag = false;
		}
	}

	public void pauseBackgroundMusic() {
		if (this.mBackgroundMediaPlayer != null && this.mBackgroundMediaPlayer.isPlaying()) {
			this.mBackgroundMediaPlayer.pause();
			this.mPaused = true;
			BluetoothServiceProxy.music_flag = false;
		}
	}

	public void resumeBackgroundMusic() {
		if (this.mBackgroundMediaPlayer != null && this.mPaused) {
			this.mBackgroundMediaPlayer.start();
			this.getVisualizerdata();
			this.mPaused = false;
			BluetoothServiceProxy.music_flag = true;
		}
	}

	public void rewindBackgroundMusic() {
		if (this.mBackgroundMediaPlayer != null) {
			this.mBackgroundMediaPlayer.stop();

			try {
				this.mBackgroundMediaPlayer.prepare();
				this.mBackgroundMediaPlayer.seekTo(0);
				this.mBackgroundMediaPlayer.start();

				this.mPaused = false;
				BluetoothServiceProxy.music_flag = true;
			} catch (final Exception e) {
				Log.e(Cocos2dxMusic.TAG, "rewindBackgroundMusic: error state");
			}
		}
	}

	public boolean isBackgroundMusicPlaying() {
		boolean ret = false;

		if (this.mBackgroundMediaPlayer == null) {
			ret = false;
		} else {
			ret = this.mBackgroundMediaPlayer.isPlaying();
		}

		return ret;
	}

	public void end() {
		if (this.mBackgroundMediaPlayer != null) {
			this.mBackgroundMediaPlayer.release();
		}

		this.initData();
	}

	public float getBackgroundVolume() {
		if (this.mBackgroundMediaPlayer != null) {
			return (this.mLeftVolume + this.mRightVolume) / 2;
		} else {
			return 0.0f;
		}
	}

	public void setBackgroundVolume(float pVolume) {
		if (pVolume < 0.0f) {
			pVolume = 0.0f;
		}

		if (pVolume > 1.0f) {
			pVolume = 1.0f;
		}

		this.mLeftVolume = this.mRightVolume = pVolume;
		if (this.mBackgroundMediaPlayer != null) {
			this.mBackgroundMediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
		}
	}

	private void initData() {
		this.mLeftVolume = 0.5f;
		this.mRightVolume = 0.5f;
		this.mBackgroundMediaPlayer = null;
		this.mPaused = false;
		this.mCurrentPath = null;
	}

	/**
	 * create mediaplayer for music
	 * 
	 * @param pPath
	 *            the pPath relative to assets
	 * @return
	 */
	private MediaPlayer createMediaplayerFromAssets(final String pPath) {
		MediaPlayer mediaPlayer = new MediaPlayer();

		try {
			if (pPath.startsWith("/")) {
				mediaPlayer.setDataSource(pPath);
			} else {
				final AssetFileDescriptor assetFileDescritor = this.mContext.getAssets().openFd(pPath);
				mediaPlayer.setDataSource(assetFileDescritor.getFileDescriptor(), assetFileDescritor.getStartOffset(), assetFileDescritor.getLength());
			}

			mediaPlayer.prepare();

			mediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
		} catch (final Exception e) {
			mediaPlayer = null;
			Log.e(Cocos2dxMusic.TAG, "error: " + e.getMessage(), e);
		}
		return mediaPlayer;
	}
	 private int  averageAmplitude( byte[] waveform)
	    {
	    	int sum = 0;
	    	if(waveform.length > 0)
	    	{
	    		for(int i=0;i<waveform.length;i++)
	    		{
	    			sum = sum + waveform[i];
	    		}
	    		return sum/waveform.length;
	    	}
	    	else
	    		return 0;
	    }
	private int transformationCommandFormat(int amplitude)
	 {
		if(amplitude < 0) {
			amplitude = 0;
		} else if(amplitude > 256) {
			amplitude = 256;
		}
		
		return amplitude / 16 + 0x1100;
	 }
	public void getVisualizerdata(){
		
		int mAudioSessionId = mBackgroundMediaPlayer.getAudioSessionId();
		if(mVisualizer!=null)
		{
			
	        mVisualizer.setEnabled(false);

		}
		mVisualizer = new Visualizer(mAudioSessionId);
   	    Log.v("visualizer","getVisualizerdata"+mAudioSessionId);

		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            
                 @Override
                 public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
                         int samplingRate) {
					// TODO Auto-generated method stub                           	
					if(Log.E) {
						StringBuilder strBuilder = new StringBuilder("len = ").append(waveform.length); 
    					Log.e("OnWaveFormDataCature : ", strBuilder.toString());
    				}
					/*int dataArr[] = new int[17];
					for(int i = 0; i < waveform.length; i++) {
						int data = (waveform[i] + 128) / 16;
						dataArr[data]++;
					}
					
					int max = dataArr[0];  
					int index = 0;
			        for (int j = 1; j < dataArr.length; j++) {  
			            //>0表示前者大于后者   
			            if (dataArr[j] > max) {  
			                max = dataArr[j];  
			                index = j;  
			            }  
			        }
			        
			        final short message = (short)transformationCommandFormat(index);
        			BluetoothServiceProxy.sendCommandToDevice(message);
        			*/
			        
					
					// averagenum = (short)averageAmplitude(waveform);//获取振幅平均值发给按摩器
					//int total = 0;
					//int average = 0;
					count = 0;
	            	for(int i = 1; i < waveform.length; i++) {
	            		if(i % SENDMESSAGE_TIME_GAP == 0) {
	            			// average = total / SENDMESSAGE_TIME_GAP;
	            			count ++;
	            			if(Log.E) {
								StringBuilder strBuilder = new StringBuilder("value = ").append(waveform[i] + 128); 
		    					Log.e(" ", strBuilder.toString());
		    				}
	            			
	            			final short message = (short)transformationCommandFormat(waveform[i] + 128);
	            			// BluetoothServiceProxy.sendCommandToDevice(message);
	            			
	            			Handler handler = new Handler();
	        				handler.postDelayed(new Runnable() { //向按摩机发送数据

	        					@Override
	        					public void run() {
	        						if(Log.E) {
			        					Log.e("run", "test");
			        				}
			        				try {
										BluetoothServiceProxy.sendCommandToDevice(message);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
	        					}
	        				}, (100 * SENDMESSAGE_TIME_GAP / waveform.length) * count);	        				
	        				/*
	            			new Thread(new Runnable() {//启动线程向按摩机发送数据

			        			@Override
			        			public void run() {
			        				if(Log.E) {
			        					Log.e("run", "test");
			        				}
			        				BluetoothServiceProxy.sendCommandToDevice(message);
			        			}

			        		}).start();
			        		*/
	            		}
	            	}
	            	count = 0;
	            }
                
                 @Override
                 public void onFftDataCapture(Visualizer visualizer, byte[] fft,
                         int samplingRate) {
                     // TODO Auto-generated method stub
                	 Log.v("visualizer","on onFftDataCapture");

                 }
             }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
   	 Log.v("visualizer","getVisualizerdata end");

	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
