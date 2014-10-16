package com.jiuzhansoft.ehealthtec.http;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import android.content.Context;
import com.jiuzhansoft.ehealthtec.R;
import com.jiuzhansoft.ehealthtec.application.EHTApplication;
import com.jiuzhansoft.ehealthtec.http.json.JSONArrayPoxy;
import com.jiuzhansoft.ehealthtec.http.json.JSONObjectProxy;
import com.jiuzhansoft.ehealthtec.http.utils.ExceptionDrawable;
import com.jiuzhansoft.ehealthtec.log.Log;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class HttpResponse {
	private Bitmap bitmap;
	private int code;
	private Drawable drawable;
	private Map<String, List<String>> headerFields;
	private HttpURLConnection httpURLConnection;
	private byte[] inputData;
	private InputStream inputStream;
	private JSONObjectProxy jsonObject;
	private JSONArrayPoxy jsonArray;
	private long length;
	private File saveFile;
	private SoftReference<Bitmap> softReferenceBitmap;
	private SoftReference<Drawable> softReferenceDrawable;
	private SoftReference<byte[]> softReferenceInputData;
	private String string;
	private String type;
	public Context mContext;

	public HttpResponse(Context context) {
		mContext = context;
	}

	public HttpResponse(Context context, Drawable drawable) {
		mContext = context;
		this.drawable = drawable;
	}

	public HttpResponse(Context context, HttpURLConnection conn) {
		mContext = context;
		this.httpURLConnection = conn;
	}

	private void imageClean() {
		
		softReferenceInputData = new SoftReference(inputData);
		softReferenceBitmap = new SoftReference(bitmap);
		softReferenceDrawable = new SoftReference(drawable);
		inputData = null;
		bitmap = null;
		drawable = null;
	}

	public void clean() {
		httpURLConnection = null;
	}

	public Bitmap getBitmap() {
		
		Bitmap retBitmap;
		if (bitmap != null) {
			retBitmap = bitmap;
			imageClean();
		} else if (softReferenceBitmap == null)
			retBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
		else
			retBitmap = (Bitmap) softReferenceBitmap.get();
		return retBitmap;
	}

	public int getCode() {
		return code;
	}

	public Drawable getDrawable() {
		Drawable retDrawable;
		if (this.drawable != null) {
			retDrawable = drawable;
			imageClean();
		} else if (softReferenceDrawable == null) {
			String s = mContext.getString(R.string.no_image);
			retDrawable = new ExceptionDrawable(mContext, s);
		} else {
			retDrawable = (Drawable) softReferenceDrawable.get();
		}
		return retDrawable;
	}

	public String getHeaderField(String paramName) {
		String fieldValue;
		if (headerFields == null) {
			fieldValue = null;
		} else {
			List list = (List) headerFields.get(paramName);
			if (list == null || list.size() < 1)
				fieldValue = null;
			else
				fieldValue = (String) list.get(0);
		}
		return fieldValue;
	}

	public Map<String, List<String>> getHeaderFields() {
		return headerFields;
	}

	public byte[] getInputData() {
		return inputData;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public JSONObjectProxy getJSONObject() {
		return jsonObject;
	}
	public JSONArrayPoxy getJSONARRAY(){
		return jsonArray;
	}

	public long getLength() {
		return length;
	}

	public File getSaveFile() {
		return saveFile;
	}

	public String getString() {
		return string;
	}

	public Drawable getThumbDrawable(float f, float f1) {
		Bitmap bitmap1 = getBitmap();
		int i = bitmap1.getWidth();
		int j = bitmap1.getHeight();
		float f3;
		Drawable drawable1;
		if (i > j) {
			float f2 = i;
			f3 = f / f2;
		} else {
			float f4 = j;
			f3 = f1 / f4;
		}
		if (f3 < 1F) {
			int k = Math.round((float) i * f3);
			int l = Math.round((float) j * f3);
			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap1, k, l, false);
			setBitmap(bitmap2);
			bitmap1.recycle();
			setDrawable(new BitmapDrawable(bitmap2));
			drawable1 = getDrawable();
		} else {
			drawable1 = getDrawable();
		}
		return drawable1;
	}

	public String getType() {
		return type;
	}

	public void setBitmap(Bitmap bitmap) {
		if (bitmap == null)
			throw new RuntimeException("bitmap is null");
		this.bitmap = bitmap;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public void setHeaderFields(Map<String, List<String>> headerFields) {
		this.headerFields = headerFields;
	}

	public void setInputData(byte[] inputData) {
		this.inputData = inputData;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setJsonObject(JSONObjectProxy jsonObjectProxy) {
		this.jsonObject = jsonObjectProxy;
	}
	public void setJsonArray(JSONArrayPoxy ja){
		this.jsonArray = ja;
	}

	public void setLength(long len) {
		this.length = len;
	}

	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

	public void setString(String s) {
		this.string = s;
	}

	public void setType(String type) {
		this.type = type;
	}
}