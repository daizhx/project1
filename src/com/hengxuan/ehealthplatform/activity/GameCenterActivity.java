package com.hengxuan.ehealthplatform.activity;

import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.MassageActivity.GridAdapter;
import com.hengxuan.ehealthplatform.activity.MassageActivity.GridAdapter.Holder;
import com.hengxuan.ehealthplatform.game.Hamster;
import com.hengxuan.ehealthplatform.massager.MassagerActivity;
import com.hengxuan.ehealthplatform.massager.musicMassage.MusicMassagerActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GameCenterActivity extends BaseActivity {

	private GridView mGridView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.game);
		setContentView(R.layout.activity_grid);
		mGridView = (GridView)findViewById(R.id.gridview);
		mGridView.setAdapter(new GridAdapter(this));
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				if(position == 0){
					startActivity(new Intent(GameCenterActivity.this, Hamster.class));
				}
			}
			
		});
	}
	
	class GridAdapter extends BaseAdapter{
		private Context mContext;
		
		public GridAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mContext = context;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 1;
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
			
			holder.icon.setImageResource(R.drawable.hamster);
			holder.labelText.setText(R.string.hamster);
			return convertView;
		}
		
		class Holder{
			public ImageView icon;
			public ImageView hintIcon;
			public TextView labelText;
		}
		
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent parentIntent = new Intent(GameCenterActivity.this, MainActivity.class);
			parentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(parentIntent);
			finish();
			break;
			
		default:
			break;
		}
		
		return super.onKeyUp(keyCode, event);
	}
}
