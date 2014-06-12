package com.hengxuan.ehealthplatform.weight;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeightDataAnalysis extends BaseActivity{
	private int age;
	private boolean male;
	private int height;
	private SharedPreferences sharedPreferences;
	private boolean animDone;
	
	private TextView date, titleName, personnalInfo;
	private ImageButton back;
	private int damp;
	private float weight, BMI, fat, water, muscle, skeleton, calorie;	
	private BodyfatScaleView scale;
	private Button kgSmall, changeInfo, submit;
	private boolean hasSubmit = false;
	private boolean submitEnable = false;
	
	private ImageView BMIIndicator, fatIndicator, waterIndicator, muscleIndicator, skeletonIndicator, calurieIndicator;
	private TextView BMIState, BMIData, fatState, fatData, waterState, waterData, muscleState, muscleData, skeletonState, skeletonData, calurieState, calurieData;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bodyfat_analysis);
		
		sharedPreferences = getSharedPreferences(ConstSysConfig.SYS_CUST_CLIENT, 0);
		age = sharedPreferences.getInt("age", 23);
		male = sharedPreferences.getBoolean("sex", true);
		height = sharedPreferences.getInt("height", 180);
		hasSubmit = getIntent().getExtras().getBoolean("hasSubmit");
		submitEnable = getIntent().getExtras().getBoolean("submitEnable");
		titleName = (TextView)findViewById(R.id.title_name);
		titleName.setText(getResources().getString(R.string.weight_analysis));
		submit = (Button)findViewById(R.id.title_right);
		
		weight = getIntent().getExtras().getFloat("weight");
		damp = getIntent().getExtras().getInt("damp");
		
		if(submitEnable == true)
			submit.setEnabled(true);
		else 
			submit.setEnabled(false);
		if(hasSubmit == true){
			submit.setText(getResources().getString(R.string.submitted));
			submit.setEnabled(false);
		}else{
			submit.setText(getResources().getString(R.string.app_error_submit));
		}
		submit.setVisibility(View.VISIBLE);
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

		BMIState = (TextView)findViewById(R.id.analysis_bmi_state);
		BMIData = (TextView)findViewById(R.id.analysis_bmi_data);
		fatState = (TextView)findViewById(R.id.analysis_fat_state);
		fatData = (TextView)findViewById(R.id.analysis_fat_data);
		waterState = (TextView)findViewById(R.id.analysis_water_state);
		waterData = (TextView)findViewById(R.id.analysis_water_data);
		muscleState = (TextView)findViewById(R.id.analysis_muscle_state);
		muscleData = (TextView)findViewById(R.id.analysis_muscle_data);
		skeletonState = (TextView)findViewById(R.id.analysis_skeleton_state);
		skeletonData = (TextView)findViewById(R.id.analysis_skeleton_data);
		calurieState = (TextView)findViewById(R.id.analysis_calurie_state);
		calurieData = (TextView)findViewById(R.id.analysis_calurie_data);
		
		calculate();	
		
		date = (TextView)findViewById(R.id.bodyfat_date);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date currentdate = new Date(System.currentTimeMillis());
		String timestr = format.format(currentdate);
		date.setText(timestr);
		
		personnalInfo = (TextView)findViewById(R.id.bodyfat_analysis_personal_info);
		personnalInfo.setText(getResources().getString(R.string.sex) + (male == true ? getResources().getString(R.string.male) : getResources().getString(R.string.female)) + "  " + getResources().getString(R.string.age) + age +  "  " + getResources().getString(R.string.height) + height + "cm");
		
		changeInfo = (Button)findViewById(R.id.bodyfat_change_personal_info);
		changeInfo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, ImprovePersonalInformation.class);
				startActivity(intent);
			}
			
		});
		
		back = (ImageButton)findViewById(R.id.title_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("hasSubmit", hasSubmit);
				setResult(RESULT_OK, intent);
				finish();
			}
			
		});
		scale = (BodyfatScaleView)findViewById(R.id.bodyfat_scale);
		scale.setWeight(weight);
		scale.invalidate();
		scale.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, BodyfatRecord.class);
				intent.putExtra("type", "weight");
				intent.putExtra("weight", weight);
				startActivity(intent);
			}
			
		});
		
		kgSmall = (Button)findViewById(R.id.weight_scale_button);
		kgSmall.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if((kgSmall.getText().toString()).equals("Kg")){
					kgSmall.setText("Pound");
					scale.setModePound(true);
				}else{
					kgSmall.setText("Kg");
					scale.setModePound(false);
				}
			}
			
		});
		BMIIndicator = (ImageView)findViewById(R.id.bmi_indicator);
		fatIndicator = (ImageView)findViewById(R.id.fat_indicator);
		waterIndicator = (ImageView)findViewById(R.id.water_indicator);
		muscleIndicator = (ImageView)findViewById(R.id.muscle_indicator);
		skeletonIndicator = (ImageView)findViewById(R.id.skeleton_indicator);
		calurieIndicator = (ImageView)findViewById(R.id.calurie_indicator);
		BMIIndicator.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, BodyfatRecord.class);
				intent.putExtra("type", "BMI");
				intent.putExtra("BMI", BMI);
				startActivity(intent);
			}
			
		});
		fatIndicator.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, BodyfatRecord.class);
				intent.putExtra("type", "fat");
				intent.putExtra("fat", fat);
				startActivity(intent);
			}
			
		});
		waterIndicator.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, BodyfatRecord.class);
				intent.putExtra("type", "water");
				intent.putExtra("water", water);
				startActivity(intent);
			}
			
		});
		muscleIndicator.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, BodyfatRecord.class);
				intent.putExtra("type", "muscle");
				intent.putExtra("muscle", muscle);
				startActivity(intent);
			}
			
		});
		skeletonIndicator.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, BodyfatRecord.class);
				intent.putExtra("type", "skeleton");
				intent.putExtra("skeleton", skeleton);
				startActivity(intent);
			}
			
		});
		calurieIndicator.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeightDataAnalysis.this, BodyfatRecord.class);
				intent.putExtra("type", "calurie");
				intent.putExtra("calurie", calorie);
				startActivity(intent);
			}
			
		});
		
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && animDone == false){
			animDone = true;
			indicatorRotate();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(age != sharedPreferences.getInt("age", 0) || male != sharedPreferences.getBoolean("sex", true) || height != sharedPreferences.getInt("height", 0)){
			age = sharedPreferences.getInt("age", 23);
			male = sharedPreferences.getBoolean("sex", true);
			height = sharedPreferences.getInt("height", 180);
			calculate();
			indicatorRotate();
			personnalInfo.setText(getResources().getString(R.string.sex) + (male == true ? getResources().getString(R.string.male) : getResources().getString(R.string.female)) + "  " + getResources().getString(R.string.age) + age +  "  " + getResources().getString(R.string.height) + height + "cm");
		}
	}
	
	private void calculate(){
		if(weight == 0.0f){
			BMI = 0.0f;
			fat = 0.0f;
			water = 0.0f;
			muscle = 0.0f;
			skeleton = 0.0f;
			calorie = 0.0f;
		}else{
			BMI = weight / height / height * 10000;
			if(damp == 0){
				fat = 0.0f;
				water = 0.0f;
				muscle = 0.0f;
				skeleton = 0.0f;
				calorie = 0.0f;
			}else{
				if(male == true){
					if(weight >= 64.0)
						fat = ((63700000/(11554-((4750+Math.abs(age-30)+damp)*42*(weight+64))/(height*height))-5800)/10)*(age+230)/256;
					else
						fat = ((63700000/(11554-((4750+Math.abs(age-30)+damp)*84*weight)/(height*height))-5800)/10)*(age+230)/256;
				}else{
					fat =((64500000/(11554-((4750+Math.abs(age-30)+damp)*84*weight)/ (height*height))-5800)/10)*(age+230)/256;
				}
				water = (1 - fat / 100) * 12 / 16 * 100;
				if(male == true){
					muscle = (float) (water*(0.95-0.0578*age/weight-0.00038*height));
					skeleton = (float) ((0.0887*weight+0.0896*age+0.0354*height-4.53) * 0.8);
					calorie = (float) ((66+14.7*weight+5*height-5.8*age)*1.1);
				}else{
					muscle = (float) (water*(0.88-0.0578*age/weight-0.00025*height));
					skeleton = (float) ((0.0676*weight+0.0900*age+0.0356*height-5.30) * 0.8);
					calorie = (float) ((655+10.6*weight+1.85*height-3.7*age)*1.1);
				}
			}
		}
		
		BMI = Float.parseFloat(new DecimalFormat("0.0").format(BMI));
		fat = Float.parseFloat(new DecimalFormat("0.0").format(fat));
		water = Float.parseFloat(new DecimalFormat("0.0").format(water));
		muscle = Float.parseFloat(new DecimalFormat("0.0").format(muscle));
		skeleton = Float.parseFloat(new DecimalFormat("0.0").format(skeleton));
		calorie = Float.parseFloat(new DecimalFormat("0.0").format(calorie));	
		BMIData.setText(BMI + "");
		fatData.setText(fat + "");
		waterData.setText(water + "");
		muscleData.setText(muscle + "");
		skeletonData.setText(skeleton + "");
		calurieData.setText(calorie + "");
		if(BMI < 18.5){
			BMIState.setText(getResources().getString(R.string.lean));
		}else if(BMI <= 25){
			BMIState.setText(getResources().getString(R.string.normal));
		}else if(BMI <= 30){
			BMIState.setText(getResources().getString(R.string.overweight));
		}else if(BMI <= 35){
			BMIState.setText(getResources().getString(R.string.fat));
		}else{
			BMIState.setText(getResources().getString(R.string.obese));
		}
		if(damp == 0)
			return;
		if(male == true){
			if(age <= 17){
				if(fat <= 12.0)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 17.0)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 22.0)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 57.0)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 62.0)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else if(age <= 30){
				if(fat <= 12.40)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 18.0)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 23.0)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 56.5)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 61.5)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else if(age <= 40){
				if(fat <= 13.0)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 18.4)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 23.0)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 56.0)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 61.0)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else if(age <= 60){
				if(fat <= 13.4)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 19.0)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 23.4)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 55.5)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 60.5)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else{
				if(fat <= 14.0)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 19.4)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 24.0)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 55.0)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 60.0)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}
		}else{
			if(age <= 17){
				if(fat <= 15.0)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 22.0)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 26.4)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 54.0)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 60.0)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else if(age <= 30){
				if(fat <= 15.40)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 23.0)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 27.0)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 53.5)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 59.5)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else if(age <= 40){
				if(fat <= 16.0)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 23.4)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 27.4)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 53.0)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 59.0)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else if(age <= 60){
				if(fat <= 16.4)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 24.0)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 28.0)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 52.5)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 58.5)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}else{
				if(fat <= 17.0)
					fatState.setText(getResources().getString(R.string.lean));
				else if(fat <= 24.4)
					fatState.setText(getResources().getString(R.string.normal));
				else if(fat <= 28.0)
					fatState.setText(getResources().getString(R.string.overweight));
				else 
					fatState.setText(getResources().getString(R.string.fat));
				if(water < 52.0)
					waterState.setText(getResources().getString(R.string.low));
				else if(water <= 58.0)
					waterState.setText(getResources().getString(R.string.normal));
				else 
					waterState.setText(getResources().getString(R.string.high));
			}
		}
		
		muscleState.setText("");
		skeletonState.setText("");
		calurieState.setText("");
	}
	private void indicatorRotate(){
		float bmiAngle = BMI / 43 * 240;
		if(bmiAngle > 240)
			bmiAngle = 240;
		Animation animationBMI = new RotateAnimation(0, bmiAngle, (BMIIndicator.getMeasuredWidth()/2), (BMIIndicator.getMeasuredHeight()/2));
		animationBMI.setFillAfter(true);
		animationBMI.setDuration(1000);
		BMIIndicator.startAnimation(animationBMI);
		
		float fatAngle = fat / 100 * 240;
		if(fatAngle > 240)
			fatAngle = 240;
		Animation animationFat = new RotateAnimation(0, fatAngle, (BMIIndicator.getMeasuredWidth()/2), (BMIIndicator.getMeasuredHeight()/2));
		animationFat.setFillAfter(true);
		animationFat.setDuration(1000);
		fatIndicator.startAnimation(animationFat);
		
		float waterAngle = 0;
		if(water >= 52 && water <= 62){
			waterAngle = 80 + (water - 52) / 10 * 80;
		}else if(water <= 52){
			waterAngle = water / 52 * 80;
		}else{
			waterAngle = 160 + (water - 62) / 38 * 80;
		}
		if(waterAngle > 240)
			waterAngle = 240;
		Animation animationWater = new RotateAnimation(0, waterAngle, (BMIIndicator.getMeasuredWidth()/2), (BMIIndicator.getMeasuredHeight()/2));
		animationWater.setFillAfter(true);
		animationWater.setDuration(1000);
		waterIndicator.startAnimation(animationWater);
		
		float muscleAngle = muscle / 100 * 240;
		if(muscleAngle > 240)
			muscleAngle = 240;
		Animation animationMuscle = new RotateAnimation(0, muscleAngle, (BMIIndicator.getMeasuredWidth()/2), (BMIIndicator.getMeasuredHeight()/2));
		animationMuscle.setFillAfter(true);
		animationMuscle.setDuration(1000);
		muscleIndicator.startAnimation(animationMuscle);
		
		float skeletonAngle = skeleton / 25 * 240;
		if(skeletonAngle > 240)
			skeletonAngle = 240;
		Animation animationSkeleton = new RotateAnimation(0, skeletonAngle, (BMIIndicator.getMeasuredWidth()/2), (BMIIndicator.getMeasuredHeight()/2));
		animationSkeleton.setFillAfter(true);
		animationSkeleton.setDuration(1000);
		skeletonIndicator.startAnimation(animationSkeleton);
		
		float calurieAngle = calorie / 5000 * 240;
		if(calurieAngle > 240)
			calurieAngle = 240;
		Animation animationCalurie = new RotateAnimation(0, calurieAngle, (BMIIndicator.getMeasuredWidth()/2), (BMIIndicator.getMeasuredHeight()/2));
		animationCalurie.setFillAfter(true);
		animationCalurie.setDuration(1000);
		calurieIndicator.startAnimation(animationCalurie);
	}
	private void addToServer(String currentDate, String userPin){
		JSONObject jsonobject=new JSONObject();
		try {
			jsonobject.put("client_code", "SZ-Youruien");
			jsonobject.put("currentDate", currentDate);
			jsonobject.put("userPin", userPin);
			jsonobject.put("weight", weight);
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
							Toast.makeText(WeightDataAnalysis.this, getResources().getString(R.string.addtoreport), Toast.LENGTH_SHORT).show();
							submit.setEnabled(false);
							submit.setText(getResources().getString(R.string.submitted));
							hasSubmit = true;
						}
						else{
							Toast.makeText(WeightDataAnalysis.this, getResources().getString(R.string.failedtoadd), Toast.LENGTH_SHORT).show();
							submit.setEnabled(true);
							hasSubmit = true;
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
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
			intent.putExtra("hasSubmit", hasSubmit);
			setResult(RESULT_OK, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
