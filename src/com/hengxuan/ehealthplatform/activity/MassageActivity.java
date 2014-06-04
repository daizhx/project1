package com.hengxuan.ehealthplatform.activity;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.application.EHTApplication;
import com.hengxuan.ehealthplatform.log.Log;
import com.hengxuan.ehealthplatform.massager.MassagerActivity;
import com.hengxuan.ehealthplatform.update.UpdateManager;


import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MassageActivity extends BaseActivity{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		mTitle.setText(R.string.massage);
		GridView gridView = (GridView)findViewById(R.id.gridview);
		gridView.setAdapter(new GridAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(position == 0){
					startActivity(new Intent(MassageActivity.this, MassagerActivity.class));
				}
			}
			
		});
		
		//¸üÐÂ
		UpdateManager updateManager = UpdateManager.getUpdateManager(this);
		updateManager.checkAndUpdate();
	}

	class GridAdapter extends BaseAdapter{
		private Context mContext;
		
		public GridAdapter(Context context){
			this.mContext = context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
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
			if(convertView == null){
				holder = new Holder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item, null);
				holder.icon = (ImageView)convertView.findViewById(R.id.icon);
				holder.hintIcon = (ImageView)convertView.findViewById(R.id.hint);
				holder.labelText = (TextView)convertView.findViewById(R.id.labelText);
				convertView.setTag(holder);
			}else{
				holder = (Holder)convertView.getTag();
			}
			holder.icon.setImageResource(R.drawable.massager_icon);
			holder.labelText.setText(R.string.massager);
			//TODO
			if(position == 1){
				holder.hintIcon.setVisibility(View.VISIBLE);
				holder.labelText.setText(R.string.music_massage);
			}
			return convertView;
		}
		
		class Holder{
			public ImageView icon;
			public ImageView hintIcon;
			public TextView labelText;
		}
	}

}
