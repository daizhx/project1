package com.jiuzhansoft.ehealthtec.utils;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import com.jiuzhansoft.ehealthtec.utils.AsynImageLoader.Task;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.widget.ImageView;

public class MyAsynImageLoader {
	private Bitmap mPlaceHolderBitmap;
	private Context context;

	public MyAsynImageLoader(Context c) {
		// TODO Auto-generated constructor stub
		context = c;
	}

	// load bitmap for imageview asyn...
	public void loadBitmap(int resId, ImageView imageView) {
		if (cancelPotencialWork(resId, imageView)) {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			final AsyncDrawable aysnDrawable = new AsyncDrawable(
					context.getResources(), mPlaceHolderBitmap, task);
			imageView.setImageDrawable(aysnDrawable);
			task.execute(resId);
		}
	}

	public void loadBitmap(String file, ImageView imageView,int w, int h) {
		if (cancelPotencialWork(file, imageView)) {
			final SBitmapLoadTask task = new SBitmapLoadTask(imageView, w, h);
			final AsyncFileDrawable asyncFileDrawable = new AsyncFileDrawable(
					context.getResources(), null, task);
			imageView.setImageDrawable(asyncFileDrawable);
			task.execute(file);
		}
	}

	private boolean cancelPotencialWork(int data, ImageView imageView) {
		// TODO Auto-generated method stub
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkTask(imageView);
		if (bitmapWorkerTask != null) {
			final int bitmapData = bitmapWorkerTask.data;
			if (bitmapData == 0 || bitmapData != data) {
				bitmapWorkerTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	private boolean cancelPotencialWork(String path, ImageView imageView) {
		final SBitmapLoadTask bitmapLoadTask = getSBitmapLoadTask(imageView);
		if (bitmapLoadTask != null) {
			final String file = bitmapLoadTask.path;
			if (file == null || !file.equals(path)) {
				bitmapLoadTask.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	private static SBitmapLoadTask getSBitmapLoadTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncFileDrawable) {
				final AsyncFileDrawable asyncFileDrawable = (AsyncFileDrawable) drawable;
				return asyncFileDrawable.getBitmapLoadTask();
			}
		}

		return null;
	}

	class AsyncDrawable extends BitmapDrawable {
		private WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
					bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask() {
			return bitmapWorkerTaskReference.get();
		}
	}

	// 从file中异步加载的bitmap
	class AsyncFileDrawable extends BitmapDrawable {
		private WeakReference<SBitmapLoadTask> bitmapAsyncTaskReference;

		public AsyncFileDrawable(Resources res, Bitmap bitmap,
				SBitmapLoadTask bitmapLoadTask) {
			super(res, bitmap);
			bitmapAsyncTaskReference = new WeakReference<SBitmapLoadTask>(
					bitmapLoadTask);
		}

		public SBitmapLoadTask getBitmapLoadTask() {
			return bitmapAsyncTaskReference.get();
		}
	}

	// 图片加载异步任务
	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private int data = 0;

		public BitmapWorkerTask(ImageView imageView) {
			// use a WeakReference to ensure the imageview can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			data = params[0];
			if (data != 0) {
				return decodeSampledBitmapFromResource(context.getResources(),
						data, 540, 200);
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// TODO Auto-generated method stub
			if (isCancelled()) {
				bitmap = null;
			}
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkTask(imageView);
				if (imageView != null && bitmapWorkerTask == this) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}

	}

	// 从filepath加载
	class SBitmapLoadTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private String path;
		private int width, height;

		public SBitmapLoadTask(ImageView imageView,int w, int h) {
			// use a WeakReference to ensure the imageview can be garbage
			// collected
			imageViewReference = new WeakReference<ImageView>(imageView);
			width = w;
			height = h;
		}

		@Override
		protected Bitmap doInBackground(String... param) {
			// TODO Auto-generated method stub
			path = param[0];
			if (path != null) {
				return decodeSampleBitmapFromFile(context.getResources(), path,
						width, height);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Bitmap resultBitmap) {
			// TODO Auto-generated method stub
			if (isCancelled()) {
				resultBitmap = null;
			}

			if (imageViewReference != null && resultBitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final SBitmapLoadTask bitmapWorkerTask = getSBitmapLoadTask(imageView);
				if (imageView != null && bitmapWorkerTask == this) {
					imageView.setImageBitmap(resultBitmap);
				}
			}
		}
	}

	public static Bitmap decodeSampleBitmapFromFile(Resources res, String file,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(file, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(file, options);
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	// called by decodeSampledBitmapFromResource
	private static int calculateInSampleSize(Options options, int reqWidth,
			int reqHeight) {
		// TODO Auto-generated method stub
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = options.outHeight;
			final int halfWidth = options.outWidth;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}
}
