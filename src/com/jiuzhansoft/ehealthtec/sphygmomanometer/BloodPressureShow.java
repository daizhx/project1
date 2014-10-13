package com.jiuzhansoft.ehealthtec.sphygmomanometer;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.log.Log;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Data;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Head;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.IBean;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Msg;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Error;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.data.Pressure;
import com.jiuzhansoft.ehealthtec.sphygmomanometer.service.RbxtApp;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BloodPressureShow extends BaseActivity implements OnClickListener, ICallback {
	private static final String TAG = "bp"; 
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	
	public static RbxtApp rbxt;
	private Button start;
	private RelativeLayout li;
	private ImageView main_imageview;
	//show the blood pressure value
	private TextView sdp;
	private ProgressBar progressbar;
	//private String ggname;
	//private DBOpenHelper dbOpenHelper; 
	private boolean hasResult = false;
	private int i = 0;
	Timer timer = new Timer();
	
//	private ImageButton bluetooth;
	private BluetoothAdapter mBtAdapter;
	private boolean connecting = false;
	private boolean paired = false;
	private BluetoothDevice deviceBP;
	private SQLiteDatabase db;
	private JSONArray array = new JSONArray();
	private boolean bluefirst = true;
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (i > 2)
			{
				i = 0;
			}
			else
			{
				switch (i)
				{
				case 1:
					main_imageview.setImageResource(R.drawable.heart2);
					break;
				case 2:
					main_imageview.setImageResource(R.drawable.heart);
					break;
				
				default:
					break;
				}
				li.invalidate();
			}
			super.handleMessage(msg);
		}
	};
	private ProgressDialog progress;
	private Handler handlerBlue = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				rightIcon.setImageResource(R.drawable.bt_off);
				rightIcon.setEnabled(true);
				Toast.makeText(BloodPressureShow.this, getResources().getString(R.string.no_avavailable_bluetooth), Toast.LENGTH_LONG).show();
				//progress.dismiss();
				break;
			case 1:
				rightIcon.setImageResource(R.drawable.bt_on);
				rightIcon.setEnabled(true);
				Toast.makeText(BloodPressureShow.this, getResources().getString(R.string.bluetooth_connection), Toast.LENGTH_LONG).show();
				//progress.dismiss();
				break;
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blood_pressure_show);
		setTitle(R.string.measuring_blood_pressure);
		setRightIcon(R.drawable.bt_connectting_indicate, this);
		
		if(rbxt == null){
			rbxt = new RbxtApp();
			rbxt.init();
		}else{
			rbxt.getService().setRunning(true);
		}	
		
//		bluetooth = (ImageButton) this.findViewById(R.id.blue);
//		bluetooth.setImageResource(R.drawable.bluetoothno);
//	    this.mSpinnerBtn = (MySpinnerButton) this.findViewById(R.id.spinner_btn);  
//		bluetooth.setOnClickListener(this);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(mReceiver, filter);
		start = (Button) this.findViewById(R.id.start);
		sdp = (TextView) this.findViewById(R.id.mmhg);
		progressbar = (ProgressBar) this.findViewById(R.id.progress);
		progressbar.setMax(300);
		start.setOnClickListener(this);
		initView();	
		rightIcon.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener(){
        	public boolean onPreDraw(){ 
        		if(bluefirst == true){
        			bluefirst = false;
        			checkSQLite();
        		}
        		return true;
        	}
        });
				
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		rbxt.setCall(this);
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.start:	
				hasResult = false;
				if (rbxt.getService().getmState() == Msg.MESSAGE_STATE_CONNECTED) {
					rbxt.getService().connect();
					byte[] send = { (-3), (-3), -6, 5, 13, 10 };			
					rbxt.getService().write(send);
				}else{
					Toast.makeText(BloodPressureShow.this, getResources().getString(R.string.please_connect_bluetooth), Toast.LENGTH_SHORT).show();
				}
			break;
		case R.id.right_icon:		
			if (rbxt.getService().getmState() == Msg.MESSAGE_STATE_CONNECTED){
				rightIcon.setImageResource(R.drawable.bt_connectting_indicate);
				rbxt.getService().stop();
				over();
			}else{				
				connectBluetooth();
			}
			break;
		}
	}
	
	/*start a timer to emulate hart jump animation
	 * */
	public void initView()
	{
		li = (RelativeLayout) findViewById(R.id.inmages);
		main_imageview = (ImageView) findViewById(R.id.heart);

		timer.scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				i++;
				Message mesasge = new Message();
				mesasge.what = i;
				handler.sendMessage(mesasge);
				
			}
		}, 0, 250);
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		timer.cancel();
		super.onDestroy();
		
 		unregisterReceiver(mReceiver);
		
 		if(deviceBP != null)
 			try {
 				Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
 				removeBondMethod.invoke(deviceBP);
 			} catch (Exception e) {
 							
 			} 
		
		rbxt.getService().stop();
 		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
	}
	
	public void onReceive(IBean bean) {
		switch (bean.getHead().getType()){
		case Head.TYPE_PRESSURE:
			progressbar.setProgress(((Pressure) bean).getPressure());
			sdp.setText(((Pressure) bean).getPressure() + "");
		    break;
		case Head.TYPE_RESULT:
			if(hasResult == true)
				return;
			hasResult = true;
			Data data = (Data)bean;
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm aaa");     
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间     
			String str = formatter.format(curDate); 
			int sys = data.getSys();
			int dia = data.getDia();
			int pul = data.getPul();
			
			if (rbxt.getService().getmState() == Msg.MESSAGE_STATE_CONNECTED) {
				//rbxt.getService().connect();
				byte[] send = { (-3), (-3), -6, 6, 13, 10 };			
				rbxt.getService().write(send);
			}
			
			Intent intent = new Intent(BloodPressureShow.this, BloodPressureResult.class);
			Bundle bundle = new Bundle();
			// bundle.putString("name", ggname);
		    bundle.putString("time", str);
		    bundle.putInt("sys", sys);
		    bundle.putInt("dia", dia);
		    bundle.putInt("pul", pul);
		    /*把bundle对象assign给Intent*/
		    intent.putExtras(bundle);
		    startActivity(intent);	
		    over();
		}
	}
	public void onError(Error error) {
		//super.onError(error);		
		if (error.getHead() != null){
			start.setEnabled(true);
			final AlertDialog infoDialog = (new AlertDialog.Builder(BloodPressureShow.this)).create();
			switch (error.getError()) {
				case Error.ERROR_EEPROM:
					infoDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.blood_pressure_error_eeprom), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							infoDialog.dismiss();
							over();
						}
					});
					
					break;
				case Error.ERROR_HEART:
				
				case Error.ERROR_DISTURB:

				case Error.ERROR_GASING:
			
				case Error.ERROR_TEST:

				case Error.ERROR_REVISE:
