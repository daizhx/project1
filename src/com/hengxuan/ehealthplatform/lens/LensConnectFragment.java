package com.hengxuan.ehealthplatform.lens;

import com.hengxuan.ehealthplatform.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LensConnectFragment extends Fragment {
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.text_view, null, false);
		TextView tv = (TextView)view.findViewById(R.id.root);
		tv.setText(R.string.lens_connecting);
		return view;
	}
}
