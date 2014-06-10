package com.hengxuan.ehealthplatform.massager.musicMassage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.bluetooth.BluetoothServiceProxy;
import com.hengxuan.ehealthplatform.massager.musicMassage.MusicService.MusicServiceBinder;
import com.hengxuan.ehealthplatform.massager.musicMassage.MusicService.OnSongChangedListener;
import com.hengxuan.ehealthplatform.massager.musicMassage.MusicService.OnUpdatePlaytime;
import com.hengxuan.ehealthplatform.user.UserLoginActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;


public class MusicMassagerActivity extends SlidingActivity implements
		OnClickListener {

	String[] mCursorCols = new String[] { "audio._id AS _id",
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.DURATION };
	private Cursor cursor;
	private ImageView mPrevious;
	private ImageView mPlay;
	private ImageView mNext;
	private int mDisplayWidth;
	private Object maxVolume;
	private AudioManager audioMgr;
	private String[] musicTitle;
	private String[] musicArtist;
	private ListView songList;
	private List<HashMap<String, String>> datalist;
	private SlidingMenu mSlideMenu;
	//play status，0-stop，1-play，2-pause
	private int playStatus;
	private static final int PLAYING = 1;
	private static final int PAUSING = 2;
	// 是否来电
	private boolean isring = false;
	private ComponentName component;
	
	private ImageView icList;
	
	//show the play song and artist
	private TextView tvSong;
	private TextView tvArtist;
	
	//inference to musicService
	private MusicService musicService;
	//当前播放歌曲
	private int currentPosition;
	private TextView tvPlaytime;
	private TextView tvDuration;
	private SeekBar mSeekBar;
	
	private ImageView btIndicator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initActionBar();
		if (!MusicService
				.isServiceRunning(MusicMassagerActivity.this,
						"com.hengxuan.ehealthplatform.massager.musicMassage.MusicService")) {
			Log.e("isRunning", "running");
			try {
				BluetoothServiceProxy
						.sendCommandToDevice(BluetoothServiceProxy.MODE_TAG_8);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		component = new ComponentName(this, MusicService.class);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		mDisplayWidth = displayMetrics.widthPixels;
		setContentView(R.layout.actvity_music_massage);
		setBehindContentView(R.layout.song_list_board);
		mSlideMenu = getSlidingMenu();
		mSlideMenu.setBehindOffset(mDisplayWidth / 4);
		// 关闭手势
		mSlideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		//右边滑出
		mSlideMenu.setMode(SlidingMenu.RIGHT);
		setUp();
		
		//bind MusicService
		Intent intent = new Intent();
		intent.setComponent(component);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			musicService = ((MusicServiceBinder)service).getService();
			musicService.setSongChangedListener(new OnSongChangedListener() {
				
				@Override
				public void onSongChanged(int position, int duration) {
					// TODO Auto-generated method stub
					HashMap<String, String> infomap = datalist.get(position);
					String musicname = infomap.get(getResources().getString(R.string.musicname));
					tvSong.setText(musicname);
					String artist = infomap.get(getResources().getString(R.string.musicartist));
					tvArtist.setText(artist);
					SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
					String time = sdf.format(duration);
					tvDuration.setText(time);
					mSeekBar.setMax(duration);
					
				}
			});
			musicService.setOnUpdatePlaytime(new OnUpdatePlaytime() {
				
				@Override
				public void updatePlaytime(int time) {
					// TODO Auto-generated method stub
					SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
					String stime = sdf.format(time);
					tvPlaytime.setText(stime);
					mSeekBar.setProgress(time);
				}
			});
		}
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//关闭音乐  not require, it is binded
//		Intent intent = new Intent();
//		intent.setComponent(component);
//		stopService(intent);
		
		SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("currentPosition", currentPosition);
		editor.commit();
		
		unbindService(conn);
		super.onDestroy();
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		// 采用此方法在第一次进入该界面时，还是会先闪一下home icon和title
		// actionBar.setDisplayShowHomeEnabled(false);
		// actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		View view = getLayoutInflater().inflate(R.layout.action_bar, null);
		LayoutParams lp = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		actionBar.setCustomView(view, lp);
		ImageView leftIcon = (ImageView)view.findViewById(R.id.left_icon);
		leftIcon.setImageResource(R.drawable.ic_action_back);
		leftIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		((TextView)view.findViewById(R.id.title)).setText(R.string.music_massage);
		btIndicator = (ImageView)view.findViewById(R.id.right_icon);
		btIndicator.setVisibility(View.VISIBLE);
	}

	private void setUp() {
		// TODO Auto-generated method stub
		// 获取播放列表
		Uri MUSIC_URL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		cursor = getContentResolver().query(MUSIC_URL, mCursorCols,
				"duration > 60000", null, null);

		// 初始化控件
		mPrevious = (ImageView) findViewById(R.id.previous);
		mPlay = (ImageView) findViewById(R.id.play);
		mNext = (ImageView) findViewById(R.id.next);
		mPrevious.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mNext.setOnClickListener(this);
		
		tvSong = (TextView)findViewById(R.id.song);
		tvArtist = (TextView)findViewById(R.id.artist);
		tvDuration = (TextView)findViewById(R.id.duration);

		audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		musicTitle = new String[cursor.getCount()];
		musicArtist = new String[cursor.getCount()];

		songList = (ListView) findViewById(R.id.list);
		datalist = getInfoArray();
		SimpleAdapter adapter = new SimpleAdapter(MusicMassagerActivity.this,
				datalist, R.layout.musiclistview, new String[] { getResources()
						.getString(R.string.musicname) },
				new int[] { R.id.musicid });
		songList.setAdapter(adapter);
		songList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				mPlay.setImageResource(R.drawable.pause);
//				SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
//				SharedPreferences.Editor editor = preferences.edit();
//				editor.putInt("currentPosition", position);
//				editor.putBoolean("isClick", true);
//				editor.commit();
//				Intent intent = new Intent(MusicService.PLAY_RAMMUSIC);
//				intent.setComponent(component);
//				startService(intent);
				musicService.playMusic(position);
				currentPosition = position;
			}			
		});
		
		icList = (ImageView)findViewById(R.id.song_list);
		icList.setOnClickListener(this);
		
		//get the last play song info, or display the 1th song info of datalist
		SharedPreferences preferences = getSharedPreferences("getPosition", MODE_WORLD_READABLE);
		int lastPosition = preferences.getInt("currentPosition", -1);
		if(!datalist.isEmpty()){
			if(lastPosition == -1){
				HashMap<String, String> infomap = datalist.get(0);
				String musicname = infomap.get(getResources().getString(R.string.musicname));
				tvSong.setText(musicname);
				String artist = infomap.get(getResources().getString(R.string.musicartist));
				tvArtist.setText(artist);
			}else{
				HashMap<String, String> infomap = datalist.get(lastPosition);
				String musicname = infomap.get(getResources().getString(R.string.musicname));
				tvSong.setText(musicname);
				String artist = infomap.get(getResources().getString(R.string.musicartist));
				tvArtist.setText(artist);
			}
		}
		mSeekBar = (SeekBar)findViewById(R.id.progress_bar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				//play();

			}

			public void onStartTrackingTouch(SeekBar seekBar) {
//				pause();

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
//				if (fromUser) {
//					seekbar_change(progress);
//				}

			}
		});
		tvPlaytime = (TextView)findViewById(R.id.playtime);
		
	}

	private List<HashMap<String, String>> getInfoArray() {
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < cursor.getCount(); i++) {
			HashMap<String, String> infoMap = new HashMap<String, String>();
			cursor.moveToPosition(i);
			int titleColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE);
			int artistColumn = cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST);
			musicArtist[i] = cursor.getString(artistColumn);
			musicTitle[i] = cursor.getString(titleColumn);
			infoMap.put(getResources().getString(R.string.musicname),
					musicTitle[i]);
			infoMap.put(getResources().getString(R.string.musicartist),
					musicArtist[i]);
			aList.add(infoMap);
		}
		Log.i("msg", aList + "");
		return aList;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == mPrevious){
			mPlay.setImageResource(R.drawable.pause);
			playStatus = PLAYING;
//			Intent intent = new Intent(MusicService.PREVIOUS_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.previous();
		}else if(v == mPlay && playStatus == PLAYING){
			mPlay.setImageResource(R.drawable.play);
//			Intent intent = new Intent(MusicService.PLAY_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.pause();
			playStatus = PAUSING;
		}else if(v == mPlay && playStatus != PLAYING){
			mPlay.setImageResource(R.drawable.pause);
//			Intent intent = new Intent(MusicService.PAUSE_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.play();
			playStatus = PLAYING;
		}else if(v == mNext){
			mPlay.setImageResource(R.drawable.pause);
			playStatus = PLAYING;
//			Intent intent = new Intent(MusicService.NEXT_ACTION);
//			intent.setComponent(component);
//			startService(intent);
			musicService.next();
		}else if(v == icList){
			showMenu();
		}
	}
	
	private class MobliePhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:  // 无任何状态时 
				// 通话结束
				if(MusicService.isServiceRunning(MusicMassagerActivity.this,"com.hengxuan.ehealthplatform.massager.musicMassage.MusicService")
						&& playStatus == PAUSING
						&& isring){
					isring = false;
					MusicService.flag = true;
					mPlay.setImageResource(R.drawable.play);
					Intent intent = new Intent(MusicService.PLAY_ACTION);
					intent.setComponent(component);
					startService(intent);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:  // 接起电话时 
				
			case TelephonyManager.CALL_STATE_RINGING:  // 电话进来时 
				// 如果正在听音乐
				if(MusicService.isServiceRunning(MusicMassagerActivity.this,"com.hengxuan.ehealthplatform.massager.musicMassage.MusicService")
						&& playStatus == PLAYING){
					isring = true;
					MusicService.flag = false;
					mPlay.setImageResource(R.drawable.pause);
					Intent intent = new Intent(MusicService.PAUSE_ACTION);
					intent.setComponent(component);
					startService(intent);
				}
				break;
			default:
				break;
			}
		}
	}

}
