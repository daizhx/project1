package com.jiuzhansoft.ehealthtec.lens.naevus;
import java.util.List;

import com.jiuzhansoft.ehealthtec.R;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class SaveFileActivity extends Activity
{
	ExpandableListView expandableListView;

	SaveFileExpandListAdapter treeViewAdapter;
	
	public String[] groups;

	public String[][] child = new String[][]{{""}, {""}};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.naevus_save_file);
		
		setTitle(R.string.storage_file);
		
		groups = new String[]{getResources().getString(R.string.create_a_new_archive), getResources().getString(R.string.save_to_the_exising_archive)}; 
		
		treeViewAdapter = new SaveFileExpandListAdapter(this);
		expandableListView = (ExpandableListView) this
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
		expandableListView.setAdapter(treeViewAdapter);
		expandableListView.expandGroup(0);
		expandableListView.setChildDivider(new ColorDrawable(Color.TRANSPARENT));
	}
}