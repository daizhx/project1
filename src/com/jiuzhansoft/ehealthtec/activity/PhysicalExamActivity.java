package com.jiuzhansoft.ehealthtec.activity;

import java.util.ArrayList;

import com.jiuzhansoft.ehealthtec.MainActivity;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.MassageActivity.GridAdapter;
import com.jiuzhansoft.ehealthtec.activity.MassageActivity.GridAdapter.Holder;
import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.constant.ConstEquipId;
import com.jiuzhansoft.ehealthtec.lens.LensBaseActivity;
import com.jiuzhansoft.ehealthtec.lens.LensConnectActivity;
import com.jiuzhansoft.ehealthtec.lens.LensShootBaseActivity;
import com.jiuzhansoft.ehealthtec.lens.hair.HairEntryActivity;
import com.jiuzhansoft.ehealthtec.lens.iris.IrisEntryActivity;
import com.jiuzhansoft.ehealthtec.lens.iris.IrisInspectionActivity;
import com.jiuzhansoft.ehealthtec.lens.naevus.NaevusEntryActivity;
import com.jiuzhansoft.ehealthtec.lens.skin.SkinEntryActivity;
import com.jiuzhansoft.ehealthtec.massager.MassagerActivity;
import com.jiuzhansoft.ehealthtec.product.Product;
import com.jiuzhansoft.ehealthtec.update.UpdateManager;
import com.jiuzhansoft.ehealthtec.weight.BodyfatMainActivity;

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
	//产品列表
	private ArrayList<Product> products = new ArrayList<Product>();
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
//				switch (position) {
//				case 0:
////					index = LensShootBaseActivity.IRIS_PHOTO_INDEX;
////					break;
//					startActivity(new Intent(PhysicalExamActivity.this, IrisEntryActivity.class));
//					return;
//				case 1:
////					index = LensShootBaseActivity.SKIN_PHOTO_INDEX;
//					startActivity(new Intent(PhysicalExamActivity.this, SkinEntryActivity.class));
//					return;
//				case 2:
////					index = LensShootBaseActivity.HAIR_PHOTO_INDEX;
//					startActivity(new Intent(PhysicalExamActivity.this, HairEntryActivity.class));
//					return;
//				case 3:
////					index = LensShootBaseActivity.NAEVUS_PHOTO_INDEX;
//					startActivity(new Intent(PhysicalExamActivity.this, NaevusEntryActivity.class));
//					return;
//				case 4:
//					return;
//				case 5:
//					startActivity(new Intent(PhysicalExamActivity.this, BodyfatMainActivity.class));
//					return;
//				}
////				Intent intent = new Intent(PhysicalExamActivity.this,
////						LensBaseActivity.class);
////				intent.putExtra("index", index);
////				startActivity(intent);
				products.get(position).EntryProduct(PhysicalExamActivity.this);
			}

		});

		//查询注册的产品
		for(Product p:EHTApplication.productList){
			if(p.mTypeId == ConstEquipId.LENSID || p.mTypeId == ConstEquipId.WEIGHTID || p.mTypeId == ConstEquipId.BLOODPRESSID){
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
//			holder.icon.setImageResource(R.drawable.massager_icon);
//			holder.labelText.setText(R.string.iris_detection);
			// TODO
//			switch (position) {
//			case 0:
//				holder.icon.setImageResource(R.drawable.lens_icon);
//				holder.labelText.setText(R.string.iris_detection);
//				break;
//			case 1:
//				holder.icon.setImageResource(R.drawable.skin_entry);
//				holder.labelText.setText(R.string.skin_detection);
//				break;
//			case 2:
//				holder.icon.setImageResource(R.drawable.hair_entry);
//				holder.labelText.setText(R.string.hair_detection);
//				break;
//			case 3:
//				holder.icon.setImageResource(R.drawable.naevus_entry);
//				holder.labelText.setText(R.string.naevus_detection);
//				break;
//			case 4:
//				holder.icon.setImageResource(R.drawable.bloody_entry);
//				holder.labelText.setText(R.string.blood_pressure_monitor);
//				break;
//			case 5:
//				holder.icon.setImageResource(R.drawable.weight_entry);
//				holder.labelText.setText(R.string.weighting_scale);
//				break;
//
//			default:
//				break;
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
