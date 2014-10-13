package com.jiuzhansoft.ehealthtec.weight;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.jiuzhansoft.ehealthtec.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;


public class DateTimePickDialogUtil implements OnDateChangedListener{

	public static final int YEAR_MONTH_DAY = 1;
	public static final int YEAR_MONTH = 2;
	public static final int YEAR = 3;
	private DatePicker datePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private String okstr, canclestr;
	private int mode = YEAR_MONTH_DAY;
	
	public DateTimePickDialogUtil(Activity activity, String initDateTime, String okstr, String canclestr, int mode){
		this.activity = activity;
		this.initDateTime = initDateTime;
		this.okstr = okstr;
		this.canclestr = canclestr;
		this.mode = mode;
	}
	
	public void init(DatePicker datePicker){
		Calendar calendar = Calendar.getInstance();
		initDateTime = calendar.get(Calendar.YEAR)+"-"
			+ calendar.get(Calendar.MONTH)+"-"
			+ calendar.get(Calendar.DAY_OF_MONTH);
		
		datePicker.init(calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH), this);
	}
	
	public AlertDialog dateTimePickDialog(final TextView startData){
		LinearLayout dateTimeLayout = 
			(LinearLayout) activity.getLayoutInflater().inflate(R.layout.mydatepicker, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		init(datePicker);
		
		ad = new AlertDialog.Builder(activity)
		.setIcon(R.drawable.android_ratingbar_single_light)
		.setTitle(initDateTime).setView(dateTimeLayout)
		.setPositiveButton(okstr, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				startData.setText(dateTime);
				if(activity instanceof BodyfatRecord){
					((BodyfatRecord)activity).getRecords();
				}
			}
		})
		.setNegativeButton(canclestr, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				//startData.setText("");
			}
		})
		.show();
		onDateChanged(null, 0, 0, 0);
		return ad;
	}	

	@Override
	public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
		SimpleDateFormat sdf = null;
		if(mode == YEAR_MONTH_DAY)
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		else if(mode == YEAR_MONTH)
			sdf = new SimpleDateFormat("yyyy-MM");
		else if(mode == YEAR)
			sdf = new SimpleDateFormat("yyyy");
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}
}
