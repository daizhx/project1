package com.jiuzhansoft.ehealthtec.sphygmomanometer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Data;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BloodPressureResult extends BaseActivity implements OnClickListener{
	private Button save,ignore;
	private Data data;
	private TextView syse,diae,pule,date;
	private ImageView ll;
	String name,time;
	int sys,dia,pul;
	private SQLiteDatabase db;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContent(R.layout.blood_pressure_result);
		save = (Button) this.findViewById(R.id.save);
		ignore = (Button) this.findViewById(R.id.ignore);
		syse = (TextView) this.findViewById(R.id.sys);
		diae = (TextView) this.findViewById(R.id.dia);
		pule = (TextView) this.findViewById(R.id.pul);
		ll = (ImageView) this.findViewById(R.id.bag);
		date = (TextView) this.findViewById(R.id.blood_pressure_result_time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date currentdate = new Date(System.currentTimeMillis());
		date.setText(format.format(currentdate));
		save.setOnClickListener(this);
		ignore.setOnClickListener(this);
		
	    Bundle bundle = this.getIntent().getExtras();
		name = bundle.getString("name");
		time = bundle.getString("time");
		sys = bundle.getInt("sys");
		dia = bundle.getInt("dia");
		pul = bundle.getInt("pul");
		if(sys>180 || dia>110){
			ll.setImageResource(R.drawable.ui022);
		}else if(sys > 160 || dia > 100){
			ll.setBackgroundResource(R.drawable.ui026);
		}else if(sys > 140 || dia > 90){
			ll.setImageResource(R.drawable.ui025);
		}else if(sys > 130 || dia > 85){
			ll.setImageResource(R.drawable.ui024);
		}else if(sys > 120 || dia > 80){
			ll.setImageResource(R.drawable.ui023);
		}else{
			ll.setImageResource(R.drawable.ui021);
		}
		syse.setText(sys+"");
		diae.setText(dia+"");
		pule.setText(pul+"");
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.save:
			save.setEnabled(false);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date currentdate = new Date(System.currentTimeMillis());
			final String timestr = format.format(currentdate);
			final String getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
			if(isOpenNetwork()){				
				addToServer(timestr, getUserPin); 
			}else{
				final AlertDialog alertdialog = (new AlertDialog.Builder(BloodPressureResult.this)).create();
				alertdialog.setMessage(getString(R.string.temporarily_stored_in_the_local));
				alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						alertdialog.dismiss();
						db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString()+"/report.db3", null);
						if(tableIsExist("blood_pressure")){
							addToSQLite(timestr, getUserPin); 
						}else{
							db.execSQL("create table blood_pressure(currentDate varchar(25),sys int,dia int,pul int,client_code varchar(20))");
							addToSQLite(timestr, getUserPin); 
						}						
					}
				});
				alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						save.setEnabled(false);
						alertdialog.cancel();
					}
				});
				alertdialog.show();
			}			
			break;
		case R.id.ignore:
			Intent intent = new Intent(BloodPressureResult.this, ReportDateSelect.class);
			intent.putExtra("reportIndex", ReportDateSelect.BLOODPRESSUREREPORT);
			startActivity(intent);			
			break;
		}
	}
	private void addToServer(String currentDate, String userPin){
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("client_code", "SZ-Youruien");
			jsonobject.put("currentDate", currentDate);
			jsonobject.put("userPin", userPin);
			jsonobject.put("high", sys);
			jsonobject.put("low", dia);
			jsonobject.put("pulse", pul);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.BLOODPRESSUREADDTOSERVER);
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				final int getcode = response.getJSONObject().getIntOrNull("code");
				post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(getcode == 1)
							Toast.makeText(BloodPressureResult.this, getResources().getString(R.string.addtoreport), Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(BloodPressureResult.this, getResources().getString(R.string.failedtoadd), Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}});
		httpsetting.setNotifyUser(true);
		HttpGroupaAsynPool.getHttpGroupaAsynPool(this).add(httpsetting);
	}
	
	/**
	 * ÅÐ¶ÏÊÇ·ñÓÐÍøÂç£¿
	 * @return
	 */
	private boolean isOpenNetwork() {  
	    ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(connManager.getActiveNetworkInfo() != null) {  
	        return connManager.getActiveNetworkInfo().isAvailable();  
	    }  
	  
	    return false;  
	}
	
	public void addToSQLite(String currentDate, String userPin){
		 db.execSQL("insert into blood_pressure values(\"" + currentDate + "\"," + sys + "," + dia + "," + pul + ",\"SZ-Youruien\")");
	}
	
	public boolean tableIsExist(String tableName){
        boolean result = false;
        if(tableName == null){
                return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                     result = true;
                }
            }
                
        } catch (Exception e) {
                // TODO: handle exception
        }                
        return result;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(db != null)
			db.close();
	}
	
}
