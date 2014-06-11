package com.hengxuan.ehealthplatform.activity;

import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.MassageActivity.GridAdapter;
import com.hengxuan.ehealthplatform.activity.MassageActivity.GridAdapter.Holder;
import com.hengxuan.ehealthplatform.lens.LensBaseActivity;
import com.hengxuan.ehealthplatform.lens.LensConnectActivity;
import com.hengxuan.ehealthplatform.lens.LensShootBaseActivity;
import com.hengxuan.ehealthplatform.lens.iris.IrisInspectionActivity;
import com.hengxuan.ehealthplatform.massager.MassagerActivity;
import com.hengxuan.ehealthplatform.update.UpdateManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PhysicalExamActivity extends BaseActivity {
	// lens class index
	private int index;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		setTitle(R.string.instruments);
		setLeftIconClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent parentIntent = new Intent(PhysicalExamActivity.this,
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
				switch (position) {
				case 0:
					index = LensShootBaseActivity.IRIS_PHOTO_INDEX;
					break;
				case 1:
					index = LensShootBaseActivity.SKIN_PHOTO_INDEX;
					break;
				case 2:
					index = LensShootBaseActivity.HAIR_PHOTO_INDEX;
					break;
				case 3:
					index = LensShootBaseActivity.NAEVUS_PHOTO_INDEX;

					break;
				case 4:
					return;
				case 5:
					return;
				}
				Intent intent = new Intent(PhysicalExamActivity.this,
						LensBaseActivity.class);
				intent.putExtra("index", index);
				startActivity(intent);
			}

		});

		// ¸üÐÂ
		UpdateManager updateManager = UpdateManager.getUpdateManager(this);
		updateManager.checkAndUpdate();

	}

	class GridAdapter extends BaseAdapter {
		private Context mContext;

		public GridAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 6;
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
			holder.icon.setImageResource(R.drawable.massager_icon);
			holder.labelText.setText(R.string.iris_detection);
			// TODO
			switch (position) {
			case 0:
				holder.icon.setImageResource(R.drawable.lens_icon);
				holder.labelText.setText(R.string.iris_detection);
				break;
			case 1:
				holder.icon.setImageResource(R.drawable.lens_icon);
				holder.labelText.setText(R.string.skin_detection);
				break;
			case 2:
				holder.icon.setImageResource(R.drawable.lens_icon);
				holder.labelText.setText(R.string.hair_detection);
				break;
			case 3:
				holder.icon.setImageResource(R.drawable.lens_icon);
				holder.labelText.setText(R.string.naevus_detection);
				break;
			case 4:
				holder.icon.setImageResource(R.drawable.lens_icon);
				holder.labelText.setText(R.string.blood_pressure_monitor);
				break;
			case 5:
				holder.icon.setImageResource(R.drawable.lens_icon);
				holder.labelText.setText(R.string.weighting_scale);
				break;

			default:
				break;
			}
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
			Intent parentIntent = new Intent(PhysicalExamActivity.this, MainActivity.class);
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
