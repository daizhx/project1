package com.jiuzhansoft.ehealthtec.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.jiuzhansoft.ehealthtec.MainActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * 更新应用版本
 * @author Administrator
 *
 */
public class UpdateService extends Service {
	private static final String TAG = "update";
	private String appName;
	private File updateFile;
	private String downloadUrl;
	private Notification mNotification;
	private PendingIntent mPendingIntent;
	private static final int DOWN_OK = 0;
	private static final int DOWN_ERROR = 1;
	private static final int TIMEOUT = 2000;
	private int notification_id = 0;
	private NotificationManager manager;
	
	private Notification nf;
	private RemoteViews rvs;
	/** 是否已下载*/
	private boolean done = false;
	private Intent onClickIntent; //下载中点击
	private Intent onClearIntent; //下载完成后点击

	// SD卡状态
	private int hasSD = 1; // 1.SD卡 2.手机内存
	private String apkPath;
	private String newVersionName; //版本名称
	private int progress = 0;
	private long downloadLenght = 0;
	public boolean updateFlag = true;
	private HttpGet get;
	private String fileDir,filePath;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		apkPath = intent.getStringExtra("apkPath");
		newVersionName = apkPath.substring(apkPath.lastIndexOf("/") + 1);
		
	        String sdcardState = Environment.getExternalStorageState();
	        if (sdcardState.equals("mounted")) {// SD是否挂载
	            String sdcardPath = Environment.getExternalStorageDirectory().toString();
	            fileDir = sdcardPath + "/eht";
	            filePath = fileDir + "/" + newVersionName;
	            onClickIntent.putExtra("filePath", filePath);
	            onClickIntent.putExtra("intent", intent);
	            onClearIntent.putExtra("intent", intent);
	            onClickIntent.putExtra("done", done);
	            onClearIntent.putExtra("done", done);

	            rvs.setTextViewText(R.id.tv_new_version_name, newVersionName);
	            rvs.setTextViewText(R.id.tv_progress, progress + "%");
	            rvs.setProgressBar(R.id.pb_download_progress, 100, progress, false);

	            nf.icon = R.drawable.ic_launcher;
	            nf.tickerText = "正在下载新版本";
	            nf.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
	            nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;

	            //通知栏内的布局view
	            nf.contentView = rvs;
	            // 当点击通知栏的通知时就会跳转到依赖的服务(UpdateNewVersionService)
	            nf.contentIntent = PendingIntent.getService(this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	            // 当清除通知栏的通知时就会跳转到依赖的服务(EndService)
	            nf.deleteIntent = PendingIntent.getService(this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);

	            if (downloadLenght == 0) {
	                if (!done) {
	                    manager.notify(1, nf);
	                    new DownloadNewApkTask().execute();
	                } else {
	                    Toast.makeText(this, "已经完成下载", Toast.LENGTH_SHORT).show();
	                }
	            }
	        } else {
	            hasSD = 2;
	            //没有SD卡
	            String sdcardPath = Environment.getDataDirectory().toString();
	            fileDir = getCacheDir().getAbsolutePath();
	            // File cache = getCacheDir() ;
	            filePath = fileDir + "/" + newVersionName;
	            onClickIntent.putExtra("filePath", filePath);
	            onClickIntent.putExtra("intent", intent);
	            onClearIntent.putExtra("intent", intent);
	            onClickIntent.putExtra("done", done);
	            onClearIntent.putExtra("done", done);

	            rvs.setTextViewText(R.id.tv_new_version_name, newVersionName);
	            rvs.setTextViewText(R.id.tv_progress, progress + "%");
	            rvs.setProgressBar(R.id.pb_download_progress, 100, progress, false);

	            nf.icon = R.drawable.ic_launcher;
	            nf.tickerText = "正在下载新版本";
	            nf.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
	            nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;

	            nf.contentView = rvs;
	            nf.contentIntent = PendingIntent.getService(this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	            nf.deleteIntent = PendingIntent.getService(this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);

	            if (downloadLenght == 0) {
	                if (!done) {
	                    manager.notify(1, nf);
	                    new DownloadNewApkTask().execute();
	                } else {
	                    Toast.makeText(this, "已经完成下载", Toast.LENGTH_SHORT).show();
	                }
	            }
	        }
	        return START_NOT_STICKY;
		
	}

	@Override
	public void onCreate() {
		super.onCreate();
        Log.d(TAG, "UpdateVersionService onCreate!");
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nf = new Notification();
		rvs = new RemoteViews(getPackageName(), R.layout.notification_view);
//		onClickIntent = new Intent(this, OnClickNotificationService.class);
//		onClearIntent = new Intent(this, OnClearNotificationService.class);
	}
	
	/** 下载新版本任务线程 */
	private class DownloadNewApkTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			updateFlag = false;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				HttpClient client = new DefaultHttpClient();
				/*
				 * get = new HttpGet(Config.UPDATE_SERVER +
				 * Config.UPDATE_APKNAME);
				 */
				get = new HttpGet(apkPath);
				HttpResponse response = client.execute(get);
				long totalLenght = response.getEntity().getContentLength();
				InputStream inputStream = response.getEntity().getContent();

				File file = new File(fileDir);
				if (!file.exists()) {
					file.mkdirs();
				}
				FileOutputStream outputStream = new FileOutputStream(filePath);

				long ctm = 0;
				int lenght = -1;
				byte[] buffer = new byte[1024];
				while ((lenght = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, lenght);
					downloadLenght += lenght;

					double percent = 100 * downloadLenght / totalLenght;
					String percent2 = String.valueOf(percent);
					if (percent2.contains(".")) {
						percent2 = percent2.substring(0, percent2.indexOf("."));
					}
					progress = Integer.valueOf(percent2);
					if ((System.currentTimeMillis() - ctm) >= 1000) {//每一秒更新一次进度条
						publishProgress(0);
						ctm = System.currentTimeMillis();
					}

					if (downloadLenght == totalLenght) {// 下载成功
						publishProgress(1);
					}
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			// 更新通知栏
			rvs.setTextViewText(R.id.tv_new_version_name, newVersionName);
			rvs.setTextViewText(R.id.tv_progress, progress + "%");
			rvs.setProgressBar(R.id.pb_download_progress, 100, progress, false);

			manager.notify(1, nf);

			if (values[0] == 1) {

				updateFlag = true;

				progress = 0;
				downloadLenght = 0;

				done = true;
				onClickIntent.putExtra("done", done);
				onClearIntent.putExtra("done", done);

				manager.cancel(1);

//				nf.icon = R.drawable.ic_launcher;
//				rvs.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
//				nf.tickerText = "下载成功";
//				nf.defaults = Notification.DEFAULT_LIGHTS;
//				nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//				nf.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.finish);
//				nf.contentIntent = PendingIntent.getService(UpdateVersionService.this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//				nf.deleteIntent = PendingIntent.getService(UpdateVersionService.this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//				manager.notify(2, nf);
				// 清除图片缓存 （异步操作）
				// new RemoveCache().execute();

				if (hasSD == 1) {
					Intent it = new Intent(Intent.ACTION_VIEW);
					it.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(it);
				} else { // hasSD = 2 ;
					String localfile = "";
					localfile = getCacheDir() + "/" + newVersionName;
                    // chmod 755 /* 755 权限是对apk自身应用具有所有权限， 对组和其他用户具有读和执行权限 */
					String cmd = "chmod 777 " + localfile;
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
						e.printStackTrace();
					}

					Intent it = new Intent(Intent.ACTION_VIEW);
					File file = new File(localfile);
					it.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(it);
				}
			}
		}
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
					manager.notify(notification_id, mNotification);
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
				manager.notify(notification_id, mNotification);
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
		manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification = new Notification();
		mNotification.icon = R.drawable.ic_launcher;
		mNotification.tickerText = getString(R.string.downloading);
		Intent updateIntent = new Intent(this, MainActivity.class);
		updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		mNotification.contentIntent = mPendingIntent;
		manager.notify(notification_id, mNotification);
	}
	
	
}
