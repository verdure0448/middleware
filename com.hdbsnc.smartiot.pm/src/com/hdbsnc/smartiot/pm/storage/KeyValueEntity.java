package com.hdbsnc.smartiot.pm.storage;

public class KeyValueEntity<K, V> {

	private K key;
	private V value;

	public KeyValueEntity(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

}
