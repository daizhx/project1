package com.jiuzhansoft.ehealthtec.http.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jiuzhansoft.ehealthtec.config.Configuration;

import android.os.Process;


public class PooledThread extends Thread {

	private static ThreadPool sPool;
	protected boolean killed;
	protected boolean paused;
	private ThreadPool pool;
	protected boolean running;
	protected boolean stopped;
	protected List<Runnable> tasks;

	static {
		int i = Integer.parseInt(Configuration.getProperty("maxPoolSize"));
		int j = Integer.parseInt(Configuration.getProperty("initPoolSize"));
		sPool = new ThreadPool(i, j);
		sPool.init();
	}

	public PooledThread(ThreadPool threadpool) {
		tasks = new ArrayList<Runnable>();
		running = false;
		stopped = false;
		paused = false;
		killed = false;
		pool = threadpool;
	}

	public static ThreadPool getThreadPool() {
		return sPool;
	}

	public boolean isRunning() {
		return running;
	}

	public void kill() {
		if (!running)
			interrupt();
		else
			killed = true;
	}

	public void killSync() {
		kill();
		do {
			if (!isAlive())
				return;
			try {
				sleep(5L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	public void pauseTasks() {
		paused = true;
	}

	public void pauseTasksSync() {
		pauseTasks();
		do {
			if (!isRunning())
				return;
			try {
				sleep(5L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (true);
	}

	protected Runnable popTask() {
		Runnable runnable;
		if (tasks.size() > 0)
			runnable = (Runnable) tasks.remove(0);
		else
			runnable = null;
		return runnable;
	}

	public void putTask(Runnable runnable) {
		tasks.add(runnable);
	}

	public void putTasks(Collection collection) {
		tasks.addAll(collection);
	}

	public void run() {
		Runnable runnable;
		Process.setThreadPriority(19);
		while (!killed) {
			while (running) {
				if (tasks.size() > 0) {
					runnable = popTask();
					if (runnable != null) {
						runnable.run();
						if (stopped) {
							stopped = false;
							if (tasks.size() > 0) {// tasksize >0
								tasks.clear();
								String s = String.valueOf(Thread
										.currentThread().getId());
								String s1 = (new StringBuilder(s)).append(
										": Tasks are stopped").toString();
								//System.out.println(s1);

							}
							running = false;
						}
						if (paused) {
							paused = false;
							if (tasks.size() > 0) {
								PrintStream printstream1 = System.out;
								String s2 = String.valueOf(Thread
										.currentThread().getId());
								String s3 = (new StringBuilder(s2)).append(
										": Tasks are paused").toString();
								//System.out.println(s3);
							}
							running = false;
						}
					}
				} else {
					running = false;
					pool.notifyForIdleThread();
				}
			}
			synchronized (this) {
				try {
					//System.out
							//.println("wait new task incoming in pooledThread.run");
					wait();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public synchronized void startTasks() {
		running = true;
		//System.out.println("inform thread to continue to run in startTasks");
		notify();
	}

	public void stopTasks() {
		stopped = true;
	}

	public void stopTasksSync() {
		stopTasks();
		do {
			if (!isRunning())
				return;
			try {
				sleep(5L);
			} catch (InterruptedException interruptedexception) {
			}
		} while (true);
	}


}
