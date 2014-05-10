package com.hengxuan.ehealthplatform.http;

import com.hengxuan.ehealthplatform.http.utils.PooledThread;
import com.hengxuan.ehealthplatform.http.utils.ThreadPool;
import com.hengxuan.ehealthplatform.log.Log;




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
	@Override
	public void onDestroy() {
	}
}