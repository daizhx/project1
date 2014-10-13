package com.jiuzhansoft.ehealthtec.http.json;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONObjectProxy extends JSONObject {

	private JSONObject jsonObject;

	public JSONObjectProxy() {
		this.jsonObject = new JSONObject();
	}

	public JSONObjectProxy(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JSONObject accumulate(String name, Object value)
			throws JSONException {
		return this.jsonObject.accumulate(name, value);
	}

	public boolean equals(Object obj) {
		return this.jsonObject.equals(obj);
	}

	public Object get(String name) throws JSONException {
		return this.jsonObject.get(name);
	}

	public boolean getBoolean(String name) throws JSONException {
		return this.jsonObject.getBoolean(name);
	}

	public Boolean getBooleanOrNull(String name) {
		Boolean retValue;
		try {
			retValue = Boolean.valueOf(this.jsonObject.getBoolean(name));
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public double getDouble(String name) throws JSONException {
		return this.jsonObject.getDouble(name);
	}

	public int getInt(String name) throws JSONException {
		return this.jsonObject.getInt(name);
	}

	public Integer getIntOrNull(String name) {
		Integer retValue;
		try {
			retValue = Integer.valueOf(this.jsonObject.getInt(name));
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public JSONArrayPoxy getJSONArray(String name) throws JSONException {
		JSONArray array = this.jsonObject.getJSONArray(name);
		return new JSONArrayPoxy(array);
	}

	public JSONArrayPoxy getJSONArrayOrNull(String name) {
		JSONArrayPoxy retValue;
		try {
			retValue = new JSONArrayPoxy(this.jsonObject.getJSONArray(name));
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public JSONObjectProxy getJSONObject(String name) throws JSONException {
		JSONObject json = this.jsonObject.getJSONObject(name);
		return new JSONObjectProxy(json);
	}

	public JSONObjectProxy getJSONObjectOrNull(String name) {
		JSONObjectProxy retValue;
		try {
			retValue = new JSONObjectProxy(this.jsonObject.getJSONObject(name));
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public long getLong(String name) throws JSONException {
		return this.jsonObject.getLong(name);
	}

	public Long getLongOrNull(String name) {
		Long retValue;
		try {
			retValue = Long.valueOf(this.jsonObject.getLong(name));
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public String getString(String name) throws JSONException {
		return this.jsonObject.getString(name);
	}

	public String getStringOrNull(String name) {
		String retValue;
		try {
			retValue = this.jsonObject.getString(name);
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public boolean has(String name) {
		return this.jsonObject.has(name);
	}

	public int hashCode() {
		return this.jsonObject.hashCode();
	}

	public boolean isNull(String name) {
		return this.jsonObject.isNull(name);
	}

	public Iterator keys() {
		return this.jsonObject.keys();
	}

	public int length() {
		return this.jsonObject.length();
	}

	public JSONArray names() {
		return this.jsonObject.names();
	}

	public Object opt(String name) {
		return this.jsonObject.opt(name);
	}

	public boolean optBoolean(String name) {
		return this.jsonObject.optBoolean(name);
	}

	public boolean optBoolean(String name, boolean fallback) {
		return this.jsonObject.optBoolean(name, fallback);
	}

	public double optDouble(String name) {
		return this.jsonObject.optDouble(name);
	}

	public double optDouble(String name, double fallback) {
		return this.jsonObject.optDouble(name, fallback);
	}

	public int optInt(String name) {
		return this.jsonObject.optInt(name);
	}

	public int optInt(String name, int fallback) {
		return this.jsonObject.optInt(name, fallback);
	}

	public JSONArray optJSONArray(String name) {
		return this.jsonObject.optJSONArray(name);
	}

	public JSONObject optJSONObject(String name) {
		return this.jsonObject.optJSONObject(name);
	}

	public long optLong(String name) {
		return this.jsonObject.optLong(name);
	}

	public long optLong(String name, long fallback) {
		return this.jsonObject.optLong(name, fallback);
	}

	public String optString(String name) {
		return this.jsonObject.optString(name);
	}

	public String optString(String name, String fallback) {
		return this.jsonObject.optString(name, fallback);
	}

	public JSONObject put(String name, double value) throws JSONException {
		return this.jsonObject.put(name, value);
	}

	public JSONObject put(String name, int value) throws JSONException {
		return this.jsonObject.put(name, value);
	}

	public JSONObject put(String name, long value) throws JSONException {
		return this.jsonObject.put(name, value);
	}

	public JSONObject put(String name, Object value) throws JSONException {
		return this.jsonObject.put(name, value);
	}

	public JSONObject put(String name, boolean value) throws JSONException {
		return this.jsonObject.put(name, value);
	}

	public JSONObject putOpt(String name, Object value) throws JSONException {
		return this.jsonObject.putOpt(name, value);
	}

	public Object remove(String name) {
		return this.jsonObject.remove(name);
	}

	public JSONArray toJSONArray(JSONArray jsonArray) throws JSONException {
		return this.jsonObject.toJSONArray(jsonArray);
	}

	public String toString() {
		return this.jsonObject.toString();
	}

	public String toString(int indentSpaces) throws JSONException {
		return this.jsonObject.toString(indentSpaces);
	}

}
