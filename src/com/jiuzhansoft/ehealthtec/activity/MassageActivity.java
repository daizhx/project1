package com.jiuzhansoft.ehealthtec.activity;

import java.util.ArrayList;

import com.jiuzhansoft.ehealthtec.MainActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.constant.ConstEquipId;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.massager.MassagerActivity;
import com.jiuzhansoft.ehealthtec.massager.musicMassage.MusicMassagerActivity;
import com.jiuzhansoft.ehealthtec.product.Product;
import com.jiuzhansoft.ehealthtec.update.UpdateManager;
import com.jiuzhansoft.ehealthtec.utils.CommonUtil;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

public class MassageActivity extends BaseActivity {
	//产品列表
	private ArrayList<Product> products = new ArrayList<Product>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		mTitle.setText(R.string.massage);
		setLeftIconClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent parentIntent = new Intent(MassageActivity.this,
						MainActivity.class);
				parentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(parentIntent);
				finish();
			}
		});
		GridView gridView = (GridView) findViewById(R.id.gridview);
		gridView.setAdapter(new GridAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				if (position == 0) {
//					startActivity(new Intent(MassageActivity.this,
//							MassagerActivity.class));
//				} else if (position == 1) {
//					startActivity(new Intent(MassageActivity.this,
//							MusicMassagerActivity.class));
//				}
				Product product = products.get(position);
				if(product.isVerificated){
					products.get(position).EntryProduct(MassageActivity.this);
				}else{
					CommonUtil.verifyProduct(MassageActivity.this, product);
					
				}
			}

		});

		//查询注册的按摩产品
		for(Product p:EHTApplication.productList){
			if(p.mTypeId == ConstEquipId.MASSAGEID){
				products.add(p);
			}
		}
	}

	class GridAdapter extends BaseAdapter {
		private Context mContext;

		public GridAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return products.size();
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
			if (convertView == null) {
				holder = new Holder();
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.gridview_item, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.hintIcon = (ImageView) convertView
						.findViewById(R.id.hint);
				holder.labelText = (TextView) convertView
						.findViewById(R.id.labelText);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
//			if (position == 0) {
//				holder.icon.setImageResource(R.drawable.massager_icon);
//				holder.labelText.setText(R.string.massager);
//			}
//			// TODO
//			if (position == 1) {
//				holder.icon.setImageResource(R.drawable.music_massage_entry);
//				// holder.hintIcon.setVisibility(View.VISIBLE);
//				holder.labelText.setText(R.string.music_massage);
//			}
			
			holder.icon.setImageBitmap(products.get(position).getLogo());
			holder.labelText.setText(products.get(position).name);
			return convertView;
		}

		class Holder {
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
			Intent parentIntent = new Intent(MassageActivity.this,
					MainActivity.class);
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
