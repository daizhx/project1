package com.jiuzhansoft.ehealthtec.healthclub;

import android.app.ListActivity;
import android.app.SearchManager;
import android.app.SearchManager.OnCancelListener;
import android.app.SearchManager.OnDismissListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jiuzhansoft.ehealthtec.activity.BaseActivity;

public class SearchActivity extends ListActivity{

	private SearchManager mSearchManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		onSearchRequested();
		mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		
		mSearchManager.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		Intent intent = getIntent();
		handleIntent(intent);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		// TODO Auto-generated method stub
		if(Intent.ACTION_SEARCH.equals(intent.getAction())){
			String query = intent.getStringExtra(SearchManager.QUERY);
			doMySearch(query);
		}
	}

	private void doMySearch(String query) {
		// TODO Auto-generated method stub
		
	}
}
