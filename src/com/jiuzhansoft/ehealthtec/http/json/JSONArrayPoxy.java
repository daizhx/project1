package com.jiuzhansoft.ehealthtec.http.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JSONArrayPoxy extends JSONArray {

	private JSONArray jsonArray;

	public JSONArrayPoxy() {
		this.jsonArray = new JSONArray();
	}

	public JSONArray getArray(){
		return jsonArray;
	}
	
	public JSONArrayPoxy(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public boolean equals(Object obj) {
		return this.jsonArray.equals(obj);
	}

	public Object get(int index) throws JSONException {
		return this.jsonArray.get(index);
	}

	public boolean getBoolean(int paramInt) throws JSONException {
		return this.jsonArray.getBoolean(paramInt);
	}

	public double getDouble(int index) throws JSONException {
		return this.jsonArray.getDouble(index);
	}

	public int getInt(int index) throws JSONException {
		return this.jsonArray.getInt(index);
	}

	public JSONArrayPoxy getJSONArray(int index) throws JSONException {
		JSONArray localArray = this.jsonArray.getJSONArray(index);
		return new JSONArrayPoxy(localArray);
	}

	public JSONArrayPoxy getJSONArrayOrNull(int index) {
		JSONArrayPoxy retValue;
		try {
			retValue = new JSONArrayPoxy(this.jsonArray.getJSONArray(index));
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public JSONObjectProxy getJSONObject(int index) throws JSONException {
		JSONObject localJson = this.jsonArray.getJSONObject(index);
		return new JSONObjectProxy(localJson);
	}

	public JSONObjectProxy getJSONObjectOrNull(int index) {
		JSONObjectProxy retValue;
		try {
			JSONObject localJson = this.jsonArray.getJSONObject(index);
			retValue = new JSONObjectProxy(localJson);
		} catch (JSONException localJSONException) {
			retValue = null;
		}
		return retValue;
	}

	public long getLong(int index) throws JSONException {
		return this.jsonArray.getLong(index);
	}

	public String getString(int index) throws JSONException {
		return this.jsonArray.getString(index);
	}

	public int hashCode() {
		return this.jsonArray.hashCode();
	}

	public boolean isNull(int index) {
		return this.jsonArray.isNull(index);
	}

	public String join(String separator) throws JSONException {
		return this.jsonArray.join(separator);
	}

	public int length() {
		return this.jsonArray.length();
	}

	public Object opt(int index) {
		return this.jsonArray.opt(index);
	}

	public boolean optBoolean(int index) {
		return this.jsonArray.optBoolean(index);
	}

	public boolean optBoolean(int index, boolean fallback) {
		return this.jsonArray.optBoolean(index, fallback);
	}

	public double optDouble(int index) {
		return this.jsonArray.optDouble(index);
	}

	public double optDouble(int index, double fallback) {
		return this.jsonArray.optDouble(index, fallback);
	}

	public int optInt(int index) {
		return this.jsonArray.optInt(index);
	}

	public int optInt(int index, int fallback) {
		return this.jsonArray.optInt(index, fallback);
	}

	public JSONArray optJSONArray(int index) {
		return this.jsonArray.optJSONArray(index);
	}

	public JSONObject optJSONObject(int index) {
		return this.jsonArray.optJSONObject(index);
	}

	public long optLong(int index) {
		return this.jsonArray.optLong(index);
	}

	public long optLong(int index, long fallback) {
		return this.jsonArray.optLong(index, fallback);
	}

	public String optString(int index) {
		return this.jsonArray.optString(index);
	}

	public String optString(int index, String fallback) {
		return this.jsonArray.optString(index, fallback);
	}

	public JSONArray put(double value) throws JSONException {
		return this.jsonArray.put(value);
	}

	public JSONArray put(int value) {
		return this.jsonArray.put(value);
	}

	public JSONArray put(int index, double value) throws JSONException {
		return this.jsonArray.put(index, value);
	}

	public JSONArray put(int index, int value) throws JSONException {
		return this.jsonArray.put(index, value);
	}

	public JSONArray put(int index, long value) throws JSONException {
		return this.jsonArray.put(index, value);
	}

	public JSONArray put(int index, Object value) throws JSONException {
		return this.jsonArray.put(index, value);
	}

	public JSONArray put(int index, boolean value) throws JSONException {
		return this.jsonArray.put(index, value);
	}

	public JSONArray put(long value) {
		return this.jsonArray.put(value);
	}

	public JSONArray put(Object value) {
		return this.jsonArray.put(value);
	}

	public JSONArray put(boolean value) {
		return this.jsonArray.put(value);
	}

	public JSONObject toJSONObject(JSONArray array) throws JSONException {
		return this.jsonArray.toJSONObject(array);
	}

	public String toString() {
		return this.jsonArray.toString();
	}

	public String toString(int indentSpaces) throws JSONException {
		return this.jsonArray.toString(indentSpaces);
	}

}
