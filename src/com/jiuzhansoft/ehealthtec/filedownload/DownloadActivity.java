package com.jiuzhansoft.ehealthtec.filedownload;

import java.io.File;

import javax.annotation.Resource;
import javax.swing.text.View;

import com.jiuzhansoft.ehealthtec.R;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Toast;

public class DownloadActivity extends Activity {
	private EditText downloadPathText;
	private Button mButton;
	private ProgressBar mProgressBar;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mProgressBar.setProgress(msg.getData().getInt("size"));
				//float num = mProgressBar.getProgress()/mProgressBar.getMax();
				if(mProgressBar.getProgress() == mProgressBar.getMax()){
					Toast.makeText(DownloadActivity.this, "done", 1000).show();
				}
				break;

			case -1:
				Toast.makeText(DownloadActivity.this, "error", 1000).show();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.text_view);
//		mProgressBar = (ProgressBar)findViewById(R.id.progress);
//		downloadPathText = (EditText)findViewById(R.id.et);
		downloadPathText.setText("http://www.eht.hk/massage.apk");
		mButton = (Button)findViewById(R.id.btn);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(android.view.View arg0) {
				// TODO Auto-generated method stub
				String path = downloadPathText.getText().toString();
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					download(path, Environment.getExternalStorageDirectory());
				}else{
					Toast.makeText(DownloadActivity.this, "sdcard error", 1000).show();
				}
				
			}
		});

		
	}
	
	private void download(final String path, final File saveDir){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				FileDownloader loader = new FileDownloader(DownloadActivity.this, path, saveDir, 2);
				int fileSize = loader.getFileSize();
				mProgressBar.setMax(fileSize);
				try {
					loader.download(new DownloadProgressListener() {
						
						@Override
						public void onDownloadSize(int Size) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", Size);
							handler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.obtainMessage(-1).sendToTarget();
					Log.d("FileDownloader", Log.getStackTraceString(e));
				}
			}
		}).start();
	}
}
