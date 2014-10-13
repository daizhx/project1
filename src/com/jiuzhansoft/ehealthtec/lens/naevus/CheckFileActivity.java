package com.jiuzhansoft.ehealthtec.lens.naevus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jiuzhansoft.ehealthtec.R;


import android.R.color;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class CheckFileActivity extends Activity
{
	ExpandableListView expandableListView;

	CheckFileExpandListAdapter treeViewAdapter;

	private List<String> firstName = new ArrayList<String>();
	
	String path = Environment.getExternalStorageDirectory()
	.toString()
	+ File.separator
	+ "dxlphoto";
	
	File[] files = new File(path).listFiles();
	
	public String[] groups;

	public String[][] child;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.naevus_check_file);
		setTitle(R.string.file);
		
		for(int i = 0; i < files.length; i++){
			String name = files[i].getName();
			if(name.split("_").length != 3)
				continue;
			if(name.split("_")[1].length() != 19)
				continue;
			if(!name.split("_")[2].equals("o.png"))
				continue;
			String first = name.split("_")[0];
			//防止重复添加相同元素到list中
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
			//
		}
		groups = new String[firstName.size()];
		child = new String[firstName.size()][1];
		for(int i = 0; i < firstName.size(); i++){
			groups[i] = firstName.get(i);
			child[i][0] = "";
		}
		
		treeViewAdapter = new CheckFileExpandListAdapter(this, firstName);
		expandableListView = (ExpandableListView) this
		.findViewById(R.id.naevus_check_file_expandList);

		List<CheckFileExpandListAdapter.TreeNode> treeNode = treeViewAdapter.GetTreeNode();
		for (int i = 0; i < groups.length; i++)
		{
			CheckFileExpandListAdapter.TreeNode node = new CheckFileExpandListAdapter.TreeNode();
			node.parent = groups[i];
			for (int ii = 0; ii < child[i].length; ii++)
			{
				node.childs.add(child[i][ii]);
			}
			treeNode.add(node);
		}

		treeViewAdapter.UpdateTreeNode(treeNode);
		expandableListView.setAdapter(treeViewAdapter);
		expandableListView.setChildDivider(new ColorDrawable(Color.TRANSPARENT));
	}
}