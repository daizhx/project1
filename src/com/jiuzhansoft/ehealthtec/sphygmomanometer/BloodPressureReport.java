package com.jiuzhansoft.ehealthtec.sphygmomanometer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.activity.BaseActivity;
import com.jiuzhansoft.ehealthtec.http.HttpError;
import com.jiuzhansoft.ehealthtec.http.HttpGroup;
import com.jiuzhansoft.ehealthtec.http.HttpGroupaAsynPool;
import com.jiuzhansoft.ehealthtec.http.HttpResponse;
import com.jiuzhansoft.ehealthtec.http.HttpSetting;
import com.jiuzhansoft.ehealthtec.http.constant.ConstFuncId;
import com.jiuzhansoft.ehealthtec.http.constant.ConstHttpProp;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;


public class BloodPressureReport extends BaseActivity{
	
	private String startDate, endDate, userPin;
	private TabHost tabHost;
	private TabWidget tabWidget;
	private int tiao;
	private double[] daysa,dayda,daypa;
	private XYMultipleSeriesDataset ds;
	private XYMultipleSeriesRenderer render;
	private XYSeriesRenderer xyRender;
	private TimeSeries series;
	private GraphicalView gv;
	LinearLayout layout,dianchart, barchart;
	List<Date[]> datess = new ArrayList<Date[]>();
	Date[] de;
	private First first;
	private String[] titles;
	private int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLUE};
	ArrayList arrList = new ArrayList(); 
	ArrayList sysList = new ArrayList(); 
	ArrayList diaList = new ArrayList(); 
	ArrayList pulList = new ArrayList();
	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, String>> dateList;
	private BloodPressureReportView reportView;
	private int deletePos;
	private String currentDate;
	
	//柱状图
	private int barMode = 2;
	private double[] sysBar, diaBar;
	private String startBarDate, endBarDate;
	ArrayList<String> dateBarList = new ArrayList<String>();
	ArrayList<Integer> sysBarList = new ArrayList<Integer>();
	ArrayList<Integer> diaBarList = new ArrayList<Integer>();
	private String[] barTitles;
	private Button week, month, year;
	private ImageButton arrawLeft, arrawRight;
	private TextView dateText;
	private boolean hasGotData = false;
	private int width;
	@Override
	public void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContent(R.layout.blood_pressure_report);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		
		titles = new String[] { "SYS", "PUL",
				"DIA"};
		barTitles = new String[]{"SYS", "DIA"};
		tabHost = (TabHost)findViewById(R.id.blood_pressure_tabhost);
		tabHost.setup();
		tabWidget = tabHost.getTabWidget();
		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(getResources().getString(R.string.trend)).setContent(R.id.blood_pressure_tab1));
		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(getResources().getString(R.string.memory)).setContent(R.id.blood_pressure_tab2));  
		tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(getResources().getString(R.string.barchart)).setContent(R.id.blood_pressure_tab3)); 
		tabWidget.getChildAt(0).setBackgroundResource(R.drawable.bottom_selected);
		tabWidget.getChildAt(1).setBackgroundResource(R.drawable.bottom_normal);
		tabWidget.getChildAt(2).setBackgroundResource(R.drawable.bottom_normal);
		((TextView)(tabWidget.getChildAt(0).findViewById(android.R.id.title))).setTextSize(20);
		((TextView)(tabWidget.getChildAt(1).findViewById(android.R.id.title))).setTextSize(20);
		((TextView)(tabWidget.getChildAt(2).findViewById(android.R.id.title))).setTextSize(20);
		tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				if(tabHost.getCurrentTab() == 0){
					tabWidget.getChildAt(0).setBackgroundResource(R.drawable.bottom_selected);
					tabWidget.getChildAt(1).setBackgroundResource(R.drawable.bottom_normal);
					tabWidget.getChildAt(2).setBackgroundResource(R.drawable.bottom_normal);
				}else if(tabHost.getCurrentTab() == 1){
					tabWidget.getChildAt(0).setBackgroundResource(R.drawable.bottom_normal);
					tabWidget.getChildAt(1).setBackgroundResource(R.drawable.bottom_selected);
					tabWidget.getChildAt(2).setBackgroundResource(R.drawable.bottom_normal);
				}else{
					tabWidget.getChildAt(0).setBackgroundResource(R.drawable.bottom_normal);
					tabWidget.getChildAt(1).setBackgroundResource(R.drawable.bottom_normal);
					tabWidget.getChildAt(2).setBackgroundResource(R.drawable.bottom_selected);
					if(hasGotData == false)
						getBarData();
				}
			}
			
		});
		
		layout  = (LinearLayout) findViewById(R.id.blood_pressure_chart);
		barchart = (LinearLayout)findViewById(R.id.blood_pressure_bar_chart);
		week = (Button)findViewById(R.id.blood_pressure_barchart_week);
		month = (Button)findViewById(R.id.blood_pressure_barchart_month);
		year = (Button)findViewById(R.id.blood_pressure_barchart_year);
		week.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(barMode == 1)
					return;
				barMode = 1;
				week.setBackgroundResource(R.drawable.round_corner_selected);
				month.setBackgroundResource(R.drawable.round_corner);
				year.setBackgroundResource(R.drawable.round_corner);
				
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
				Date currentdate1 = new Date(System.currentTimeMillis());
				String timestr1 = format1.format(currentdate1);
				String date = "";
				Calendar calendar = Calendar.getInstance();  
			    calendar.setTime(new Date(System.currentTimeMillis()));  
				int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);  
				if(dayIndex == 1){
					date = timestr1 + "~" + getDateStr(timestr1.split(" ")[0] , 6, true);
				}else if(dayIndex == 2){
					date = getDateStr(timestr1 , 1, false) + "~" + getDateStr(timestr1 , 5, true);
				}else if(dayIndex == 3){
					date = getDateStr(timestr1 , 2, false) + "~" + getDateStr(timestr1 , 4, true);
				}else if(dayIndex == 4){
					date = getDateStr(timestr1 , 3, false) + "~" + getDateStr(timestr1 , 3, true);
				}else if(dayIndex == 5){
					date = getDateStr(timestr1 , 4, false) + "~" + getDateStr(timestr1 , 2, true);
				}else if(dayIndex == 6){
					date = getDateStr(timestr1 , 5, false) + "~" + getDateStr(timestr1 , 1, true);
				}else if(dayIndex == 7){
					date = getDateStr(timestr1 , 6, false) + "~" + timestr1;
				}
				dateText.setTextSize(16);
				dateText.setText(date);
				getBarData();
			}
			
		});
		month.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(barMode == 2)
					return;
				barMode = 2;
				week.setBackgroundResource(R.drawable.round_corner);
				month.setBackgroundResource(R.drawable.round_corner_selected);
				year.setBackgroundResource(R.drawable.round_corner);
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
				Date currentdate1 = new Date(System.currentTimeMillis());
				String timestr1 = format1.format(currentdate1);
				dateText.setTextSize(20);
				dateText.setText(timestr1);
				getBarData();
			}
			
		});
		year.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(barMode == 3)
					return;
				barMode = 3;
				week.setBackgroundResource(R.drawable.round_corner);
				month.setBackgroundResource(R.drawable.round_corner);
				year.setBackgroundResource(R.drawable.round_corner_selected);
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy");
				Date currentdate1 = new Date(System.currentTimeMillis());
				String timestr1 = format1.format(currentdate1);
				dateText.setTextSize(20);
				dateText.setText(timestr1);
				getBarData();
			}
			
		});
		dateText = (TextView)findViewById(R.id.blood_pressure_record_date);
		arrawLeft = (ImageButton)findViewById(R.id.blood_pressure_record_date_minus);
		arrawRight = (ImageButton)findViewById(R.id.blood_pressure_record_date_plus);
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
		Date currentdate1 = new Date(System.currentTimeMillis());
		String timestr1 = format1.format(currentdate1);
		dateText.setText(timestr1);
		arrawLeft.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(barMode == 2){
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
				}else if(barMode == 3){
					dateText.setText(Integer.parseInt(dateText.getText().toString()) - 1 + "");
				}else{
					String date = dateText.getText().toString();
					String newDate = getDateStr(date.split("~")[0], 7, false) + "~" + getDateStr(date.split("~")[1], 7, false);
					dateText.setText(newDate);
				}
				
				getBarData();				
			}
			
		});
		arrawRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(barMode == 2){
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
				}else if(barMode == 3){
					dateText.setText(Integer.parseInt(dateText.getText().toString()) + 1 + "");
				}else{
					String date = dateText.getText().toString();
					String newDate = getDateStr(date.split("~")[0], 7, true) + "~" + getDateStr(date.split("~")[1], 7, true);
					dateText.setText(newDate);
				}
				
				getBarData();	
			}
			
		});
		reportView = (BloodPressureReportView)findViewById(R.id.blood_pressure_report_view);
		first = new First();
		startDate = getIntent().getExtras().getString("startDate");
		endDate = getIntent().getExtras().getString("endDate");
		userPin = getStringFromPreference(ConstHttpProp.USER_PIN);
		getData();	
	}

	private long dateToUtc(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date2 = sdf.parse(date);
			return date2.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public void getData(){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("userPIN", userPin);
			jsonObject.put("startTime", dateToUtc(startDate + " 00:00:00"));
			jsonObject.put("endTime", dateToUtc(endDate + " 23:59:59"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HttpSetting httpsetting=new HttpSetting();
		httpsetting.setFunctionId(ConstFuncId.BLOODPRESSUREGETHISTORYLIST);
		httpsetting.setRequestMethod("GET");
		httpsetting.setJsonParams(jsonObject);
		httpsetting.setListener(new HttpGroup.OnAllListener() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onEnd(HttpResponse response) {
				// TODO Auto-generated method stub
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
								arrList.add(objectproxy.getStringOrNull("date"));
								sysList.add(objectproxy.getIntOrNull("high"));
								diaList.add(objectproxy.getIntOrNull("low"));
								pulList.add(objectproxy.getIntOrNull("pulse"));
								
								showChart();
								showList();
								reportView.setList(sysList, diaList);
								reportView.invalidate();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else{
						Toast.makeText(BloodPressureReport.this, getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
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
	
	public void showList(){
		dateList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map;
		for(int i = 0; i < arrList.size(); i++){
			map = new HashMap<String, String>();
			map.put("date", arrList.get(i).toString().split(" ")[0] + "\n" + arrList.get(i).toString().split(" ")[1]);
			map.put("sys", sysList.get(i) + "");
			map.put("dia", diaList.get(i) + "");
			map.put("pul", pulList.get(i) + "");
			dateList.add(map);
		}
		adapter = new SimpleAdapter(BloodPressureReport.this,
				dateList,
				R.layout.blood_pressure_list_content,
				new String[]{"date", "sys", "dia", "pul"},
				new int[]{R.id.blood_pressure_list_date, R.id.blood_pressure_list_sys, R.id.blood_pressure_list_dia, R.id.blood_pressure_list_pul});
		ListView list = (ListView)findViewById(R.id.blool_pressure_history_list);
		list.setAdapter(adapter);
		list.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final AlertDialog alertdialog = (new AlertDialog.Builder(BloodPressureReport.this)).create();
				alertdialog.setMessage(getString(R.string.whether_delete_file));
				
				alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						deleteReport();
						alertdialog.dismiss();
					}
				});
				alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						alertdialog.cancel();
					}
				});
				alertdialog.show();
				deletePos = position;
				currentDate = ((TextView)view.findViewById(R.id.blood_pressure_list_date)).getText().toString();
				return false;
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	public void showChart(){
	    tiao = quChong(arrList).size();
	    if(tiao == 0){
	    	tiao = 1;
	    	de = new Date[tiao];
	    	daypa = new double[1];
		    daysa = new double[1];
		    dayda = new double[1];
		    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        String date = sDateFormat.format(new java.util.Date());
            arrList.add(date);
		    dayda[0] = 1000;
		    daysa[0] = 1000;
		    daypa[0] = 1000;
		     sysList.add(1000);
		     diaList.add(1000);
		     pulList.add(1000);
	    }
	    daypa = new double[tiao];
	    daysa = new double[tiao];
	    dayda = new double[tiao];
	    System.out.println(quChong(arrList));
	    Map <Object,Integer> map = new HashMap <Object,Integer>(); 
        for(Object o :arrList){ 
          map.put(o, map.get(o)==null?1:map.get(o)+1); 
        }
        int index = 0;
        int s = 0;
        int arr[] = new int[tiao];
        de = new Date[tiao];
        for(Object i:map.keySet()){
            System.out.println(i+"----->"+map.get(i));
            index = map.get(i); 
              arr[s] = index; 
              String te = i.toString();
              String tes = te.replaceAll("-", "");  
              String end = tes.substring(0, 8);
              double a = Double.parseDouble(end.substring(0, 4));
              int aa = (int) a;
              double b = Double.parseDouble(end.substring(4, 6));
              int bb = (int) b;
              double c = Double.parseDouble(end.substring(6, 8));
              int cc = (int) c;
              de[s] = new Date(aa-1900,bb-1,cc);
              s++;
        }
        Date swap = new Date();
        int swapArr;
        for(int i = 0; i < de.length; i++){
        	for(int j = de.length - 1; j > i; j--){
        		if(de[j].getTime() < de[j - 1].getTime()){
        			swap = de[j];
        			de[j] = de[j - 1];
        			de[j - 1] = swap;
        			swapArr = arr[j];
        			arr[j] = arr[j - 1];
        			arr[j - 1] = swapArr;
        		}
        	}
        }
        datess.add(de);
       
        /*int t;
        int j=arr.length;
        for(int i=0;i<j;i++) {
         
          if(j>=i) {
           t=arr[i];
           arr[i]=arr[--j];
           arr[j]=t;           
          }else{
           break;
          }
         
        }*/
  
    	int sa = 0,da =0,pa =0;
        List sal,dal,pal;
		
        for (int i = 0; i < arr.length; i++) {
        	int a = arr.length;
          	if(a==1){
   	    	sal = sysList.subList(0, arr[i]);
	    	dal = diaList.subList(0, arr[i]);
	    	pal = pulList.subList(0, arr[i]);
	    	sa = first.getAverage(sal);
	    	da = first.getAverage(dal);
	    	pa = first.getAverage(pal);
	    	daysa[i] = sa;
	    	dayda[i] = da;
	    	daypa[i] = pa;
        	}else{
        		int le = 0;
        		for (int k = 0; k < i; k++) {
					le += arr[k];
				}
        		sal = sysList.subList(le, le+arr[i]);
        		dal = diaList.subList(le, le+arr[i]);
        		pal = pulList.subList(le, le+arr[i]);
        		sa = first.getAverage(sal)/arr[i];
      	    	da = first.getAverage(dal)/arr[i];
      	    	pa = first.getAverage(pal)/arr[i];
      	    	daysa[i] = sa;
    	    	dayda[i] = da;
    	    	daypa[i] = pa;
        	}
		}
        
        if (ds == null)
            getDataset();
        if (render == null)
            getRenderer();
        
        if (gv == null) {
        	gv = ChartFactory.getTimeChartView(this, ds, render, "dd-MMM-yyyy");
            layout.addView(gv);

        } else {
        	layout.removeAllViews(); 
        	gv = ChartFactory.getTimeChartView(this, ds, render, "dd-MMM-yyyy");
        	layout.addView(gv, new LayoutParams(LayoutParams.FILL_PARENT,
	        LayoutParams.FILL_PARENT));   
     	}
        /*if (dataset == null)
       	 getDemoDataset();
        if (renderer == null)
       	 getDemoRenderer();

      if (dv == null) {
          dv = ChartFactory.getScatterChartView(this, dataset, renderer);
          dianchart.addView(dv);

    } else {

        //   绘制图形
   	 dianchart.removeAllViews();   //先remove再add可以实现统计图更新
   	   dv = ChartFactory.getScatterChartView(this, dataset, renderer);
   	   dianchart.addView(dv, new LayoutParams(LayoutParams.FILL_PARENT,
   	       LayoutParams.FILL_PARENT));
       }*/
	}
	 public static List quChong(List list){  
         List newlist=new ArrayList();  
         Iterator iter=list.iterator();  
          while(iter.hasNext()){  
            Object obj=iter.next();//定义一个Object变量去接收  
             if(!newlist.contains(obj)){//判断是否包含  
                newlist.add(obj);  
           }  
          }  
        return newlist;  
     }
	 private XYMultipleSeriesDataset getDataset() {
	        ds = new XYMultipleSeriesDataset();
	        Date[] xV = datess.get(0); 
	        for (int i = 0; i < titles.length; i++) {
	            // 新建一个系列（线条）
	            series = new TimeSeries(titles[i]);
	            int seriesLength = xV.length;
	            switch (i) {
	            case 0:
	            	  for (int k = 0;k < seriesLength;k++){
	            		
	                 series.add(xV[k],daysa[k]);
	                 System.out.println(daysa[k]+"sasasasas");
	            	  }
	                ds.addSeries(series);
	                break;
	            case 2:
	            	  for (int k = 0;k < seriesLength;k++){
	            		 
	 	                 series.add(xV[k],dayda[k]);
	 	            	  }
	            	   ds.addSeries(series);
	                break;
	            case 1:
	            	  for (int k = 0;k < seriesLength;k++){
	            		  series.add(xV[k],daypa[k]);
	 	            	  }
	            	   ds.addSeries(series);
	                break;
	    
	            default:
	            	     series.add(0,0);
	                     ds.addSeries(series);
	                break;
	            }

	        }
 	        return ds;

	    }
	 public XYMultipleSeriesRenderer getRenderer() {
	        render = new XYMultipleSeriesRenderer();
	        render.setAxisTitleTextSize(width / 30); // 设置坐标轴标题文本大小
	        render.setChartTitleTextSize(width / 30); // 设置图表标题文本大小
	   //     render.setChartTitle("Measurement of blood pressure data values");
	        render.setLabelsTextSize(width / 40); // 设置轴标签文本大小
	        render.setLegendTextSize(width / 30); // 设置图例文本大小
	        render.setMargins(new int[] {30, 30, width / 20, 30}); // 设置4边留白
	        render.setPanEnabled(true,false); // 设置x,y坐标轴不会因用户划动屏幕而移动
	        render.setMarginsColor(Color.argb(0, 0xff, 0, 0));// 设置4边留白透明
	        render.setBackgroundColor(Color.TRANSPARENT); // 设置背景色透明
	        render.setApplyBackgroundColor(true); // 使背景色生效
	        render.setXLabels(4);// 设置X轴显示12个点，根据setChartSettings的最大值和最小值自动计算点的间隔
	        render.setYLabels(12);// 设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
	        render.setXLabelsAlign(Align.CENTER);// 刻度线与刻度标注之间的相对位置关系
	        render.setYLabelsAlign(Align.LEFT);// 刻度线与刻度标注之间的相对位置关系
	        // render.setZoomButtonsVisible(true);// 是否显示放大缩小按钮
	        render.setShowGrid(true);// 是否显示网格
	        render.setGridColor(Color.WHITE);// 设置网格颜色
	        render.setAxesColor(Color.WHITE);// 设置X.y轴颜色
	        render.setXLabelsColor(Color.WHITE);
	        render.setYLabelsColor(0,Color.WHITE);
	        render.setLabelsColor(Color.WHITE);
	    //    render.setYLabelsColor(scale, color)(R.color.black);
	        render.setFitLegend(true);// 设置自动按比例缩放
	        render.setYAxisMax(200.0); // 设置Y轴最大值
	        render.setYAxisMin(40.0); // 设置Y轴最小值
	        // 设置x,y轴上的刻度的颜色
	      render.setLabelsColor(Color.WHITE);
	   //     render.setXTitle("Measuring time");
	   //     render.setYTitle("Measurement value");
//
	        for (int i = 0; i < titles.length; i++) {
	            xyRender = new XYSeriesRenderer();
	            xyRender.setPointStyle(PointStyle.CIRCLE);
	           xyRender.setColor(colors[i]);// 设置线图颜色
	           xyRender.setLineWidth(2.0f);
	            xyRender.setFillPoints(true);// 设置为实心点
	            render.addSeriesRenderer(xyRender);// 添加到render中
	        }
	        return render;

	    }
	 
	 public void deleteReport(){
		 JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("userPin", userPin);
				jsonObject.put("currentDate", currentDate);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			HttpSetting httpsetting=new HttpSetting();
			httpsetting.setFunctionId(ConstFuncId.BLOODPRESSUREDELETE);
			httpsetting.setJsonParams(jsonObject);
			httpsetting.setListener(new HttpGroup.OnAllListener() {

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onEnd(HttpResponse response) {
					// TODO Auto-generated method stub
					JSONObjectProxy json = response.getJSONObject();
					if(json == null)return;
					final int getcode = json.getIntOrNull("code");
					post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							if(getcode == 1){
								dateList.remove(deletePos);
								arrList.remove(deletePos);
								sysList.remove(deletePos);
								diaList.remove(deletePos);
								pulList.remove(deletePos);
								adapter.notifyDataSetChanged();
								Toast.makeText(BloodPressureReport.this, getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
							}else
								Toast.makeText(BloodPressureReport.this, getResources().getString(R.string.delete_failed), Toast.LENGTH_SHORT).show();
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
	 
	 
	 //柱状图
	 public void getBarData(){
		 getBarDate();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("userPIN", userPin);
				jsonObject.put("startTime", dateToUtc(startDate + " 00:00:00"));
				jsonObject.put("endTime", dateToUtc(endDate + " 23:59:59"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			HttpSetting httpsetting=new HttpSetting();
			httpsetting.setFunctionId(ConstFuncId.BLOODPRESSUREGETHISTORYLIST);
			httpsetting.setRequestMethod("GET");
			httpsetting.setJsonParams(jsonObject);
			httpsetting.setListener(new HttpGroup.OnAllListener() {

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onEnd(HttpResponse response) {
					// TODO Auto-generated method stub
					hasGotData = true;
					dateBarList.clear();
					sysBarList.clear();
					diaBarList.clear();
					
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
									dateBarList.add(objectproxy.getStringOrNull("date"));
									diaBarList.add(objectproxy.getIntOrNull("high"));
									sysBarList.add(objectproxy.getIntOrNull("low"));
									showBarChart();
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}else{
							Toast.makeText(BloodPressureReport.this, getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
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
	 private void getBarDate(){
		 barchart.removeAllViews();
		 if(barMode == 1){
			 String date = dateText.getText().toString();
			 startBarDate = date.split("~")[0];
			 endBarDate = date.split("~")[1];
		 }else if(barMode == 2){
			 String date = dateText.getText().toString(); 
			 int month = Integer.parseInt(date.split("-")[1]);
			 startBarDate = date + "-01";
			 if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
				 endBarDate = date + "-31";
			 }else if(month == 2){
				 if(Integer.parseInt(date.split("-")[0]) % 4 == 0){
					 endBarDate = date + "-29";
				 }else{
					 endBarDate = date + "-28";
				 }
			 }else{
				 endBarDate = date + "-30";
			 }
		 }else{
			 String date = dateText.getText().toString(); 
			 startBarDate = date + "-01-01";
			 endBarDate = date + "-12-31";
		 }
	 }
	 
	 public String getDateStr(String day,int dayAddNum, boolean plus) {  
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	        Date nowDate = null;  
	        try {  
	            nowDate = df.parse(day);  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        Date newDate2 = null;
	        if(plus){
	        	newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60 * 1000); 
	        }else{
	        	newDate2 = new Date(nowDate.getTime() - dayAddNum * 24 * 60 * 60 * 1000); 
	        } 
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");  
	        String dateOk = simpleDateFormat.format(newDate2);  
	        return dateOk;  
	    } 
	 
	 private void handleBarData(){
		 if(barMode == 1){
			 sysBar = new double[7];
			 diaBar = new double[7];
			 int num[] = new int[7];
			 int sys[] = new int[7];
			 int dia[] = new int[7];
			 String date[] = new String[]{startBarDate, 
					 getDateStr(startBarDate, 1, true), 
					 getDateStr(startBarDate, 2, true),
					 getDateStr(startBarDate, 3, true),
					 getDateStr(startBarDate, 4, true),
					 getDateStr(startBarDate, 5, true),
					 getDateStr(startBarDate, 6, true)};
			 for(int i = 0; i < dateBarList.size(); i++){
				for(int j = 0; j < 7; j++){
					if(date[j].equals(dateBarList.get(i).split(" ")[0])){
						num[j]++;
						sys[j] += sysBarList.get(i);
						dia[j] += diaBarList.get(i);
					}
				}
			 }
			 for(int i = 0; i < num.length; i++){
				if(num[i] != 0){
					sysBar[i] = sys[i] / num[i];
					diaBar[i] = dia[i] / num[i];
				}
			}
		 }else if(barMode == 2){
			 sysBar = new double[5];
			 diaBar = new double[5];
			int num[] = new int[5];
			int sys[] = new int[5];
			int dia[] = new int[5];
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance(); 
			try {
				calendar.setTime(format.parse(startBarDate));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);  
			int first = 0;
			if(dayIndex == 1){
				first = 7;
			}else if(dayIndex == 2){
				first = 1;
			}else if(dayIndex == 3){
				first = 2;
			}else if(dayIndex == 4){
				first = 3;
			}else if(dayIndex == 5){
				first = 4;
			}else if(dayIndex == 6){
				first = 5;
			}else if(dayIndex == 7){
				first = 6;
			}
			for(int i = 0; i < dateBarList.size(); i++){
				int day = Integer.parseInt(dateBarList.get(i).split(" ")[0].split("-")[2]);
				int index = (day + first - 2) / 7;
				num[index]++;
				sys[index] += sysBarList.get(i);
				dia[index] += diaBarList.get(i);
			}
			for(int i = 0; i < num.length; i++){
				if(num[i] != 0){
					sysBar[i] = sys[i] / num[i];
					diaBar[i] = dia[i] / num[i];
				}
			}
		 }else{
			 sysBar = new double[12];
			 diaBar = new double[12];
			 int num[] = new int[12];
			 int sys[] = new int[12];
			 int dia[] = new int[12];
			 for(int i = 0; i < dateBarList.size(); i++){
				int month = Integer.parseInt(dateBarList.get(i).split(" ")[0].split("-")[1]);
				for(int j = 0; j < 12; j++){
					if(month == j + 1){
						num[j]++;
						sys[j] += sysBarList.get(i);
						dia[j] += diaBarList.get(i);
					}
				}
			 }
			 for(int i = 0; i < num.length; i++){
				 if(num[i] != 0){
					 sysBar[i] = sys[i] / num[i];
					 diaBar[i] = dia[i] / num[i];
				 }
			 }
			
		 }
	 }
	 private void showBarChart(){
		 handleBarData();
		 XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
			//多个序列的数据集
			XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			//构建数据集以及渲染
			int xLable[];
			if(barMode == 1){
				xLable = new int[]{1, 2, 3, 4, 5, 6, 7};
			}else if(barMode == 2){
				xLable = new int[]{1, 2, 3, 4, 5};
			}else{
				xLable = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
			}
			for (int i = 0; i < barTitles.length; i++) {
				
				XYSeries series = new XYSeries(barTitles[i]);
				
				for (int j=0;j<sysBar.length;j++) {
					if(i == 0)
						series.add(xLable[j],diaBar[j]);
					else
						series.add(xLable[j],sysBar[j]);	
				}
				dataset.addSeries(series);
				XYSeriesRenderer xyRenderer = new XYSeriesRenderer();
				if(i == 0){
					xyRenderer.setColor(Color.RED);
				}else{
					xyRenderer.setColor(Color.rgb(82, 186, 44));
				}
				
				// 设置点的样式 //
				xyRenderer.setPointStyle(PointStyle.SQUARE);
				// 将要绘制的点添加到坐标绘制中
				xyRenderer.setChartValuesTextAlign(Align.CENTER);
				xyRenderer.setChartValuesTextSize(width / 30);
				xyRenderer.setDisplayChartValues(true);

				renderer.addSeriesRenderer(xyRenderer);
			}
			//设置x轴标签数
			if(barMode == 1){
				renderer.setXLabels(0);
				renderer.setPanLimits(new double[]{0, 8, 0, 0});
				renderer.addTextLabel(1, getResources().getString(R.string.sunday));
				renderer.addTextLabel(2, getResources().getString(R.string.monday));
				renderer.addTextLabel(3, getResources().getString(R.string.tuesday));
				renderer.addTextLabel(4, getResources().getString(R.string.wednesday));
				renderer.addTextLabel(5, getResources().getString(R.string.thursday));
				renderer.addTextLabel(6, getResources().getString(R.string.friday));
				renderer.addTextLabel(7, getResources().getString(R.string.saturday));
			}else if(barMode == 2){
				renderer.setXLabels(0);
				renderer.setPanLimits(new double[]{0, 6, 0, 0});
				renderer.addTextLabel(1, getResources().getString(R.string.first_week));
				renderer.addTextLabel(2, getResources().getString(R.string.second_week));
				renderer.addTextLabel(3, getResources().getString(R.string.third_week));
				renderer.addTextLabel(4, getResources().getString(R.string.fourth_week));
				renderer.addTextLabel(5, getResources().getString(R.string.fifth_week));
			}else{
				renderer.setXLabels(0);
				renderer.setPanLimits(new double[]{0, 13, 0, 0});
				renderer.addTextLabel(1, getResources().getString(R.string.january));
				renderer.addTextLabel(2, getResources().getString(R.string.february));
				renderer.addTextLabel(3, getResources().getString(R.string.march));
				renderer.addTextLabel(4, getResources().getString(R.string.april));
				renderer.addTextLabel(5, getResources().getString(R.string.may));
				renderer.addTextLabel(6, getResources().getString(R.string.june));
				renderer.addTextLabel(7, getResources().getString(R.string.july));
				renderer.addTextLabel(8, getResources().getString(R.string.august));
				renderer.addTextLabel(9, getResources().getString(R.string.september));
				renderer.addTextLabel(10, getResources().getString(R.string.october));
				renderer.addTextLabel(11, getResources().getString(R.string.november));
				renderer.addTextLabel(12, getResources().getString(R.string.december));
			}
			//设置轴的颜色
			renderer.setAxesColor(Color.WHITE);
			
			renderer.setYAxisMin(0);
			renderer.setYAxisMax(getMaxValue(diaBar));
			//设置x轴和y轴的标签对齐方式
			renderer.setXLabelsAlign(Align.CENTER);
			renderer.setYLabelsAlign(Align.LEFT);
			// 设置现实网格
			renderer.setShowGrid(false); 
			renderer.setZoomEnabled(false, false);
			renderer.setPanEnabled(true, false);
			// 设置条形图之间的距离
			renderer.setBarSpacing(0.5);
			//设置x轴和y轴标签的颜色
	      
			//设置图标的标题
			renderer.setLabelsColor(Color.WHITE);
			
			renderer.setAxisTitleTextSize(width / 25);

			//设置图例的字体大小
			renderer.setLegendTextSize(width / 20);
			renderer.setLegendHeight(width / 30);
			renderer.setLabelsTextSize(width / 30);
			renderer.setFitLegend(true);
			
			renderer.setMargins(new int[] {30, 30, width / 15, 30});// 设置4边留白
			renderer.setMarginsColor(Color.argb(0, 0xff, 0, 0));// 设置4边留白透明

			barchart.removeAllViews();	
			GraphicalView mChartView = ChartFactory.getBarChartView(getApplicationContext(),dataset, renderer, Type.STACKED);	
			barchart.addView(mChartView);	
	 }
	 private double getMaxValue(double value[]){
		 double max = 0;
		 for(int i = 0; i < value.length; i++){
			 if(max < value[i])
				 max = value[i];
		 }
		 return max + 30;
	 }
}
