package com.hengxuan.ehealthplatform.activity;

import com.hengxuan.ehealthplatform.MainActivity;
import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.PhysicalExamActivity.GridAdapter;
import com.hengxuan.ehealthplatform.activity.PhysicalExamActivity.GridAdapter.Holder;
import com.hengxuan.ehealthplatform.lens.hair.HairReportActivity;
import com.hengxuan.ehealthplatform.lens.iris.IrisReportActivity;
import com.hengxuan.ehealthplatform.lens.skin.SkinReportActivity;

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

/**
 * 体检报告窗口
 * 
 * @author Administrator
 * 
 */
public class ReportActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.physical_report);
		setContentView(R.layout.activity_grid);
		setLeftIconClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent parentIntent = new Intent(ReportActivity.this,
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
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					startActivity(new Intent(ReportActivity.this, IrisReportActivity.class));
					break;
				case 1:
					startActivity(new Intent(ReportActivity.this, HairReportActivity.class));
					break;
				case 2:
					startActivity(new Intent(ReportActivity.this, SkinReportActivity.class));
					break;
				case 3:
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent parentIntent = new Intent(ReportActivity.this,
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

	class GridAdapter extends BaseAdapter {
		private Context mContext;

		public GridAdapter(Context context) {
			this.mContext = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
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
				holder.labelText.setText(R.string.blood_pressure_monitor);
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
}
