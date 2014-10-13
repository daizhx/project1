package com.jiuzhansoft.ehealthtec.sphygmomanometer;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.constant.ConstSysConfig;
import com.jiuzhansoft.ehealthtec.weight.DateTimePickDialogUtil;


public class ReportDateSelect extends BaseActivity{
	
	private TextView startData; //, startDataM, startDataD;
	private TextView endData; //, endDataM, endDataD;
	private String dateStr;
	private String username;
	private int reportIndex;
	
	public static final int SKANREPORT = 1;
	public static final int HAIRREPORT = 2;
	public static final int BLOODPRESSUREREPORT = 3;
	
	private void myalert(String msg){
		final AlertDialog ad = (new AlertDialog.Builder(ReportDateSelect.this)).create();
		ad.setMessage(msg);
		ad.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				ad.dismiss();
			}
		});
		ad.show();
	}
	
	private void initView(){
		
		username = getStringFromPreference(ConstSysConfig.SYS_USER_NAME);
		if(username != null){
			((EditText) findViewById(R.id.getname)).setText(username);
		}
		
		startData = (TextView) findViewById(R.id.startdata);
		endData = (TextView) findViewById(R.id.enddata);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis());
		dateStr = format.format(date);
		String getYear = dateStr.substring(0, 4);
		String getMonth = dateStr.substring(5, 7);
		int yearToInt = Integer.parseInt(getYear);
		int monthToInt = Integer.parseInt(getMonth);
		if(monthToInt <= 3){
			yearToInt--;
			monthToInt = 12 + (monthToInt - 3);
		}else{
			monthToInt = monthToInt - 3;
		}
		int getDay = Integer.parseInt(dateStr.substring(8, 10));
		if(getDay > 28)
			getDay = 28;
		if(monthToInt < 10){
			if(getDay > 9)
				startData.setHint(yearToInt+"-0"+monthToInt+"-"+getDay);
			else
				startData.setHint(yearToInt+"-0"+monthToInt+"-0"+getDay);
		}
		else{
			if(getDay > 9)
				startData.setHint(yearToInt+"-"+monthToInt+"-"+getDay);
			else
				startData.setHint(yearToInt+"-"+monthToInt+"-0"+getDay);
		}
		endData.setHint(dateStr);
		
		final String okstr = getResources().getString(R.string.system_settings);
		final String canclestr =getResources().getString(R.string.cancel);
		
		startData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DateTimePickDialogUtil dateTimePickDialog = 
					new DateTimePickDialogUtil(ReportDateSelect.this, dateStr, okstr, canclestr, DateTimePickDialogUtil.YEAR_MONTH_DAY);
				dateTimePickDialog.dateTimePickDialog(startData);
			}
		});
		
		endData.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DateTimePickDialogUtil dateTimePickDialog = 
					new DateTimePickDialogUtil(ReportDateSelect.this, dateStr, okstr, canclestr, DateTimePickDialogUtil.YEAR_MONTH_DAY);
				dateTimePickDialog.dateTimePickDialog(endData);
			}
		});		
		
		((Button) findViewById(R.id.okbtn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isOrNotIntent()){
					Intent intent = new Intent();
					switch(reportIndex){
//						case 0:
//							intent.setClass(ReportDateSelect.this, IrisReport.class);
//							break;
//						case ReportDateSelect.SKANREPORT:
//							intent.setClass(ReportDateSelect.this, ReportDateList.class);
//							intent.putExtra("reportIndex", reportIndex);
//							break;
//						case ReportDateSelect.HAIRREPORT:
//							intent.setClass(ReportDateSelect.this, ReportDateList.class);
//							intent.putExtra("reportIndex", reportIndex);
//							break;
//						case ReportDateSelect.BLOODPRESSUREREPORT:
//							intent.setClass(ReportDateSelect.this, BloodPressureReport.class);
//							intent.putExtra("reportIndex", reportIndex);
//							break;
						default:
							break;
					}
					if(!TextUtils.isEmpty(startData.getText().toString()))
						intent.putExtra("startDate", startData.getText().toString());
					else
						intent.putExtra("startDate", startData.getHint().toString());
					if(!TextUtils.isEmpty(endData.getText().toString()))
						intent.putExtra("endDate", endData.getText().toString());
					else
						intent.putExtra("endDate", endData.getHint().toString());
					intent.putExtra("username", username);
					
					startActivity(intent);
					ReportDateSelect.this.finish();
					
					if(Integer.parseInt(android.os.Build.VERSION.SDK) >= 5)
						overridePendingTransition(R.anim.in_from_left_animation, R.anim.out_to_right_animation);
				}else{
					myalert(getResources().getString(R.string.dateselecterror));
				}
			}
		});
		
	}
	
	private boolean isOrNotIntent(){
		int startY = 0;
		int startM = 0;
		int startD = 0;
		int endY = 0;
		int endM = 0;
		int endD = 0;
		if(!TextUtils.isEmpty(startData.getText().toString())){
			startY = Integer.parseInt(startData.getText().toString().substring(0, 4));
			startM = Integer.parseInt(startData.getText().toString().substring(5, 7));
			startD = Integer.parseInt(startData.getText().toString().substring(8, 10));
		}else{
			startY = Integer.parseInt(startData.getHint().toString().substring(0, 4));
			startM = Integer.parseInt(startData.getHint().toString().substring(5, 7));
			startD = Integer.parseInt(startData.getHint().toString().substring(8, 10));
		}
		
		if(!TextUtils.isEmpty(endData.getText().toString())){
			endY = Integer.parseInt(endData.getText().toString().substring(0, 4));
			endM = Integer.parseInt(endData.getText().toString().substring(5, 7));
			endD = Integer.parseInt(endData.getText().toString().substring(8, 10));
		}else{
			endY = Integer.parseInt(endData.getHint().toString().substring(0, 4));
			endM = Integer.parseInt(endData.getHint().toString().substring(5, 7));
			endD = Integer.parseInt(endData.getHint().toString().substring(8, 10));
		}
		
		boolean flag = true;
		if(startY > endY){
			flag = false;
		}else if(startY == endY){
			if(startM > endM){
				flag = false;
			}else if(startM == endM){
				if(startD > endD){
					flag = false;
				}
			}
		}
		return flag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContent(R.layout.irisreport_select);
//		reportIndex = getIntent().getExtras().getInt("reportIndex");
		initView();
	}
}
