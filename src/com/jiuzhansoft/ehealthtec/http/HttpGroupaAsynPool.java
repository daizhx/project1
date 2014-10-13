package com.jiuzhansoft.ehealthtec.http;

import android.app.Activity;

import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.utils.PooledThread;
import com.jiuzhansoft.ehealthtec.http.utils.ThreadPool;
import com.jiuzhansoft.ehealthtec.log.Log;

public class HttpGroupaAsynPool extends HttpGroup {
	
	public HttpGroupaAsynPool(HttpGroupSetting paramHttpGroupSetting) {
		super(paramHttpGroupSetting);
	}
	
	
	@Override
	public void execute(final HttpRequest httpRequest) {
		if (Log.D) { 
			Log.d("HttpGroupaAsynPool", "execute");
		}
		
		ThreadPool threadpool = PooledThread.getThreadPool();
		int i = httpRequest.getHttpSetting().getPriority();
		threadpool.offerTask(new Runnable() {

			@Override
			public void run() {
				if (Log.D) { 
					Log.d("HttpGroupaAsynPool", "execute.run");
				}
				// TODO Auto-generated method stub
				if (httpList.size() < 1)
					onStart();
				httpList.add(httpRequest);
				httpRequest.nextHandler();

			}

		}, i);
	}

}