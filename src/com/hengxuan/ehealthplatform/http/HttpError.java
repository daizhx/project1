package com.hengxuan.ehealthplatform.http;

import com.hengxuan.ehealthplatform.http.constant.ConstHttpProp;


public class HttpError {
	private int errorCode;
	private Throwable exception;
	private HttpResponse httpResponse;
	private int jsonCode;
	private String message;
	private boolean noRetry;
	private int responseCode;
	private int times;

	public HttpError() {
		this.errorCode = ConstHttpProp.EXCEPTION;
	}

	public HttpError(Throwable throwable) {
		errorCode = ConstHttpProp.EXCEPTION;
		exception = throwable;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorCodeStr() {
		String errorCodeStr = null;
		switch (this.errorCode) {

		case ConstHttpProp.EXCEPTION:
			errorCodeStr = "EXCEPTION";
			break;
		case ConstHttpProp.TIME_OUT:
			errorCodeStr = "TIME_OUT";
			break;
		case ConstHttpProp.RESPONSE_CODE:
			errorCodeStr = "RESPONSE_CODE";
			break;
		case ConstHttpProp.JSON_CODE:
			errorCodeStr = "JSON_CODE";
			break;
		default:
			errorCodeStr = "UNKNOWN";
			break;
		}
		return errorCodeStr;
	}

	public Throwable getException() {
		return exception;
	}

	public HttpResponse getHttpResponse() {
		return httpResponse;
	}

	public int getJsonCode() {
		return jsonCode;
	}

	public String getMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public int getTimes() {
		return times;
	}

	public boolean isNoRetry() {
		return noRetry;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setException(Throwable paramThrowable) {
		this.exception = paramThrowable;
	}

	public void setHttpResponse(HttpResponse paramHttpResponse) {
		this.httpResponse = paramHttpResponse;
	}

	public void setJsonCode(int paramInt) {
		this.jsonCode = paramInt;
	}

	public void setMessage(String paramString) {
		this.message = paramString;
	}

	public void setNoRetry(boolean paramBoolean) {
		this.noRetry = paramBoolean;
	}

	public void setResponseCode(int paramInt) {
		this.responseCode = paramInt;
	}

	public void setTimes(int paramInt) {
		this.times = paramInt;
	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder("HttpError [errorCode=");
		
		stringbuilder.append(getErrorCodeStr()).append(
				", exception=").append(exception)
				.append(", jsonCode=").append(jsonCode)
				.append(", message=").append(message)
				.append(", responseCode=").append(responseCode)
				.append(", time=").append(times)
				.append("]");
		return stringbuilder.toString();
	}
}
