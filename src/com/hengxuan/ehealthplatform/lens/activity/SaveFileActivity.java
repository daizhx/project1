package com.hengxuan.ehealthplatform.lens.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hengxuan.ehealthplatform.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SaveFileActivity extends Activity {
	ExpandableListView mExpandableListView;
	SaveFileExpandListAdapter treeViewAdapter;
	public String[] groups;
	public String[][] child = new String[][]{{""}, {""}};	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nevus_save_file);
		((TextView) findViewById(R.id.titleText2)).setText(getResources().getString(R.string.storage_file));
		((Button)findViewById(R.id.swich_btn)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		groups = new String[]{getResources().getString(R.string.create_a_new_archive), getResources().getString(R.string.save_to_the_exising_archive)}; 
		
		treeViewAdapter = new SaveFileExpandListAdapter(this);
		mExpandableListView = (ExpandableListView) this
		.findViewById(R.id.naevus_save_file_expandList);

		List<SaveFileExpandListAdapter.TreeNode> treeNode = treeViewAdapter.GetTreeNode();
		for (int i = 0; i < groups.length; i++)
		{
			SaveFileExpandListAdapter.TreeNode node = new SaveFileExpandListAdapter.TreeNode();
			node.parent = groups[i];
			for (int ii = 0; ii < child[i].length; ii++)
			{
				node.childs.add(child[i][ii]);
			}
			treeNode.add(node);
		}

		treeViewAdapter.UpdateTreeNode(treeNode);
		mExpandableListView.setAdapter(treeViewAdapter);
		mExpandableListView.expandGroup(0);
		mExpandableListView.setChildDivider(new ColorDrawable(Color.TRANSPARENT));
	}
	
	
	static class SaveFileExpandListAdapter extends BaseExpandableListAdapter
	{
		public static final int ItemHeight = 48;
		public static final int PaddingLeft = 36;

		private EditText edit;
		private Button save;
		private ListView list;
		
		private boolean hasSave;
		
		String path = Environment.getExternalStorageDirectory()
		.toString()
		+ File.separator
		+ "dxlphoto";
		
		File[] files = new File(path).listFiles();
		
		private List<String> firstName = new ArrayList<String>();
		
		private HashMap<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>();

		private List<TreeNode> treeNodes = new ArrayList<TreeNode>();

		private SaveFileActivity parentContext;
		
		private ProgressDialog progress;

		private LayoutInflater layoutInflater;
		
		private Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				progress.dismiss();
				Toast.makeText(parentContext, parentContext.getResources().getString(R.string.successfully_saved), Toast.LENGTH_SHORT).show();
				parentContext.finish();
			}
			
		};

		static public class TreeNode
		{
			Object parent;
			List<Object> childs = new ArrayList<Object>();
		}

		public SaveFileExpandListAdapter(Context view)
		{
			parentContext = (SaveFileActivity) view;
			
			for(int i = 0; i < files.length; i++){
				String name = files[i].getName();
				if(name.split("_").length != 3)
					continue;
				if(name.split("_")[1].length() != 19)
					continue;
				if(!name.split("_")[2].equals("o.png"))
					continue;
				String first = name.split("_")[0];
				boolean has = false;
				for(int j = 0; j < firstName.size(); j++){
					if(firstName.get(j).equals(first)){
						has = true;
						break;
					}
				}
				if(has == false){
					firstName.add(first);
				}
			}
			
		}

		public List<TreeNode> GetTreeNode()
		{
			return treeNodes;
		}

		public void UpdateTreeNode(List<TreeNode> nodes)
		{
			treeNodes = nodes;
		}

		public void RemoveAll()
		{
			treeNodes.clear();
		}

		public Object getChild(int groupPosition, int childPosition)
		{
			return treeNodes.get(groupPosition).childs.get(childPosition);
		}

		public int getChildrenCount(int groupPosition)
		{
			return treeNodes.get(groupPosition).childs.size();
		}
		
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent)
		{
			layoutInflater = (LayoutInflater) parentContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if(groupPosition == 0){
				convertView = layoutInflater.inflate(R.layout.expand_child_input, null);
				//convertView.setBackgroundColor(Color.rgb(10, 60, 100));
				edit = (EditText) convertView.findViewById(R.id.naevus_save_edittext);
				edit.setFocusable(true);
				edit.requestFocus();
				save = (Button) convertView.findViewById(R.id.naevus_save_button);
				save.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(hasSave == true){
							save.setEnabled(false);
							return;
						}
						
						String name = edit.getText().toString().trim();
						if(name.equals(""))
							return;
						saveBitmap(name);
						hasSave = true;
						save.setEnabled(false);
					}
					
				});
			}else{
				convertView = layoutInflater.inflate(R.layout.expand_child_list, null);
				//convertView.setBackgroundColor(Color.rgb(10, 60, 100));
				list = (ListView) convertView.findViewById(R.id.naevus_save_listview);
				list.setAdapter(new ArrayAdapter<String>(parentContext, android.R.layout.simple_expandable_list_item_1, firstName));
				list.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						if(hasSave == true){
							save.setEnabled(false);
							return;
						}
						saveBitmap(firstName.get(position));
						hasSave = true;
						list.setClickable(false);
						save.setEnabled(false);
					}
					
				});
			}
			return convertView;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent)
		{
			layoutInflater = (LayoutInflater) parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.expand_group, null);
			TextView textView = (TextView)convertView.findViewById(R.id.textGroup);
			textView.setText(getGroup(groupPosition).toString());
			textView.setPadding(0, 20, 0, 20);
			textView.setTextSize(25);
			textView.setTextColor(Color.WHITE);
			textView.setBackgroundColor(Color.rgb(0, 110, 180));
			return convertView;
		}

		public long getChildId(int groupPosition, int childPosition)
		{
			return childPosition;
		}

		public Object getGroup(int groupPosition)
		{
			return treeNodes.get(groupPosition).parent;
		}

		public int getGroupCount()
		{
			return treeNodes.size();
		}

		public long getGroupId(int groupPosition)
		{
			return groupPosition;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

		public boolean hasStableIds()
		{
			return true;
		}
		
		public void saveBitmap(final String name){
			progress = ProgressDialog.show(
					parentContext, parentContext.getResources().getString(R.string.file), 
					parentContext.getResources().getString(R.string.saving));
			new Thread(){
				public void run(){
					SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");     
					String date = sDateFormat.format(new java.util.Date()); 

					File fo = new File(path + File.separator + name +"_" + date + "_" + "o.png");
					
					try{
						fo.createNewFile();
					}catch(Exception e){
						
					}
					FileOutputStream fouso = null;
					try{
						fouso = new FileOutputStream(fo);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} 
					BitmapFactory.decodeFile(NevusView.getOriginalBitmap()).compress(Bitmap.CompressFormat.PNG, 100, fouso);
					try {
						fouso.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						fouso.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					File fa = new File(path + File.separator + name +"_" + date + "_" + "a.png");
					try{
						fo.createNewFile();
					}catch(Exception e){
						
					}
					FileOutputStream fousa = null;
					try{
						fousa = new FileOutputStream(fa);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					NevusView.getResizeBitmap().compress(Bitmap.CompressFormat.PNG, 100, fousa);
					try {
						fousa.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						fousa.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(0);
				}
			}.start();
		}
	}
}
