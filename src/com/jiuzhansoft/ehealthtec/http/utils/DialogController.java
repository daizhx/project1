package com.jiuzhansoft.ehealthtec.http.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

public class DialogController implements DialogInterface.OnClickListener, DialogInterface.OnKeyListener{


	protected AlertDialog alertDialog;
	protected AlertDialog.Builder builder;
	private boolean canBack;
	private Context context;
	private CharSequence initMessage;
	private CharSequence initNegativeButton;
	private CharSequence initNeutralButton;
	private CharSequence initPositiveButton;
	private CharSequence initTitle;
	private View view;

	public DialogController()
	{
		canBack = false;
	}

	public void init(Context context1)
	{
		context = context1;
		builder = new AlertDialog.Builder(context1);
		initContent();
		initButton();
	}

	protected void initButton()
	{
		if (!TextUtils.isEmpty(initPositiveButton))
		{
			builder.setPositiveButton(initPositiveButton, this);
		}
		if (!TextUtils.isEmpty(initNeutralButton))
		{
			builder.setNeutralButton(initNeutralButton, this);
		}
		if (!TextUtils.isEmpty(initNegativeButton))
		{
			builder.setNegativeButton(initNegativeButton, this);
		}
	}

	protected void initContent()
	{
		if (TextUtils.isEmpty(initTitle))
		{
			builder.setTitle("æ√’πø∆ºº");
		} else
		{
			builder.setTitle(initTitle);
		}
		if (!TextUtils.isEmpty(initMessage))
		{
			builder.setMessage(initMessage);
		}
		if (view != null)
		{
			builder.setView(view);
		}
		builder.setOnKeyListener(this);
	}

	public boolean isCanBack()
	{
		return canBack;
	}

	public void onClick(DialogInterface dialoginterface, int i)
	{
	}

	public boolean onKey(DialogInterface dialoginterface, int i, KeyEvent keyevent)
	{
		boolean flag;
		if (!isCanBack() && 4 == i)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public void setCanBack(boolean flag)
	{
		canBack = flag;
	}

	public void setMessage(CharSequence charsequence)
	{
		if (alertDialog != null)
			alertDialog.setMessage(charsequence);
		else
		if (builder != null)
			builder.setMessage(charsequence);
		else
			initMessage = charsequence;
	}

	public void setNegativeButton(CharSequence charsequence)
	{
		if (alertDialog != null)
		{
			if (TextUtils.isEmpty(charsequence))
				alertDialog.getButton(-1).setVisibility(View.GONE);
			else
				alertDialog.setButton(-1, charsequence, this);
		} 
		else if (builder != null)
		{
			builder.setNegativeButton(initNegativeButton, this);
		} 
		else
		{
			initNegativeButton = charsequence;
		}
	}

	public void setNeutralButton(CharSequence charsequence)
	{
		if (alertDialog != null)
		{
			if (TextUtils.isEmpty(charsequence))
				alertDialog.getButton(-1).setVisibility(View.GONE);
			else
				alertDialog.setButton(-1, charsequence, this);
		} 
		else if (builder != null)
		{
			builder.setNeutralButton(initNeutralButton, this);
		} 
		else
		{
			initNeutralButton = charsequence;
		}
	}

	public void setPositiveButton(CharSequence charsequence)
	{
		if (alertDialog != null)
		{
			if (TextUtils.isEmpty(charsequence))
				alertDialog.getButton(-1).setVisibility(View.GONE);
			else
				alertDialog.setButton(-1, charsequence, this);
		} 
		else if (builder != null)
			builder.setPositiveButton(charsequence, this);
		else
			initPositiveButton = charsequence;
	}

	public void setTitle(CharSequence charsequence)
	{
		if (alertDialog != null)
			alertDialog.setTitle(charsequence);
		else 
		if (builder != null)
			builder.setTitle(charsequence);
		else
			initTitle = charsequence;
	}

	public void setView(View view1)
	{
		if (alertDialog != null)
			alertDialog.setView(view1);
		else if (builder != null)
			builder.setView(view1);
		else
			view = view1;
	}

	public void show()
	{
		if (alertDialog != null)
			alertDialog.show();
		else if (builder != null)
		{
			alertDialog = builder.show();
		} 
		else
		{
			throw new RuntimeException("builder is null, need init this controller");
		}
	}

}
