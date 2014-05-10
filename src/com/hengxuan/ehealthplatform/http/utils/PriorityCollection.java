package com.hengxuan.ehealthplatform.http.utils;

import java.util.ArrayList;
import java.util.Collection;




public class PriorityCollection extends ArrayList<Object> implements Comparable<Object>, IPriority{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7042033084995648055L;
	private int priority;

	public PriorityCollection(int i)
	{
		priority = i;
	}

	public PriorityCollection(int i, int j)
	{
		super(i);
		priority = j;
	}

	public PriorityCollection(Collection<?> collection, int i)
	{
		super(collection);
		priority = i;
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return priority;
	}

	@Override
	public int compareTo(Object obj) {
		IPriority ipriority = (IPriority)obj;
		int i = getPriority();
		int j = ipriority.getPriority();
		int k;
		if (i > j)
		{
			k = 1;
		}
		else if(i < j)
		{
			k = -1;
		}
		else
		{
			k = 0;
		}
		return k;
	}


}
