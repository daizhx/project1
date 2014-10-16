package com.jiuzhansoft.ehealthtec.http.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class URLParamMap implements Map<String, String> {
	private final String charset;
	private Map<String, String> map;

	public URLParamMap() {
		this.map = new HashMap<String, String>();
		this.charset = "UTF-8";
	}

	public URLParamMap(String codeType) {
		this.map = new HashMap<String, String>();
		this.charset = codeType;
	}

	public void clear() {
		this.map.clear();
	}

	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		throw new RuntimeException("Can't use putAll method");
	}

	public Set<Entry<String, String>> entrySet() {
		return this.map.entrySet();
	}

	public boolean equals(Object obj) {
		return this.map.equals(obj);
	}

	public String get(Object key) {
		return (String) this.map.get(key);
	}

	public int hashCode() {
		return this.map.hashCode();
	}

	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	public Set<String> keySet() {
		return this.map.keySet();
	}

	public String put(String key, String value) {
		try {
			return (String) this.map.put(key,
					URLEncoder.encode(value, this.charset));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public void putAll(Map<? extends String, ? extends String> paramMap) {
		throw new RuntimeException("Can't use putAll method");
	}

	public String remove(Object key) {
		return (String) this.map.remove(key);
	}

	public int size() {
		return this.map.size();
	}

	public Collection<String> values() {
		return this.map.values();
	}
}