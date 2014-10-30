package com.jiuzhansoft.ehealthtec.weight;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.constant.ConstSysConfig;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class BodyfatRecord extends com.jiuzhansoft.ehealthtec.activity.BaseActivity{
	
	private int age;
	private boolean male;
	private int height;
	private SharedPreferences sharedPreferences;

	private DisplayMetrics dm;
	private TextView date, typeText, year, month, dataText, stateText, titleName, dateText;
	private ImageButton back, arrawLeft, arrawRight;
	
	private BodyfatRecordScrollView record;
	
	private List<Integer> dataIndex = new ArrayList<Integer>();
	private List<Float> data = new ArrayList<Float>();
	
	private boolean animDone;
	private ImageView indicator, indicatorBack;
	private float angle;
	private String month_to_year;
	
	private int showMode = 1;
	private List<Float> weightList = new ArrayList<Float>();
	private List<Integer> dampList = new ArrayList<Integer>();
	private List<String> dateList = new ArrayList<String>();
	
	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.bodyfat_record_activity);
		
		sharedPreferences = getSharedPreferences(ConstSysConfig.SYS_CUST_CLIENT, 0);
		age = sharedPreferences.getInt("age", 23);
		male = sharedPreferences.getBoolean("sex", true);
		height = sharedPreferences.getInt("height", 180);
		
		titleName = (TextView)findViewById(R.id.title_name);
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		record = (BodyfatRecordScrollView)findViewById(R.id.bodyfat_scroll);
		
		date = (TextView)findViewById(R.id.bodyfat_current_date);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currentdate = new Date(System.currentTimeMillis());
		String timestr = format.format(currentdate);
		date.setText(timestr);
		
		typeText = (TextView)findViewById(R.id.bodyfat_record_type);
		
		back = (ImageButton)findViewById(R.id.title_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		indicator = (ImageView)findViewById(R.id.record_indicator);
		indicatorBack = (ImageView)findViewById(R.id.record_indicator_background);
		dataText = (TextView)findViewById(R.id.bodyfat_record_data);
		stateText = (TextView)findViewById(R.id.bodyfat_record_state);
		
		year = (TextView)findViewById(R.id.bodyfat_record_year_mode);
		month = (TextView)findViewById(R.id.bodyfat_record_month_mode);
		year.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showMode == 2)
					return;
				year.setTextColor(Color.rgb(0, 124, 200));
				month.setTextColor(getResources().getColor(R.color.gray));
				record.setShowMode(BodyfatRecordScrollView.SHOW_YEAR);
				showMode = 2;
				month_to_year = dateText.getText().toString().split("-")[1];
				dateText.setText(dateText.getText().toString().split("-")[0]);
				getRecords();
			}
			
		});
		month.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showMode == 1)
					return;
				month.setTextColor(Color.rgb(0, 124, 200));
				year.setTextColor(getResources().getColor(R.color.gray));
				record.setShowMode(BodyfatRecordScrollView.SHOW_MONTH);
				showMode = 1;
				dateText.setText(dateText.getText().toString() + "-" + month_to_year);
				getRecords();
			}
			
		});
		
		dateText = (TextView)findViewById(R.id.bodyfat_record_date);
		arrawLeft = (ImageButton)findViewById(R.id.bodyfat_record_date_minus);
		arrawRight = (ImageButton)findViewById(R.id.bodyfat_record_date_plus);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
		Date currentdate1 = new Date(System.currentTimeMillis());
		String timestr1 = format1.format(currentdate1);
		dateText.setText(timestr1);
		dateText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DateTimePickDialogUtil dateTimePickDialog = 
					new DateTimePickDialogUtil(BodyfatRecord.this, dateText.getText().toString(), getResources().getString(R.string.sure), getResources().getString(R.string.cancel), showMode == 1 ? DateTimePickDialogUtil.YEAR_MONTH : DateTimePickDialogUtil.YEAR);
				dateTimePickDialog.dateTimePickDialog(dateText);				
			}
			
		});
		
		arrawLeft.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showMode == 1){
					String date = dateText.getText().toString();
					String month = date.split("-")[1];
					if(Integer.parseInt(month) == 1){
						String year = date.split("-")[0];
						year = Integer.parseInt(year) - 1 + "";
						month = 12 + "";
						date = year + "-" + month;
					}else{					
						if(Integer.parseInt(month) - 1 < 10){
							month = "0" + (Integer.parseInt(month) - 1);
						}else{
							month = Integer.parseInt(month) - 1 + "";
						}
						date = date.split("-")[0] + "-" + month;
					}
					dateText.setText(date);
				}else{
					dateText.setText(Integer.parseInt(dateText.getText().toString()) - 1 + "");
				}
				
				getRecords();				
			}
			
		});
		
		arrawRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(showMode == 1){
					String date = dateText.getText().toString();
					String month = date.split("-")[1];
					if(Integer.parseInt(month) == 12){
						String year = date.split("-")[0];
						year = Integer.parseInt(year) + 1 + "";
						month = "0" + 1;
						date = year + "-" + month;
					}else{
						if(Integer.parseInt(month) + 1 < 10){
							month = "0" + (Integer.parseInt(month) + 1);
						}else{
							month = Integer.parseInt(month) + 1 + "";
						}
						date = date.split("-")[0] + "-" + month;
					}
					dateText.setText(date);
				}else{
					dateText.setText(Integer.parseInt(dateText.getText().toString()) + 1 + "");
				}
				
				getRecords();	
			}
			
		});
		showIndicator();
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && animDone == false){
			animDone = true;
			Animation animation = new RotateAnimation(0, angle, (indicator.getMeasuredWidth()/2), (indicator.getMeasuredHeight()/2));
			animation.setFillAfter(true);
			animation.setDuration(1000);
			indicator.startAnimation(animation);
		}
	}
	
	private void showIndicator(){
		String type = getIntent().getExtras().getString("type");
		if("BMI".equals(type)){
			indicatorBack.setImageResource(R.drawable.bmi_indicator_background);
			typeText.setText("BMI");
			float BMI = getIntent().getExtras().getFloat("BMI");
			angle = BMI / 43 * 240;
			if(angle > 240)
				angle = 240;
			dataText.setText(BMI + "");
			if(BMI < 18.5){
				stateText.setText(getResources().getString(R.string.lean));
			}else if(BMI <= 25){
				stateText.setText(getResources().getString(R.string.normal));
			}else if(BMI <= 30){
				stateText.setText(getResources().getString(R.string.overweight));
			}else if(BMI <= 35){
				stateText.setText(getResources().getString(R.string.fat));
			}else{
				stateText.setText(getResources().getString(R.string.obese));
			}
			
			record.setMaxValue(50.0f);
			record.setMaxNormalValue(18.5f);
			record.setMinNormalValue(25.0f);
			titleName.setText(getResources().getString(R.string.normal_value) + "  18.5-25.0");
		}else if("weight".equals(type)){
			indicatorBack.setImageResource(R.drawable.bmi_indicator_background);
			typeText.setText(getResources().getString(R.string.weight1));
			float weight = getIntent().getExtras().getFloat("weight");
			angle = weight / height / height * 10000 / 43 * 240;
			if(angle > 240)
				angle = 240;
			dataText.setText(weight + "");
			float weightBMI = weight / height / height * 10000;
			if(weightBMI < 18.5){
				stateText.setText(getResources().getString(R.string.lean));
			}else if(weightBMI <= 25){
				stateText.setText(getResources().getString(R.string.normal));
			}else if(weightBMI <= 30){
				stateText.setText(getResources().getString(R.string.overweight));
			}else if(weightBMI <= 35){
				stateText.setText(getResources().getString(R.string.fat));
			}else{
				stateText.setText(getResources().getString(R.string.obese));
			}
			record.setMaxValue(50.0f * height * height / 10000);
			record.setMaxNormalValue(18.5f * height * height / 10000);
			record.setMinNormalValue(25.0f * height * height / 10000);
			titleName.setText(getResources().getString(R.string.normal_value) + " " + new DecimalFormat("0.0").format(18.5f * height * height / 10000) + "-" + new DecimalFormat("0.0").format(25.0f * height * height / 10000));
		}else if("fat".equals(type)){
			indicatorBack.setImageResource(R.drawable.fat_indicator_background);
			typeText.setText(getResources().getString(R.string.fat_percentage));
			float fat = getIntent().getExtras().getFloat("fat");
			angle = fat / 100 * 240;
			if(angle > 240)
				angle = 240;
			dataText.setText(fat + "");
			if(male == true){
				if(age <= 17){
					if(fat <= 12.0)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 17.0)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 22.0)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(17.0f);
					record.setMinNormalValue(22.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  12.1-17.0");
				}else if(age <= 30){
					if(fat <= 12.4)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 18.0)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 23.0)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(12.5f);
					record.setMinNormalValue(18.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  12.5-18.0");
				}else if(age <= 40){
					if(fat <= 13.0)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 18.4)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 23.0)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(13.1f);
					record.setMinNormalValue(18.4f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  13.1-18.4");
				}else if(age <= 60){
					if(fat <= 13.4)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 19.0)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 23.4)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(13.5f);
					record.setMinNormalValue(19.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  13.5-19.0");
				}else{
					if(fat <= 14.0)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 19.4)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 24.0)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(14.1f);
					record.setMinNormalValue(19.4f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  14.1-19.4");
				}
			}else{
				if(age <= 17){
					if(fat <= 15.0)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 22.0)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 26.4)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(15.1f);
					record.setMinNormalValue(22.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + " 15.1-22.0");
				}else if(age <= 30){
					if(fat <= 15.4)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 23.0)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 27.0)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(15.5f);
					record.setMinNormalValue(23.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  15.5-23.0");
				}else if(age <= 40){
					if(fat <= 16.0)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 23.4)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 27.4)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(16.0f);
					record.setMinNormalValue(23.5f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  16.0-23.5");
				}else if(age <= 60){
					if(fat <= 16.4)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 24.0)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 28.4)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(16.5f);
					record.setMinNormalValue(24.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  16.5-24.0");
				}else{
					if(fat <= 17.0)
						stateText.setText(getResources().getString(R.string.lean));
					else if(fat <= 24.4)
						stateText.setText(getResources().getString(R.string.normal));
					else if(fat <= 28.4)
						stateText.setText(getResources().getString(R.string.overweight));
					else 
						stateText.setText(getResources().getString(R.string.fat));
					record.setMaxNormalValue(17.1f);
					record.setMinNormalValue(24.4f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  17.1-24.4");
				}
			}
			if(fat == 0)
				stateText.setText(getResources().getString(R.string.no_data));
			record.setMaxValue(100.0f);
		}else if("water".equals(type)){
			indicatorBack.setImageResource(R.drawable.water_indicator_background);
			typeText.setText(getResources().getString(R.string.water_percentage));
			float water = getIntent().getExtras().getFloat("water");
			if(water >= 52 && water <= 62){
				angle = 80 + (water - 52) / 10 * 80;
			}else if(water <= 52){
				angle = water / 52 * 80;
			}else{
				angle = 160 + (water - 62) / 38 * 80;
			}
			if(angle > 240)
				angle = 240;
			dataText.setText(water + "");
			record.setMaxValue(100.0f);
			if(male == true){
				if(age <= 17){
					if(water < 57.0)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 62.0)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(57.0f);
					record.setMinNormalValue(62.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  57.0-62.0");
				}else if(age <= 30){
					if(water < 56.5)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 61.5)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(56.5f);
					record.setMinNormalValue(61.5f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  56.5-61.5");
				}else if(age <= 40){
					if(water < 56.0)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 61.0)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(56.0f);
					record.setMinNormalValue(61.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  56.0-61.0");
				}else if(age <= 60){
					if(water < 55.5)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 60.5)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(55.5f);
					record.setMinNormalValue(60.5f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  55.5-60.5");
				}else{
					if(water < 55.0)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 60.0)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(55.0f);
					record.setMinNormalValue(60.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  55.0-60.0");
				}
			}else{
				if(age <= 17){
					if(water < 54.0)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 60.0)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(54.0f);
					record.setMinNormalValue(60.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  54.0-60.0");
				}else if(age <= 30){
					if(water < 53.5)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 59.5)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(53.5f);
					record.setMinNormalValue(59.5f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  53.5-59.5");
				}else if(age <= 40){
					if(water < 53.0)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 59.0)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(53.0f);
					record.setMinNormalValue(59.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  53.0-59.0");
				}else if(age <= 60){
					if(water < 55.5)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 60.5)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(52.5f);
					record.setMinNormalValue(58.5f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  52.5-58.5");
				}else{
					if(water < 52.0)
						stateText.setText(getResources().getString(R.string.low));
					else if(water <= 58.0)
						stateText.setText(getResources().getString(R.string.normal));
					else 
						stateText.setText(getResources().getString(R.string.high));
					record.setMaxNormalValue(52.0f);
					record.setMinNormalValue(58.0f);
					titleName.setText(getResources().getString(R.string.normal_value) + "  52.0-58.0");
				}				
			}
			if(water == 0)
				stateText.setText(getResources().getString(R.string.no_data));
		}else if("muscle".equals(type)){
			indicatorBack.setImageResource(R.drawable.water_indicator_background);
			typeText.setText(getResources().getString(R.string.muscle_percentage));
			float muscle = getIntent().getExtras().getFloat("muscle");
			angle = muscle / 100 * 240;
			if(angle > 240)
				angle = 240;
			dataText.setText(muscle + "");
			stateText.setText("");
			record.setMaxValue(100.0f);
			if(muscle == 0)
				stateText.setText(getResources().getString(R.string.no_data));
		}else if("skeleton".equals(type)){
			indicatorBack.setImageResource(R.drawable.water_indicator_background);
			typeText.setText(getResources().getString(R.string.skeleton_percentage));
			float skeleton = getIntent().getExtras().getFloat("skeleton");
			angle = skeleton / 25 * 240;
			if(angle > 240)
				angle = 240;
			dataText.setText(skeleton + "");
			stateText.setText("");
			record.setMaxValue(25.0f);
			if(skeleton == 0)
				stateText.setText(getResources().getString(R.string.no_data));
		}else if("calurie".equals(type)){
			indicatorBack.setImageResource(R.drawable.water_indicator_background);
			typeText.setText(getResources().getString(R.string.calurie_used));
			float calurie = getIntent().getExtras().getFloat("calurie");
			angle = calurie / 5000 * 240;
			if(angle > 240)
				angle = 240;
			dataText.setText(calurie + "");
			stateText.setText("");
			record.setMaxValue(5000.0f);
			if(male == true){
				if(age <= 2){
					record.setMaxNormalValue(700);
					titleName.setText(getResources().getString(R.string.normal_value) + "  700");
				}else if(age <= 5){
					record.setMaxNormalValue(900);
					titleName.setText(getResources().getString(R.string.normal_value) + "  900");
				}else if(age <= 8){
					record.setMaxNormalValue(1090);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1090");
				}else if(age <= 11){
					record.setMaxNormalValue(1290);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1290");
				}else if(age <= 14){
					record.setMaxNormalValue(1480);
					titleName.setText(getResources().getString(R.string.normal_value) + " 1480");
				}else if(age <= 17){
					record.setMaxNormalValue(1610);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1610");
				}else if(age <= 29){
					record.setMaxNormalValue(1550);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1550");
				}else if(age <= 49){
					record.setMaxNormalValue(1500);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1500");
				}else if(age <= 69){
					record.setMaxNormalValue(1350);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1350");
				}else{
					record.setMaxNormalValue(1220);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1220");
				}
			}else{
				if(age <= 2){
					record.setMaxNormalValue(700);
					titleName.setText("getResources().getString(R.string.normal_value) +   700");
				}else if(age <= 5){
					record.setMaxNormalValue(860);
					titleName.setText(getResources().getString(R.string.normal_value) + "  860");
				}else if(age <= 8){
					record.setMaxNormalValue(1000);
					titleName.setText(getResources().getString(R.string.normal_value) + " 1000");
				}else if(age <= 11){
					record.setMaxNormalValue(1180);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1180");
				}else if(age <= 14){
					record.setMaxNormalValue(1340);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1340");
				}else if(age <= 17){
					record.setMaxNormalValue(1300);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1300");
				}else if(age <= 29){
					record.setMaxNormalValue(1210);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1210");
				}else if(age <= 49){
					record.setMaxNormalValue(1170);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1170");
				}else if(age <= 69){
					record.setMaxNormalValue(1110);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1110");
				}else{
					record.setMaxNormalValue(1010);
					titleName.setText(getResources().getString(R.string.normal_value) + "  1010");
				}
			}
			if(calurie == 0)
				stateText.setText(getResources().getString(R.string.no_data));
		}
		getRecords();
	}
	
	public void getRecords(){
		record.setDate(dateText.getText().toString());
		String timestr = dateText.getText().toString();
		String startDate = "";
		String endDate = "";
		if(showMode == 1){
			int month = Integer.parseInt(timestr.split("-")[1]);
			int endDay = 0;
			if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
				endDay = 31;
			}else if(month == 2){
				int year = Integer.parseInt(timestr.split("-")[0]);
				if(year % 4 == 0){
					endDay = 29;
				}else{
					endDay = 28;
				}
			}else{
				endDay = 30;
			}
			startDate = timestr + "-01 00:00:00";
			endDate = timestr + "-" + endDay + " 23:59:59";
		}else{
			startDate = timestr + "-01-01 00:00:00";
			endDate = timestr + "-12-31 23:59:59";
		}
		
		String userPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("userId", userPin);
			jsonobject.put("beginTime", startDate);
			jsonobject.put("endTime", endDate);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.BODYFATREPORT);
		httpsetting.setRequestMethod("GET");
		httpsetting.setJsonParams(jsonobject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
				weightList.clear();
				dampList.clear();
				dateList.clear();
				
				JSONObjectProxy json = response.getJSONObject();
				if(json == null)return;
				try {
					int code = json.getInt("code");
					String msg = json.getString("msg");
					JSONArrayPoxy object = json.getJSONArrayOrNull("object");
					if(code == 1 && object != null){
						for(int i = 0; i < object.length(); i++){
							JSONObjectProxy objectproxy;
							try {
								objectproxy = object.getJSONObject(i);	
								dateList.add(objectproxy.getStringOrNull("date"));
								weightList.add(Float.parseFloat(objectproxy.getStringOrNull("weight")));
								dampList.add(objectproxy.getIntOrNull("impedance"));
								showRecords();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
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
	private void showRecords(){
		dataIndex.clear();
		data.clear();
		if(showMode == 1){
			for(int i = 0; i < dateList.size(); i++){
				if(dampList.get(i) == 0){
					continue;
				}
				int day = Integer.parseInt(dateList.get(i).split(" ")[0].split("-")[2]);
				if(dataIndex.size() == 0){
					dataIndex.add(day);
				}else{
					boolean has = false;
					for(int j = 0; j < dataIndex.size(); j++){
						if(day == dataIndex.get(j)){
							has = true;
						}
					}
					if(has == false)
						dataIndex.add(day);
				}
			}
			String type = getIntent().getExtras().getString("type");
			for(int i = 0; i < dataIndex.size(); i++){
				int num = 0;
				float dataT = 0.0f;
				for(int j = 0; j < dateList.size(); j++){
					if(dampList.get(j) == 0){
						continue;
					}
					if(Integer.parseInt(dateList.get(j).split(" ")[0].split("-")[2]) == dataIndex.get(i)){
						if("weight".equals(type)){
							dataT = dataT + weightList.get(j);
						}else if("BMI".equals(type)){
							dataT = dataT + weightList.get(j) / height / height * 10000;
						}else if("fat".equals(type)){
							if(dampList.get(j) == 0){
								continue;
							}else{
								if(male == true){
									if(weightList.get(j) >= 64.0)
										dataT = dataT + ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*42*(weightList.get(j)+64))/(height*height))-5800)/10)*(age+230)/256;
									else
										dataT = dataT + ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/(height*height))-5800)/10)*(age+230)/256;
								}else{
									dataT = dataT + ((64500000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/ (height*height))-5800)/10)*(age+230)/256;
								}
							}
						}else if("water".equals(type)){
							float water = 0;
							if(dampList.get(j) == 0){
								continue;
							}else{
								if(male == true){
									if(weightList.get(j) >= 64.0)
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*42*(weightList.get(j)+64))/(height*height))-5800)/10)*(age+230)/256;
									else
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/(height*height))-5800)/10)*(age+230)/256;
								}else{
									water = ((64500000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/ (height*height))-5800)/10)*(age+230)/256;
								}
							}
							dataT = dataT + (1 - water / 100) * 12 / 16 * 100;
						}else if("muscle".equals(type)){
							float water = 0;
							if(dampList.get(j) == 0){
								continue;
							}else{
								if(male == true){
									if(weightList.get(j) >= 64.0)
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*42*(weightList.get(j)+64))/(height*height))-5800)/10)*(age+230)/256;
									else
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/(height*height))-5800)/10)*(age+230)/256;
								}else{
									water = ((64500000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/ (height*height))-5800)/10)*(age+230)/256;
								}
							}
							water = (1 - water / 100) * 12 / 16 * 100;
							if(male == true){
								dataT = dataT + (float) (water*(0.95-0.0578*age/weightList.get(j)-0.00038*height));								
							}else{
								dataT = dataT + (float) (water*(0.88-0.0578*age/weightList.get(j)-0.00025*height));						
							}
						}else if("skeleton".equals(type)){
							if(male == true){
								dataT = dataT + (float) ((0.0887*weightList.get(j)+0.0896*age+0.0354*height-4.53) * 0.8);
							}else{
								dataT = dataT + (float) ((0.0676*weightList.get(j)+0.0900*age+0.0356*height-5.30) * 0.8);
							}
						}else{
							if(male == true){
								dataT = dataT + (float) ((66+14.7*weightList.get(j)+5*height-5.8*age)*1.1);
							}else{
								dataT = dataT + (float) ((655+10.6*weightList.get(j)+1.85*height-3.7*age)*1.1);
							}
						}						
						num++;
					}
				}
				if(num != 0){
					dataT = Float.parseFloat(new DecimalFormat("0.0").format(dataT / num));
					data.add(dataT);
				}
			}
		}else{
			for(int i = 0; i < dateList.size(); i++){
				if(dampList.get(i) == 0){
					continue;
				}
				int month = Integer.parseInt(dateList.get(i).split("-")[1]);
				if(dataIndex.size() == 0){
					dataIndex.add(month);
				}else{
					boolean has = false;
					for(int j = 0; j < dataIndex.size(); j++){						
						if(month == dataIndex.get(j)){
							has = true;
						}
					}
					if(has == false)
						dataIndex.add(month);
				}
			}
			String type = getIntent().getExtras().getString("type");
			for(int i = 0; i < dataIndex.size(); i++){
				int num = 0;
				float dataT = 0.0f;
				for(int j = 0; j < dateList.size(); j++){
					if(dampList.get(j) == 0){
						continue;
					}
					if(Integer.parseInt(dateList.get(j).split("-")[1]) == dataIndex.get(i)){
						if("weight".equals(type)){
							dataT = dataT + weightList.get(j);
						}else if("BMI".equals(type)){
							dataT = dataT + weightList.get(j) / height / height * 10000;
						}else if("fat".equals(type)){
							if(dampList.get(j) == 0){
								continue;
							}else{
								if(male == true){
									if(weightList.get(j) >= 64.0)
										dataT = dataT + ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*42*(weightList.get(j)+64))/(height*height))-5800)/10)*(age+230)/256;
									else
										dataT = dataT + ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/(height*height))-5800)/10)*(age+230)/256;
								}else{
									dataT = dataT + ((64500000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/ (height*height))-5800)/10)*(age+230)/256;
								}
							}
						}else if("water".equals(type)){
							float water = 0;
							if(dampList.get(j) == 0){
								continue;
							}else{
								if(male == true){
									if(weightList.get(j) >= 64.0)
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*42*(weightList.get(j)+64))/(height*height))-5800)/10)*(age+230)/256;
									else
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/(height*height))-5800)/10)*(age+230)/256;
								}else{
									water = ((64500000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/ (height*height))-5800)/10)*(age+230)/256;
								}
							}
							dataT = dataT + (1 - water / 100) * 12 / 16 * 100;
						}else if("muscle".equals(type)){
							float water = 0;
							if(dampList.get(j) == 0){
								continue;
							}else{
								if(male == true){
									if(weightList.get(j) >= 64.0)
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*42*(weightList.get(j)+64))/(height*height))-5800)/10)*(age+230)/256;
									else
										water = ((63700000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/(height*height))-5800)/10)*(age+230)/256;
								}else{
									water = ((64500000/(11554-((4750+Math.abs(age-30)+dampList.get(j))*84*weightList.get(j))/ (height*height))-5800)/10)*(age+230)/256;
								}
							}
							water = (1 - water / 100) * 12 / 16 * 100;
							if(male == true){
								dataT = dataT + (float) (water*(0.95-0.0578*age/weightList.get(j)-0.00038*height));								
							}else{
								dataT = dataT + (float) (water*(0.88-0.0578*age/weightList.get(j)-0.00025*height));						
							}
						}else if("skeleton".equals(type)){
							if(male == true){
								dataT = dataT + (float) ((0.0887*weightList.get(j)+0.0896*age+0.0354*height-4.53) * 0.8);
							}else{
								dataT = dataT + (float) ((0.0676*weightList.get(j)+0.0900*age+0.0356*height-5.30) * 0.8);
							}
						}else{
							if(male == true){
								dataT = dataT + (float) ((66+14.7*weightList.get(j)+5*height-5.8*age)*1.1);
							}else{
								dataT = dataT + (float) ((655+10.6*weightList.get(j)+1.85*height-3.7*age)*1.1);
							}
						}						
						num++;
					}
				}
				if(num != 0){
					dataT = Float.parseFloat(new DecimalFormat("0.0").format(dataT / num));
					data.add(dataT);
				}
			}
		}
		
		record.setList(dataIndex, data);
	}
}
