package com.hengxuan.ehealthplatform.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.log.Log;


public class DefaultEffectHttpListener implements HttpGroup.OnStartListener,
	HttpGroup.OnEndListener, HttpGroup.OnErrorListener, BaseActivity.DestroyListener {
	
	class State implements Runnable {

		private static final int WAIT_TIME = 500;
		
		private boolean hasThread;
		private RelativeLayout.LayoutParams layoutParams;
		private int missionCount;
		private ViewGroup modal;
		private BaseActivity myActivity;
		private ProgressBar progressBar;
		private ViewGroup rootFrameLayout;
		private int waitTime;

		private void firstMission()
		{
			if (hasThread)
			{
				waitTime = -1;
				//System.out.println("=====have wrong is occurred-myactivity="+myActivity+"---------------------");
				notify();
			} else
			{
				final ViewGroup rootFrameLayout = getRootFrameLayout();
				final ViewGroup modal = getModal();
				newProgressBar();
				
				myActivity.post(new Runnable() {
					public void run()
					{
						if (Log.D)
						{
							StringBuilder stringbuilder = new StringBuilder("state add modal -->> ");
							String s = stringbuilder.append(modal).toString();
							Log.d("DefaultEffectHttpListener", s);
						}
						
						ViewGroup.LayoutParams layoutparams = new ViewGroup.LayoutParams(-1, -1);
						rootFrameLayout.addView(modal, layoutparams);
						//System.out.println("+++++++++++++++++display progressBar--myacivity="+myActivity+"+++++++++++++++++++++++++");
						rootFrameLayout.invalidate();
						myActivity.onShowModal();
					}
				});
			}
		}
		
		private ViewGroup getModal()
		{
			if (modal == null)
			{
				modal = new RelativeLayout(myActivity);
				
				modal.setOnTouchListener(new View.OnTouchListener() {
					public boolean onTouch(View view, MotionEvent motionevent)
					{
						return true;
					}
				});
				ColorDrawable colordrawable = new ColorDrawable(Color.BLACK);
				colordrawable.setAlpha(100);
				modal.setBackgroundDrawable(colordrawable);
			}
			
			return modal;
		}

		private ViewGroup getRootFrameLayout()
		{
			if (rootFrameLayout == null)
			{
				rootFrameLayout = (ViewGroup)myActivity.getWindow().peekDecorView();

				if (rootFrameLayout == null)
				{
					try
					{
						Thread.sleep(((long) (50L)));
					} 
					catch (InterruptedException interruptedexception) 
					{
					}
					
					rootFrameLayout = getRootFrameLayout();
				}
			}
			
			return ((ViewGroup) (rootFrameLayout));
		}

		private void lastMission()
		{
			if (hasThread)
			{
				waitTime = WAIT_TIME;
				notify();
			} else
			{
				(new Thread(this)).start();
				hasThread = true;
			}
		}

		private void newProgressBar()
		{
			myActivity.post(new Runnable() {
				public void run()
				{
					modal.removeView(progressBar);
					progressBar = new ProgressBar(myActivity);
					modal.addView(progressBar, layoutParams);
				}
			});
		}

		public boolean addMission()
		{
			boolean flag = true;
			
			synchronized(this) {
				int i = missionCount + 1;
				missionCount = i;
				if (missionCount != 1)
				{
					flag = false;
				}
				else
				{
					firstMission();
				}
			}
			
			return flag;
		}

		public boolean removeMission()
		{
			boolean flag = true;
			
			synchronized(this) {
				int i = missionCount - 1;
				missionCount = i;
				if (missionCount >= 1)
				{
					flag = false;
				}
				else
				{
					lastMission();
				}
			}
			
			return flag;
		}

		
		public void run()
		{
			while(hasThread){
			synchronized(this) {
				int i = waitTime;
				
				try {
					if (i != -1)
					{
						int j = waitTime;
						waitTime = 0;
						wait(j);
					}
					else
					{
						wait();
					}
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
				
				 if(waitTime == 0)
				{
					 //System.out.println("-----thread exit because of waitTime == 0");
					final ViewGroup rootFrameLayout = getRootFrameLayout();
					final ViewGroup modal = getModal();
										
					myActivity.post(new Runnable() {
						public void run()
						{
							if (Log.D)
							{
								StringBuilder stringbuilder = new StringBuilder("state remove modal -->> ");
								String s = stringbuilder.append(modal).toString();
								Log.d("DefaultEffectHttpListener", s);
							}
							
							rootFrameLayout.removeView(modal);
							rootFrameLayout.invalidate();
							//System.out.println("+++++++++++++++++hide progressBar-myacitvity-"+myActivity +"+++++++++++++++++++++++++");
							myActivity.onHideModal();
						}
					});
					waitTime = WAIT_TIME;
					hasThread = false;
				}
				 else{
					 //System.out.println("-----thread exit because of waittime=-1-------");
				 }
			}
			}
		}

		public State(BaseActivity myactivity)
		{
			super();
			waitTime = WAIT_TIME;
			RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layoutParams = layoutparams;
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			myActivity = myactivity;
		}
		
	}

	private static final Map stateMap = Collections.synchronizedMap(new HashMap());
	private BaseActivity myActivity;
	private HttpGroup.OnEndListener onEndListener;
	private HttpGroup.OnErrorListener onErrorListener;
	private HttpGroup.OnStartListener onStartListener;

	public DefaultEffectHttpListener(HttpSetting httpsetting, BaseActivity myactivity) {
		if (httpsetting != null) {
			onStartListener = httpsetting.getOnStartListener();
			onEndListener = httpsetting.getOnEndListener();
			onErrorListener = httpsetting.getOnErrorListener();
		}
		myActivity = myactivity;
		myactivity.addDestroyListener(this);
	}
	
	public DefaultEffectHttpListener(BaseActivity myactivity) {
		onStartListener = null;
		onEndListener = null;
		onErrorListener = null;

		myActivity = myactivity;
		myactivity.addDestroyListener(this);
	}

	private void missionBegins()
	{
		synchronized(stateMap) {
			if (myActivity != null)
			{
				State state = null;
				//synchronized(stateMap) {
					if (Log.D)
					{
						StringBuilder stringbuilder = new StringBuilder("state get with -->> ");
						String s = stringbuilder.append(myActivity).toString();
						Log.d("DefaultEffectHttpListener", s);
					}
				
					state = (State)stateMap.get(myActivity);
					if (Log.D)
					{
						String s1 = (new StringBuilder("state get -->> ")).append(state).toString();
						Log.d("DefaultEffectHttpListener", s1);
					}
					if (state == null)
					{
						state = new State(myActivity);
						stateMap.put(myActivity, state);
					}				
				//}
				
				state.addMission();
			}
		}
	}
	
	private void missionComplete()
	{
		synchronized(stateMap) {
			if (myActivity != null)
			{
				((State)stateMap.get(myActivity)).removeMission();
			}
		}
	}
	
	public void onDestroy() {
		synchronized(stateMap) {
			stateMap.remove(myActivity);
			myActivity = null;
		}
	}

	public void onEnd(HttpResponse httpresponse) {
		if (onEndListener != null)
			onEndListener.onEnd(httpresponse);
		missionComplete();
	}

	public void onError(HttpError httperror) {
		if (onErrorListener != null)
			onErrorListener.onError(httperror);
		missionComplete();
	}

	public void onStart() {
		missionBegins();
		if (onStartListener != null)
			onStartListener.onStart();
	}

}
