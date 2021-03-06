package com.jiuzhansoft.ehealthtec.http.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Vector;

import android.os.Process;


/**
 * 线程池，包含一些线程对象的普通对象
 * @author Administrator
 *
 */
public class ThreadPool {

	protected boolean hasIdleThread;
	protected int initPoolSize;
	protected boolean initialized;
	protected int maxPoolSize;
	protected PriorityQueue<IPriority> queue;
	protected Vector<PooledThread> threads;

	public ThreadPool(int i, int j) {
		threads = new Vector<PooledThread>();
		initialized = false;
		hasIdleThread = false;
		queue = new PriorityQueue<IPriority>();
		maxPoolSize = i;
		initPoolSize = j;
	}

	/*return a abstract class?*/
	private synchronized IPriority pollTasks() {
		return (IPriority) queue.poll();
	}

	/**
	 * 
	 * @return PooledThread or null
	 */
	public PooledThread getIdleThread() {
		Iterator<PooledThread> iterator = null;
		PooledThread retThread = null;
		while (true) {
			iterator = threads.iterator();
			while (iterator.hasNext())// 线程池不为空时
			{
				PooledThread thread = (PooledThread) iterator.next();
				if (!thread.isRunning()) {
					retThread = thread;
					//System.out.println("get idle thread in getIdleThread1");
					return retThread;
				}
			}
			//没有闲置的线程时，并且线程池未满时，new一个
			if (getPoolSize() < maxPoolSize) {
				PooledThread pooledthread = new PooledThread(this);
				pooledthread.start();
				threads.add(pooledthread);
				retThread = pooledthread;
				//System.out.println("get idle thread in getIdleThread2");
				return retThread;
			}
			
			//等待其他线程闲置
			if (!waitForIdleThread()) {
				retThread = null;
				//System.out.println("can not get idle thread in getIdleThread1");
				break;
			}
		}
		return retThread;
	}

	public int getPoolSize() {
		return threads.size();
	}

	/**
	 * new initPoolSize个PooledThread线程到threads队列中，new一个控制线程，不断将任务queue中的任务分发到pooledthread中
	 * 去执行，没有任务时等待。
	 */
	public void init() {
		initialized = true;
		int i = 0;
		while (true) {
			if (i >= initPoolSize) {
				(new Thread(new Runnable() {
					@Override
					public void run() {
						Process.setThreadPriority(19);
						do {
							//TODO 如果出现返回null的情况，没有处理
							PooledThread pooledthread = getIdleThread();
							Collection<?> collection = (Collection<?>) pollTasks();
							if (collection != null) {
								pooledthread.putTasks(collection);
								pooledthread.startTasks();
								continue;
							}
							synchronized (queue) {
								try {
									queue.wait();
									//System.out.println("have new data incoming");
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						} while (true);
					}

				})).start();
				return;
			}
			PooledThread pooledthread = new PooledThread(this);
			pooledthread.start();
			threads.add(pooledthread);
			i++;
		}
	}

	/**
	 * pooledThread通知线程池自己已闲置
	 */
	protected void notifyForIdleThread() {
		synchronized (this) {
			hasIdleThread = true;
			//System.out.println("have idle thread-----");
			notify();
		}
	}

	/**
	 * 向任务queue中添加runnable任务
	 * @param runnable
	 * @param priority
	 */
	public void offerTask(Runnable runnable, int priority) {
		PriorityCollection collection = new PriorityCollection(priority);
		collection.add(runnable);
		offerTasks(collection);
	}

	public void offerTasks(IPriority ipriority) {
		queue.offer(ipriority);
		synchronized (queue) {
			//System.out.println("add data to queue-----");
			queue.notify();
		}
	}

	public void setMaxPoolSize(int i) {
		maxPoolSize = i;
		int j = getPoolSize();
		if (i < j)
			setPoolSize(i);
	}

	public void setPoolSize(int i) {
		if (!initialized) {
			initPoolSize = i;
			return;
		}
		int j = getPoolSize();
		if (i > j)// 小添加
		{
			int k = getPoolSize();
			while ((k < i) && (k < maxPoolSize)) {
				PooledThread pooledthread = new PooledThread(this);
				pooledthread.start();
				threads.add(pooledthread);
				k++;
			}
		} else// 大删除
		{
			int l = getPoolSize();
			while (l > i) {
				((PooledThread) threads.remove(0)).kill();
				l--;
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	protected boolean waitForIdleThread() {
		boolean ret = false;
		hasIdleThread = false;
		while (true) {
			if (hasIdleThread) {
				ret = true;
				break;
			} else {
				if (getPoolSize() >= maxPoolSize) {
					try {
					synchronized(this) {
						wait();
						//System.out.println("get new idle thread in waitForIdleThread");
						ret = true;
						break;
					}
					} catch (InterruptedException e) {
						ret = false;
						e.printStackTrace();
						break;
					}

				} else {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}


}
