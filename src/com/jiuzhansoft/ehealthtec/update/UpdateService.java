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
 * ����Ӧ�ð汾
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
	/** �Ƿ�������*/
	private boolean done = false;
	private Intent onClickIntent; //�����е��
	private Intent onClearIntent; //������ɺ���

	// SD��״̬
	private int hasSD = 1; // 1.SD�� 2.�ֻ��ڴ�
	private String apkPath;
	private String newVersionName; //�汾����
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
	        if (sdcardState.equals("mounted")) {// SD�Ƿ����
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
	            nf.tickerText = "���������°汾";
	            nf.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
	            nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;

	            //֪ͨ���ڵĲ���view
	            nf.contentView = rvs;
	            // �����֪ͨ����֪ͨʱ�ͻ���ת�������ķ���(UpdateNewVersionService)
	            nf.contentIntent = PendingIntent.getService(this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	            // �����֪ͨ����֪ͨʱ�ͻ���ת�������ķ���(EndService)
	            nf.deleteIntent = PendingIntent.getService(this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);

	            if (downloadLenght == 0) {
	                if (!done) {
	                    manager.notify(1, nf);
	                    new DownloadNewApkTask().execute();
	                } else {
	                    Toast.makeText(this, "�Ѿ��������", Toast.LENGTH_SHORT).show();
	                }
	            }
	        } else {
	            hasSD = 2;
	            //û��SD��
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
	            nf.tickerText = "���������°汾";
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
	                    Toast.makeText(this, "�Ѿ��������", Toast.LENGTH_SHORT).show();
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
	
	/** �����°汾�����߳� */
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
					if ((System.currentTimeMillis() - ctm) >= 1000) {//ÿһ�����һ�ν�����
						publishProgress(0);
						ctm = System.currentTimeMillis();
					}

					if (downloadLenght == totalLenght) {// ���سɹ�
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
			// ����֪ͨ��
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
//				nf.tickerText = "���سɹ�";
//				nf.defaults = Notification.DEFAULT_LIGHTS;
//				nf.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//				nf.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.finish);
//				nf.contentIntent = PendingIntent.getService(UpdateVersionService.this, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//				nf.deleteIntent = PendingIntent.getService(UpdateVersionService.this, 0, onClearIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//				manager.notify(2, nf);
				// ���ͼƬ���� ���첽������
				// new RemoveCache().execute();

				if (hasSD == 1) {
					Intent it = new Intent(Intent.ACTION_VIEW);
					it.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
					it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(it);
				} else { // hasSD = 2 ;
					String localfile = "";
					localfile = getCacheDir() + "/" + newVersionName;
                    // chmod 755 /* 755 Ȩ���Ƕ�apk����Ӧ�þ�������Ȩ�ޣ� ����������û����ж���ִ��Ȩ�� */
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
	 * ���߳�����
	 */
	public void createThread() {
		/***
		 * ����UI
		 */
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DOWN_OK:
					// ������ɣ������װ
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
						// ���سɹ�
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
	 * �����ļ�
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public long downloadUpdateFile(String down_url, String file)
			throws Exception {
		int down_step = 5;// ��ʾstep
		int totalSize;// �ļ��ܴ�С
		int downloadCount = 0;// �Ѿ����غõĴ�С
		int updateCount = 0;// �Ѿ��ϴ����ļ���С
		InputStream inputStream;
		OutputStream outputStream;
		URL url = new URL(down_url);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(TIMEOUT);
		httpURLConnection.setReadTimeout(TIMEOUT);
		// ��ȡ�����ļ���size
		totalSize = httpURLConnection.getContentLength();
		if (httpURLConnection.getResponseCode() == 404) {
			throw new Exception("fail!");
		}
		inputStream = httpURLConnection.getInputStream();
		outputStream = new FileOutputStream(file, false);// �ļ������򸲸ǵ�
		byte buffer[] = new byte[1024];
		int readsize = 0;
		while ((readsize = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readsize);
			downloadCount += readsize;// ʱʱ��ȡ���ص��Ĵ�С
			/**
			 * ÿ������5%
			 */
			if (updateCount == 0
					|| (downloadCount * 100 / totalSize - down_step) >= updateCount) {
				updateCount += down_step;
				 //�ı�֪ͨ��
				 mNotification.setLatestEventInfo(this, "��������...", updateCount
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
