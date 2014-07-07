package com.hengxuan.ehealthplatform.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class UpdateService extends Service {
	private String appName;
	private File updateFile;
	private String downloadUrl;
	private Notification mNotification;
	private PendingIntent mPendingIntent;
	private static final int DOWN_OK = 0;
	private static final int DOWN_ERROR = 1;
	private static final int TIMEOUT = 2000;
	private int notification_id = 0;
	private NotificationManager notificationManager;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		appName = intent.getStringExtra("app_name");
		downloadUrl = intent.getStringExtra("url");
		createFile(appName);
		createNotification();
		createThread();//线程下载
		return super.onStartCommand(intent, flags, startId);
	}


	/***
	 * 开线程下载
	 */
	public void createThread() {
		/***
		 * 更新UI
		 */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DOWN_OK:
					// 下载完成，点击安装
					Uri uri = Uri.fromFile(updateFile);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri,
							"application/vnd.android.package-archive");
					mPendingIntent = PendingIntent.getActivity(
							UpdateService.this, 0, intent, 0);
					mNotification.setLatestEventInfo(UpdateService.this,
							getText(R.string.app_name), getText(R.string.download_complete), mPendingIntent);
					notificationManager.notify(notification_id, mNotification);
					stopSelf();
					break;
				case DOWN_ERROR:
					mNotification.setLatestEventInfo(UpdateService.this,
							getText(R.string.app_name), getText(R.string.download_fail), mPendingIntent);
					break;
				default:
					stopSelf();
					break;
				}
			}
		};
		final Message message = new Message();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					long downloadSize = downloadUpdateFile(downloadUrl,
							updateFile.toString());
					if (downloadSize > 0) {
						// 下载成功
						message.what = DOWN_OK;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					message.what = DOWN_ERROR;
					handler.sendMessage(message);
				}
			}
		}).start();
	}

	/***
	 * 下载文件
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)
			throws Exception {
		int down_step = 5;// 提示step
		int totalSize;// 文件总大小
		int downloadCount = 0;// 已经下载好的大小
		int updateCount = 0;// 已经上传的文件大小
		InputStream inputStream;
		OutputStream outputStream;
		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// 获取下载文件的size
		totalSize = httpURLConnection.getContentLength();
		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");
		}
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
		byte buffer[] = new byte[1024];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// 时时获取下载到的大小
			/**
			 * 每次增张5%
			 */
			if (updateCount == 0
					|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				 //改变通知栏
				 mNotification.setLatestEventInfo(this, "正在下载...", updateCount
				 + "%" + "", mPendingIntent);
//				contentView.setTextViewText(R.id.notificationPercent,
//						updateCount + "%");
//				contentView.setProgressBar(R.id.notificationProgress, 100,
//						updateCount, false);
				// show_view
				notificationManager.notify(notification_id, mNotification);
			}
		}
		if (httpURLConnection != null) {
			httpURLConnection.disconnect();
		}
		inputStream.close();
		outputStream.close();
		return downloadCount;
	}


	private void createFile(String appName2) {
		// TODO Auto-generated method stub
		String path = Environment.getExternalStorageDirectory() + File.separator + "eht" + File.separator + appName2; 
		updateFile = new File(path);
	}

	private void createNotification() {
		// TODO Auto-generated method stub
		notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = new Notification();
		mNotification.icon = R.drawable.ic_launcher;
		mNotification.tickerText = getString(R.string.downloading);
		Intent updateIntent = new Intent(this, MainActivity.class);
		updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		mNotification.contentIntent = mPendingIntent;
		notificationManager.notify(notification_id, mNotification);
	}
	
	
}