//					((TextView) window.findViewById(R.id.msgtv)).setMovementMethod(ScrollingMovementMethod.getInstance());
					infoDialog.setMessage(getString(R.string.blood_pressure_error_revise));
					infoDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							infoDialog.dismiss();
							over();
						}
					});
					break;
				case Error.ERROR_POWER:
					infoDialog.setMessage(getString(R.string.blood_pressure_error_power));
					infoDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							infoDialog.dismiss();
							over();
						}
					});
				break;
			}
			infoDialog.show();
		}
	}
	public void onMessage(Msg message) {

	}
	private void connectBluetooth(){
		Log.d(TAG, "connectBluetooth");
		if(mBtAdapter == null)
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBtAdapter.isEnabled() == false){
			mBtAdapter.enable();
//			bluetooth.setImageResource(R.drawable.bluetooth_anim);
//    		AnimationDrawable animationDrawable = (AnimationDrawable) bluetooth.getDrawable();  
//    		animationDrawable.start(); 
//    		bluetooth.setEnabled(false);
			AnimationDrawable animationDrawable = (AnimationDrawable)rightIcon.getDrawable();
			animationDrawable.start();
			rightIcon.setEnabled(false);
			
		}else{
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

			if (pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					if(device.getName().equals("Bluetooth BP")){
						paired = true;
						deviceBP = device;
						rbxt.getService().setDevice(device);
						rightIcon.setImageResource(R.drawable.bt_connectting_indicate);
		        		AnimationDrawable animationDrawable = (AnimationDrawable) rightIcon.getDrawable();  
		        		animationDrawable.start(); 
		        		rightIcon.setEnabled(false);
						new BluetoothThread().start();
						return;
					}
				}
			}
			if(mBtAdapter.isEnabled()){
				if (mBtAdapter.isDiscovering()) {
					mBtAdapter.cancelDiscovery();
				}
				rightIcon.setImageResource(R.drawable.bt_connectting_indicate);
        		AnimationDrawable animationDrawable = (AnimationDrawable) rightIcon.getDrawable();  
        		animationDrawable.start(); 
        		rightIcon.setEnabled(false);
				mBtAdapter.startDiscovery();
			}
		}
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(device.getName().equals("Bluetooth BP")){
					mBtAdapter.cancelDiscovery();
					paired = false;
					deviceBP = device;
					rbxt.getService().setDevice(device);
					new BluetoothThread().start();
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {	
				if (rbxt.getService().getmState() != Msg.MESSAGE_STATE_CONNECTED && connecting == false) {
					rightIcon.setImageResource(R.drawable.bt_off);
					Toast.makeText(BloodPressureShow.this, getResources().getString(R.string.no_avavailable_bluetooth), Toast.LENGTH_LONG).show();
				}
				if(connecting == false)
					rightIcon.setImageResource(R.drawable.bt_off);
				rightIcon.setEnabled(true);
			} else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
				if(mBtAdapter.isEnabled()){
					Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

					if (pairedDevices.size() > 0) {
						for (BluetoothDevice device : pairedDevices) {
							if(device.getName().equals("Bluetooth BP")){
								paired = true;
								deviceBP = device;
								rbxt.getService().setDevice(device);
								new BluetoothThread().start();
								return;
							}
						}
					}
					if (mBtAdapter.isDiscovering()) {
						mBtAdapter.cancelDiscovery();
					}
					mBtAdapter.startDiscovery();
				}
			}

		}
	};
	
	public class BluetoothThread extends Thread{
		public void run(){
			connecting = true;
			if(paired == false){
				//why do not perform deviceBP.createBond directly
				Method createBondMethod;
				try {
					createBondMethod = BluetoothDevice.class.getMethod("createBond");
					createBondMethod.invoke(deviceBP);
				} catch (Exception e) {
					
				} 				
			}
			rbxt.getService().connect();
			connecting = false;
			if (rbxt.getService().getmState() == Msg.MESSAGE_STATE_CONNECTED){
				handlerBlue.sendEmptyMessage(1);
			}else{
				handlerBlue.sendEmptyMessage(0);
			}
		}
	}
	public void over(){
		progressbar.setProgress(0);
		sdp.setText(0 + "");
	}
	
	public void checkSQLite(){
		if(isOpenNetwork()){
			db = SQLiteDatabase.openOrCreateDatabase(getFilesDir().toString()+"/report.db3", null);
			Cursor rows = null;
			if(tableIsExist("blood_pressure")){
				rows = db.rawQuery("select * from blood_pressure", null);
			}
			if(rows != null){
				 while (rows.moveToNext()) {
					 JSONObject object = new JSONObject();
					 try {
						object.put("currentDate", rows.getString(rows.getColumnIndex("currentDate")))
						.put("high", rows.getInt(rows.getColumnIndex("sys")))
						.put("low", rows.getInt(rows.getColumnIndex("dia")))
						.put("pulse", rows.getInt(rows.getColumnIndex("pul")))
						.put("client_code", rows.getString(rows.getColumnIndex("client_code")));
						Log.d(TAG, "DB date entries: currentDate["+object.getString("currentDate") + "]sys[" + object.getInt("hight") 
								+ "]dia[" + object.getInt("low") + "]pulse[" + object.getInt("pulse") + "]client_code[" + object.getString("client_code"));
						array.put(object);
					} catch (JSONException e) {
						
					}
				 }
				 final AlertDialog alertdialog = (new AlertDialog.Builder(BloodPressureShow.this)).create();
				 alertdialog.setMessage(getString(R.string.reports_local_to_server));
				 alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.app_error_submit), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						alertdialog.dismiss();
						addToServer();
					}
				});
				alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.delete), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						alertdialog.dismiss();
						db.execSQL("drop table blood_pressure");
						connectBluetooth();
					}
				});
				alertdialog.show();
			}else{
				connectBluetooth();	
			}
		}else{
			connectBluetooth();	
		}
	}
	public void addToServer(){
		final String userPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("userPin", userPin);
			jsonobject.put("bloodpressureList", array);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.BLOODPRESSURELOCALTOSERVER);
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
						final AlertDialog alertDialog = (new AlertDialog.Builder(BloodPressureShow.this)).create();
						if(getcode == 1){
							alertDialog.setMessage(getString(R.string.addtoreport));
							db.execSQL("drop table blood_pressure");
						}else{
							alertDialog.setMessage(getString(R.string.failedtoadd));
						}
						alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();	
								connectBluetooth();
							}
						});
						alertDialog.show();
					}
				});
			}

			@Override
			public void onError(HttpError httpError) {
				// TODO Auto-generated method stub
				post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						final AlertDialog alertDialog = (new AlertDialog.Builder(BloodPressureShow.this)).create();
						alertDialog.setMessage(getString(R.string.failedtoadd));
						alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								alertDialog.dismiss();	
								connectBluetooth();
							}
						});
						alertDialog.show();
					}
				});
				
			}

			@Override
			public void onProgress(int i, int j) {
				// TODO Auto-generated method stub
				
			}});
		httpsetting.setNotifyUser(true);
		getHttpGroupaAsynPool().add(httpsetting);
	}
	
	private boolean isOpenNetwork() {  
	    ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(connManager.getActiveNetworkInfo() != null) {  
	        return connManager.getActiveNetworkInfo().isAvailable();  
	    }  
	  
	    return false;  
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
}
