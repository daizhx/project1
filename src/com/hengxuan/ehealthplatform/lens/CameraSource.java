package com.hengxuan.ehealthplatform.lens;

import java.net.HttpURLConnection;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface CameraSource {

	static final String LOG_TAG = "camera";
	
	/**
	 * Open the camera source for subsequent use via calls to capture().
	 * 
	 * @return true if the camera source was successfully opened.
	 */
	
	boolean open(HttpURLConnection httpURLconnection);
	
	/**
	 * Close the camera source. Calling close on a closed CameraSource is
	 * permitted but has no effect. The camera source may be reopened after
	 * being closed.
	 */
	
	void close();
	
	/**
	 * The width of the captured image.
	 * 
	 * @return the width of the capture in pixels
	 */
	
	int getWidth();
	
	/**
	 * The height of the captured image.
	 * 
	 * @return the height of the capture in pixels
	 */
	
	int getHeight();
	
	/**
	 * Attempts to render the current camera view onto the supplied canvas.
	 * The capture will be rendered into the rectangle (0,0,width,height).
	 * Outstanding transformations on the canvas may alter this.
	 * 
	 * @param canvas the canvas to which the captured pixel data will be written
	 * @return true iff a frame was successfully written to the canvas
	 */
	
	void capture(Canvas canvas,HttpURLConnection httpURLconnection);
	
	boolean saveImage(String savePath, String fileName);
	
	Bitmap getCaptureImage();
	
}

