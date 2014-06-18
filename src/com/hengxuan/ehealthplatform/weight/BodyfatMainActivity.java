package com.hengxuan.ehealthplatform.weight;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.hengxuan.ehealthplatform.R;
import com.hengxuan.ehealthplatform.activity.BaseActivity;
import com.hengxuan.ehealthplatform.http.HttpError;
import com.hengxuan.ehealthplatform.http.HttpGroup;
import com.hengxuan.ehealthplatform.http.HttpResponse;
import com.hengxuan.ehealthplatform.http.HttpSetting;
import com.hengxuan.ehealthplatform.http.constant.ConstFuncId;
import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;
import com.hengxuan.ehealthplatform.http.constant.ConstSysConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class BodyfatMainActivity extends BaseActivity implements BodyfatCallback{

	public static BodyfatApp rbxt;
	private SharedPreferences sharedPreferences;
	private TabHost tabHost;
	private Button sexChoice, submit;
	
	private TextView weightText;
	private TextView BMIText, suggestion;
	private ImageButton bluetooth, input;
	private ImageButton back;
	private BluetoothAdapter mBtAdapter; 
	private ImageView weightCircle;
	
	private LinearLayout weightDataInput;
	private EditText weightDataEdit;
	private Button weightDataSure, kg;
	private Button weightDataCancel;
	
	private float weight = 0.0f;
	private int damp;
	private float height;
	private boolean hasSubmit = false;
	private boolean submitEnable = false;
	
	private boolean connecting = false;
	private boolean paired = false;
	private BluetoothDevice deviceBP;
	
	private ListView bmiListView, fatListView, calurieListView, waterListView;
	ArrayList<HashMap<String, String>> bmiList, fatList, calurieList, waterList;

	private ProgressDialog progress;
	private Handler handlerBlue = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:
				bluetooth.setImageResource(R.drawable.bluetoothno);
				bluetooth.setEnabled(true);
				Toast.makeText(BodyfatMainActivity.this, getResources().getString(R.string.no_avavailable_bluetooth), Toast.LENGTH_LONG).show();
				break;
			case 1:
				bluetooth.setImageResource(R.drawable.bluetooth);
				bluetooth.setEnabled(true);
				Toast.makeText(BodyfatMainActivity.this, getResources().getString(R.string.bluetooth_connection), Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bodyfat_main_activity);
		
		if(rbxt == null){
			rbxt = new BodyfatApp();
			rbxt.init();
		}else{
			rbxt.getService().setRunning(true);
		}
		
		tabHost = (TabHost)findViewById(R.id.bodyfat_tab);
		tabHost.setup();
		View tab1 = (View)LayoutInflater.from(this).inflate(R.layout.bottom_bar, null);  
		((TextView)tab1.findViewById(R.id.bottom_text)).setText(getResources().getString(R.string.get_weight));
		((ImageView)tab1.findViewById(R.id.bottom_image)).setImageResource(R.drawable.bodyfat_tab1);
		View tab2 = (View)LayoutInflater.from(this).inflate(R.layout.bottom_bar, null);  
		((TextView)tab2.findViewById(R.id.bottom_text)).setText(getResources().getString(R.string.information));
		((ImageView)tab2.findViewById(R.id.bottom_image)).setImageResource(R.drawable.bodyfat_tab2);
		
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(tab1).setContent(R.id.bodyfat_tab1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(tab2).setContent(R.id.bodyfat_tab2));
		
		sexChoice = (Button)findViewById(R.id.bodyfat_information_sex);
		sexChoice.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sexChoice.getText().toString().equals(getResources().getString(R.string.male))){
					loadInformation(true);
					sexChoice.setText(getResources().getString(R.string.female));
				}else{
					loadInformation(false);
					sexChoice.setText(getResources().getString(R.string.male));
				}
				((SimpleAdapter)(fatListView.getAdapter())).notifyDataSetChanged();
				((SimpleAdapter)(calurieListView.getAdapter())).notifyDataSetChanged();
				((SimpleAdapter)(waterListView.getAdapter())).notifyDataSetChanged();
			}
		});
		
		submit = (Button)findViewById(R.id.bodyfat_submit);
		submit.setEnabled(false);
		submit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date currentdate = new Date(System.currentTimeMillis());
				String timestr = format.format(currentdate);
				String getUserPin = getStringFromPreference(ConstHttpProp.USER_PIN);
				addToServer(timestr, getUserPin); 
			}
			
		});
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if(tabHost.getCurrentTab() == 0){
					sexChoice.setVisibility(View.GONE);
					submit.setVisibility(View.VISIBLE);
				}else{
					submit.setVisibility(View.GONE);
					sexChoice.setVisibility(View.VISIBLE);
				}
			}
			
		});
		
		//测体重界面
		
		weightText = (TextView)findViewById(R.id.weight_data);
		BMIText = (TextView)findViewById(R.id.bmi_data);
		bluetooth = (ImageButton)findViewById(R.id.bodyfat_blue);
		back = (ImageButton)findViewById(R.id.title_back);
		weightCircle = (ImageView)findViewById(R.id.bodyfat_bmi_circle);
		weightCircle.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BodyfatMainActivity.this, WeightDataAnalysis.class);
				intent.putExtra("weight", weight);
				intent.putExtra("damp", damp);
				intent.putExtra("hasSubmit", hasSubmit);
				intent.putExtra("submitEnable", submitEnable);
				startActivityForResult(intent, 5);
			}
			
		});		
		kg = (Button)findViewById(R.id.bodyfat_kgButton);
		kg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(kg.getText().toString().equals("Kg")){
					kg.setText("Pound");
					weightText.setText(new DecimalFormat("0.0").format(weight * 2.2));
				}else{
					kg.setText("Kg");
					weightText.setText(weight + "");
				}
			}
			
		});
		
		ViewTreeObserver vto = weightCircle.getViewTreeObserver();  
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {  
            @Override  
            public void onGlobalLayout() {
            	weightCircle.getViewTreeObserver().removeGlobalOnLayoutListener(this);  
            	weightText.setTextSize(TypedValue.COMPLEX_UNIT_PX, weightCircle.getHeight() * 0.23f);
        		BMIText.setTextSize(TypedValue.COMPLEX_UNIT_PX, weightCircle.getHeight() * 0.08f);
            	kg.setWidth(weightCircle.getHeight() / 2);
            }  
        });  

		bluetooth.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (rbxt.getService().getmState() == BodyfatBluetoothService.STATE_CONNECTED){
					bluetooth.setImageResource(R.drawable.bluetoothno);
					rbxt.getService().stop();
				}else{
					connectBluetooth();
				}
			}
			
		});
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		input = (ImageButton)findViewById(R.id.bodyfat_input);
		input.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				weightDataInput.setVisibility(View.VISIBLE);
				weightDataEdit.setFocusable(true);
				weightDataEdit.setFocusableInTouchMode(true);   
				weightDataEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
		});
		
		weightDataInput = (LinearLayout)findViewById(R.id.weight_data_input);
		weightDataEdit = (EditText)findViewById(R.id.weight_data_edit);
		weightDataSure = (Button)findViewById(R.id.weight_data_button_sure);
		weightDataSure.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				String s = weightDataEdit.getText().toString().trim();
				if(s == null || "".equals(s)){
					weightDataEdit.setError(
							Html.fromHtml("<font color=#00ff00>"
									+getResources().getString(R.string.can_not_be_empty)
									+"</font>"));
					return;
				}
				//submit.setEnabled(false);
				submitEnable = false;
				weightDataInput.setVisibility(View.GONE);
				weight = Float.parseFloat(new DecimalFormat("0.0").format(Float.parseFloat(weightDataEdit.getText().toString())));
				weightText.setText(weight + "");
				kg.setText("Kg");
				BMIText.setText("BMI:" + new DecimalFormat("0.0").format(Float.parseFloat(weightDataEdit.getText().toString()) / height / height * 10000));
				giveSuggestion((float) (Float.parseFloat(weightDataEdit.getText().toString()) / height / height * 10000));
				weightDataEdit.setText("");
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(weightDataEdit.getWindowToken(), 0);
			}
			
		});
		weightDataCancel = (Button)findViewById(R.id.weight_data_button_cancel);
		weightDataCancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				weightDataInput.setVisibility(View.GONE);
				weightDataEdit.setText("");
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(weightDataEdit.getWindowToken(), 0);
			}
			
		});
		
		suggestion = (TextView)findViewById(R.id.bodyfat_suggestion);
		
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		this.registerReceiver(mReceiver, filter);
		
		//信息界面
		bmiListView = (ListView)findViewById(R.id.bmi_list);
		fatListView = (ListView)findViewById(R.id.fat_list);
		calurieListView = (ListView)findViewById(R.id.calurie_list);
		waterListView = (ListView)findViewById(R.id.water_list);
		
		bmiList = new ArrayList<HashMap<String, String>>();
		fatList = new ArrayList<HashMap<String, String>>();
		calurieList = new ArrayList<HashMap<String, String>>();
		waterList = new ArrayList<HashMap<String, String>>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("lean", getResources().getString(R.string.lean));
		map.put("normal", getResources().getString(R.string.normal));
		map.put("overweight", getResources().getString(R.string.overweight));
		map.put("fat", getResources().getString(R.string.fat));
		map.put("obese", getResources().getString(R.string.obese));
		bmiList.add(map);
		SimpleAdapter adapterBMI = new SimpleAdapter(BodyfatMainActivity.this,
				bmiList,
				R.layout.bodyfat_bmi_list_content,
				new String[]{"lean", "normal", "overweight", "fat", "obese"},
				new int[]{R.id.bodyfat_bmi_lean, R.id.bodyfat_bmi_normal, R.id.bodyfat_bmi_overweight, R.id.bodyfat_bmi_fat, R.id.bodyfat_bmi_obese});
		bmiListView.setAdapter(adapterBMI);
		
		loadInformation(false);
		SimpleAdapter adapterFat = new SimpleAdapter(BodyfatMainActivity.this,
				fatList,
				R.layout.bodyfat_fat_list_content,
				new String[]{"age", "lean", "normal", "overweight", "fat"},
				new int[]{R.id.bodyfat_fat_age, R.id.bodyfat_fat_lean, R.id.bodyfat_fat_normal, R.id.bodyfat_fat_overweight, R.id.bodyfat_fat_fat});
		fatListView.setAdapter(adapterFat);
				
		SimpleAdapter adapterCalurie = new SimpleAdapter(BodyfatMainActivity.this,
				calurieList,
				R.layout.bodyfat_calurie_list_content,
				new String[]{"age", "consume"},
				new int[]{R.id.bodyfat_calurie_age, R.id.bodyfat_calurie_consume});
		calurieListView.setAdapter(adapterCalurie);
		
		SimpleAdapter waterCalurie = new SimpleAdapter(BodyfatMainActivity.this,
				waterList,
				R.layout.bodyfat_water_list_content,
				new String[]{"age", "low", "normal", "high"},
				new int[]{R.id.bodyfat_water_age, R.id.bodyfat_water_low, R.id.bodyfat_water_normal, R.id.bodyfat_water_high});
		waterListView.setAdapter(waterCalurie);
		checkInfo();
	}
	
	private void connectBluetooth(){
		if(mBtAdapter == null)
			mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBtAdapter.isEnabled() == false){
			mBtAdapter.enable();
			bluetooth.setImageResource(R.drawable.bluetooth_anim);
    		AnimationDrawable animationDrawable = (AnimationDrawable) bluetooth.getDrawable();  
    		animationDrawable.start(); 
    		bluetooth.setEnabled(false);
		}else{
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

			if (pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					if(device.getName().equals("Bluetooth BP")){
						paired = true;
						deviceBP = device;
						rbxt.getService().setDevice(device);
						bluetooth.setImageResource(R.drawable.bluetooth_anim);
		        		AnimationDrawable animationDrawable = (AnimationDrawable) bluetooth.getDrawable();  
		        		animationDrawable.start(); 
		        		bluetooth.setEnabled(false);
						new BluetoothThread().start();
						return;
					}
				}
			}
			if(mBtAdapter.isEnabled()){
				if (mBtAdapter.isDiscovering()) {
					mBtAdapter.cancelDiscovery();
				}
				bluetooth.setImageResource(R.drawable.bluetooth_anim);
        		AnimationDrawable animationDrawable = (AnimationDrawable) bluetooth.getDrawable();  
        		animationDrawable.start(); 
        		bluetooth.setEnabled(false);
				mBtAdapter.startDiscovery();
			}
		}
    }
	
	public class BluetoothThread extends Thread{
		public void run(){
			connecting = true;
			if(paired == false){
				Method createBondMethod;
				try {
					createBondMethod = BluetoothDevice.class.getMethod("createBond");
					createBondMethod.invoke(deviceBP);
				} catch (Exception e) {
					
				} 				
			}
			rbxt.getService().connect();
			connecting = false;
			if (rbxt.getService().getmState() == 1){
				handlerBlue.sendEmptyMessage(1);
			}else{
				handlerBlue.sendEmptyMessage(0);
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
					if (rbxt.getService().getmState() != 1 && connecting == false) {
						bluetooth.setImageResource(R.drawable.bluetoothno);
						Toast.makeText(BodyfatMainActivity.this, getResources().getString(R.string.no_avavailable_bluetooth), Toast.LENGTH_LONG).show();
					}
					if(connecting == false)
						bluetooth.setImageResource(R.drawable.bluetoothno);
					bluetooth.setEnabled(true);
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
	
	@Override
	public void onReceive(IBean weight, IBean damp) {
		// TODO Auto-generated method stub
		if(((Damp)damp).getError())
			Toast.makeText(BodyfatMainActivity.this, "Error", Toast.LENGTH_SHORT).show();
		this.weight = (float)((Weight)weight).getWeight() / 10;
		kg.setText("Kg");
		weightText.setText(this.weight + "");
		BMIText.setText("BMI:" + new DecimalFormat("0.0").format(this.weight / height / height * 10000));
		this.damp = ((Damp)damp).getDamp();
		if(((Weight)weight).getHead().getType() == Head.TYPE_STABLE){
			giveSuggestion((float) (this.weight / height / height * 10000));
			hasSubmit = false;
			submit.setEnabled(true);
			submitEnable = true;
			submit.setText(getResources().getString(R.string.app_error_submit));
			rbxt.getService().stop();
		}
		
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		rbxt.setCall(this);
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 5:
			if(hasSubmit == false && data.getExtras().getBoolean("hasSubmit") == true){
				submit.setText(getResources().getString(R.string.submitted));
				submit.setEnabled(false);
				submitEnable = false;
				hasSubmit = true;
			}
			break;
		}
	} 
	
	public void giveSuggestion(float bmi){
		if(bmi < 18.5){
			suggestion.setText(getResources().getString(R.string.lean_suggestion));
			weightCircle.setImageResource(R.drawable.lean_selector);
		}else if(bmi <= 25){
			suggestion.setText(getResources().getString(R.string.normal_suggestion));
			weightCircle.setImageResource(R.drawable.normal_selector);
		}else if(bmi <= 30){
			suggestion.setText(getResources().getString(R.string.overweight_suggestion));
			weightCircle.setImageResource(R.drawable.overweight_selector);
		}else if(bmi <= 35){
			suggestion.setText(getResources().getString(R.string.fat_suggestion));
			weightCircle.setImageResource(R.drawable.fat_selector);
		}else{
			suggestion.setText(getResources().getString(R.string.fat_suggestion));
			weightCircle.setImageResource(R.drawable.obese_selector);
		}
	}
	
	public void loadInformation(boolean male){
		fatList.clear();
		calurieList.clear();
		waterList.clear();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("age", getResources().getString(R.string.weight_age));
		map.put("lean", getResources().getString(R.string.lean));
		map.put("normal", getResources().getString(R.string.normal));
		map.put("overweight", getResources().getString(R.string.overweight));
		map.put("fat", getResources().getString(R.string.fat));
		fatList.add(map);
		map = new HashMap<String, String>();
		map.put("age", getResources().getString(R.string.weight_age));
		map.put("consume", getResources().getString(R.string.daily_calorie_consumption));
		calurieList.add(map);
		map = new HashMap<String, String>();
		map.put("age", getResources().getString(R.string.weight_age));
		map.put("low", getResources().getString(R.string.low));
		map.put("normal", getResources().getString(R.string.normal));
		map.put("high", getResources().getString(R.string.high));
		waterList.add(map);
		if(male){
			map = new HashMap<String, String>();
			map.put("age", "10~17");
			map.put("lean", "3.0~15.0");
			map.put("normal", "15.1~22.0");
			map.put("overweight", "22.1~26.4");
			map.put("fat", "26.5~50.5");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "18~30");
			map.put("lean", "3.0~15.4");
			map.put("normal", "15.5~23.0");
			map.put("overweight", "23.1~27.0");
			map.put("fat", "27.1~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "31~40");
			map.put("lean", "3.0~16.0");
			map.put("normal", "16.1~23.4");
			map.put("overweight", "23.5~27.4");
			map.put("fat", "27.5~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "41~60");
			map.put("lean", "3.0~16.4");
			map.put("normal", "16.5~24.0");
			map.put("overweight", "24.1~28.0");
			map.put("fat", "28.1~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "61~99");
			map.put("lean", "3.0~17.0");
			map.put("normal", "17.1~24.4");
			map.put("overweight", "24.5~28.4");
			map.put("fat", "28.5~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "1~2");
			map.put("consume", "700");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "3~5");
			map.put("consume", "860");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "6~8");
			map.put("consume", "1000");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "9~11");
			map.put("consume", "1180");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "12~14");
			map.put("consume", "1340");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "15~17");
			map.put("consume", "1300");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "18~29");
			map.put("consume", "1210");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "30~49");
			map.put("consume", "1170");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "50~69");
			map.put("consume", "1110");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", ">=70");
			map.put("consume", "1010");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "10~17");
			map.put("low", "<54.0");
			map.put("normal", "54.0~60.0");
			map.put("high", ">60.0");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "18~30");
			map.put("low", "<53.5");
			map.put("normal", "53.5~59.5");
			map.put("high", ">59.5");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "31~40");
			map.put("low", "<53.0");
			map.put("normal", "53.0~59.0");
			map.put("high", ">59.0");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "41~60");
			map.put("low", "<52.5");
			map.put("normal", "52.5~58.5");
			map.put("high", ">58.5");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "61~99");
			map.put("low", "<52.0");
			map.put("normal", "52.0~58.0");
			map.put("high", ">58.0");
			waterList.add(map);
		}else{
			map = new HashMap<String, String>();
			map.put("age", "10~17");
			map.put("lean", "3.0~12.0");
			map.put("normal", "12.1~17.0");
			map.put("overweight", "17.1~22.0");
			map.put("fat", "22.1~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "18~30");
			map.put("lean", "3.0~12.4");
			map.put("normal", "12.5~18.0");
			map.put("overweight", "18.1~23.0");
			map.put("fat", "23.1~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "31~40");
			map.put("lean", "3.0~13.0");
			map.put("normal", "13.1~18.4");
			map.put("overweight", "18.5~23.0");
			map.put("fat", "23.1~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "41~60");
			map.put("lean", "3.0~13.4");
			map.put("normal", "13.5~19.0");
			map.put("overweight", "19.1~23.4");
			map.put("fat", "23.5~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "61~99");
			map.put("lean", "3.0~14.0");
			map.put("normal", "14.1~19.4");
			map.put("overweight", "19.5~24.0");
			map.put("fat", "24.1~50.0");
			fatList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "1~2");
			map.put("consume", "700");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "3~5");
			map.put("consume", "900");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "6~8");
			map.put("consume", "1090");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "9~11");
			map.put("consume", "1290");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "12~14");
			map.put("consume", "1480");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "15~17");
			map.put("consume", "1610");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "18~29");
			map.put("consume", "1550");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "30~49");
			map.put("consume", "1500");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "50~69");
			map.put("consume", "1350");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", ">=70");
			map.put("consume", "1220");
			calurieList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "10~17");
			map.put("low", "<57.0");
			map.put("normal", "57.0~62.0");
			map.put("high", ">62.0");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "18~30");
			map.put("low", "<56.5");
			map.put("normal", "56.5~61.5");
			map.put("high", ">61.5");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "31~40");
			map.put("low", "<56.0");
			map.put("normal", "56.0~61.0");
			map.put("high", ">61.0");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "41~60");
			map.put("low", "<55.5");
			map.put("normal", "55.5~60.5");
			map.put("high", ">60.5");
			waterList.add(map);
			map = new HashMap<String, String>();
			map.put("age", "61~99");
			map.put("low", "<55.0");
			map.put("normal", "55.0~60.0");
			map.put("high", ">60.0");
			waterList.add(map);
		}
	}
	private void addToServer(String currentDate, String userPin){
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("client_code", "SZ-Youruien");
			jsonobject.put("currentDate", currentDate);
			jsonobject.put("userPin", userPin);
			jsonobject.put("weight", weight + "");
			System.out.println("11111111111111111111111111111111111       " + weight);
			jsonobject.put("impedance", damp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.BODYFATADDTOSERVER);
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
						if(getcode == 1){
							Toast.makeText(BodyfatMainActivity.this, getResources().getString(R.string.addtoreport), Toast.LENGTH_SHORT).show();
							submit.setEnabled(false);
							submitEnable = false;
							submit.setText(getResources().getString(R.string.submitted));
							hasSubmit = true;
						}
						else{
							Toast.makeText(BodyfatMainActivity.this, getResources().getString(R.string.failedtoadd), Toast.LENGTH_SHORT).show();
							submit.setEnabled(true);
							submitEnable = true;
						}
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
		getHttpGroupaAsynPool().add(httpsetting);
	}
	
	public void checkInfo(){
		sharedPreferences = getSharedPreferences(ConstSysConfig.SYS_CUST_CLIENT, 0);
		int age = sharedPreferences.getInt("age", 0);
		height = sharedPreferences.getInt("height", 0);
		if(age == 0 || height == 0){
			final AlertDialog infoDialog = (new AlertDialog.Builder(BodyfatMainActivity.this)).create();
			infoDialog.setMessage(getString(R.string.please_fill_in_the_necessary_personal_information));
			infoDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					infoDialog.dismiss();
					Intent intent = new Intent(BodyfatMainActivity.this, ImprovePersonalInformation.class);
					startActivity(intent);
					finish();
				}
			});
			
			infoDialog.show();
//			infoDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//				
//				@Override
//				public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
//					// TODO Auto-generated method stub
//					return true;
//				}
//			});
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(height != sharedPreferences.getInt("height", 0)){
			height = sharedPreferences.getInt("height", 0);
			BMIText.setText("BMI:" + new DecimalFormat("0.0").format(this.weight / height / height * 10000));
			giveSuggestion((float) (this.weight / height / height * 10000));
		}	
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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
	
	
}
