package com.hengxuan.ehealthplatform.lens;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.lens.MyDataBaseContract.ImagesInfo;
import com.hengxuan.ehealthplatform.utils.MyAsynImageLoader;

public class LensPhotoList extends BaseActivity {

	//照片类别
	private int photoClassId;
	private ListView mListView;
	SQLiteDatabase db;
	MyAsynImageLoader myAsynImageLoader;
	
	String[] projection = {
			ImagesInfo._ID,
			ImagesInfo.COLUMN_NAME_OWNER,
			ImagesInfo.COLUMN_NAME_TAG,
			ImagesInfo.COLUMN_NAME_DATA,
			ImagesInfo.COLUMN_NAME_DATE,
			ImagesInfo.COLUMN_NAME_TYPE
	};
	private ArrayList<Map<String, String>> photoInfos = new ArrayList<Map<String,String>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContent(R.layout.simple_list);
		Intent intent = getIntent();
		photoClassId = intent.getIntExtra("index", 0);
		
		myAsynImageLoader = new MyAsynImageLoader(this);
		final MyDbHelper myDbHelper = new MyDbHelper(this);
		db = myDbHelper.getReadableDatabase();
		queryDb(photoClassId);
		
		mListView = (ListView)findViewById(R.id.expandlist);
		mListView.setAdapter(new MyListAdatper());
		
	}
	
	private void queryDb(int type){
		Cursor c = null;
		if(type == 0){
			c = db.query(ImagesInfo.TABLE_NAME, projection, null, null, null, null, null);
		}else{
			c = db.query(ImagesInfo.TABLE_NAME, projection, "type="+type, null, null, null, "_id desc");
		}

		if(!c.moveToFirst())return;
		do{
			long itemId = c.getLong(c.getColumnIndexOrThrow(ImagesInfo._ID));
			String name = c.getString(c.getColumnIndexOrThrow(ImagesInfo.COLUMN_NAME_OWNER));
			String tag = c.getString(c.getColumnIndexOrThrow(ImagesInfo.COLUMN_NAME_TAG));
			String path = c.getString(c.getColumnIndexOrThrow(ImagesInfo.COLUMN_NAME_DATA));
			String date = c.getString(c.getColumnIndexOrThrow(ImagesInfo.COLUMN_NAME_DATE));
			int t = c.getInt(c.getColumnIndexOrThrow(ImagesInfo.COLUMN_NAME_TYPE));
			Log.d("daizhx", itemId + ":" + name + ":" + tag + ":" + path + ":" + date + ":" + t);
			Map<String, String> map = new HashMap<String, String>();
			map.put("name", name);
			map.put("tag", tag);
			map.put("date", date);
			map.put("path", path);
			photoInfos.add(map);
		}while(c.moveToNext());
	}
	
	class MyListAdatper extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photoInfos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Holder holder;
//			View view = getLayoutInflater().inflate(R.layout.list_item_photo, null);
			if(convertView == null){
				convertView = getLayoutInflater().inflate(R.layout.list_item_photo, null);
				holder = new Holder();
				holder.name = (TextView)convertView.findViewById(R.id.tv_name);
				holder.extraText = (TextView)convertView.findViewById(R.id.tv_extra);
				holder.dateTime = (TextView)convertView.findViewById(R.id.tv_date);
				holder.iv = (ImageView)convertView.findViewById(R.id.iv_photo);
				convertView.setTag(holder);
			}else{
				holder = (Holder)convertView.getTag();
			}
			//populate data
			holder.name.setText(photoInfos.get(position).get("name"));
			holder.extraText.setText(photoInfos.get(position).get("tag"));
			holder.dateTime.setText(photoInfos.get(position).get("date"));
			
			//获取图片路径，异步加载
			String path = photoInfos.get(position).get("path");
			myAsynImageLoader.loadBitmap(path, holder.iv, 200, 200);
			return convertView;
		}
		
		class Holder{
			public ImageView iv;
			public TextView name;
			public TextView extraText;
			public TextView dateTime;
		}
	}
}
