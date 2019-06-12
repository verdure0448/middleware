package com.hdbsnc.smartiot.ism.impl;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hdbsnc.smartiot.common.ism.sm.ISession;


/**
 * Map<S1, S3> 와 동일하나 추가로 S2가 존재하여 S1, S2로 S3를 찾을 수 있는 Map
 * 기본 Map의 S1은 deviceId이다.
 * 
 * @author hjs0317
 *
 * @param <S1>
 * @param <S2>
 * @param <S3>
 */

public class DoubleKeySessionHashtable implements Map<String, ISession>{

	private Hashtable<String, ISession> deviceIdMap;
	private Hashtable<String, ISession> sessionKeyMap;
	    
	public DoubleKeySessionHashtable(){
		this.deviceIdMap = new Hashtable<String, ISession>();
		this.sessionKeyMap = new Hashtable<String, ISession>();
	}
	
	@Override
	public int size() {
		return this.deviceIdMap.size();
	}

	@Override
	public boolean isEmpty() {
		return this.deviceIdMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.deviceIdMap.containsKey(key);
	}
	
	public boolean containsSessionKey(String key){
		return this.sessionKeyMap.containsKey(key);
	}
	
	public boolean containsDeviceId(String id){
		return this.deviceIdMap.containsKey(id);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.deviceIdMap.containsValue(value);
	}
	
	public boolean containsSession(ISession session){
		return this.deviceIdMap.containsValue(session);
	}

	@Override
	public ISession get(Object key) {
		return this.deviceIdMap.get(key);
	}
	
	public ISession getBySessionKey(String key){
		return this.sessionKeyMap.get(key);
	}
	
	public ISession getByDeviceId(String id){
		return this.deviceIdMap.get(id);
	}

	@Override
	public synchronized ISession put(String key, ISession value) {
		this.sessionKeyMap.put(value.getSessionKey(), value);
		return this.deviceIdMap.put(key, value);
	}
	
	public synchronized ISession put(ISession value){
		this.deviceIdMap.put(value.getDeviceId(), value);
		this.sessionKeyMap.put(value.getSessionKey(), value);
		return value;
	}
	
	public synchronized ISession putSession(ISession value){
		return put(value);
	}

	@Override
	public synchronized ISession remove(Object key) {
		return this.deviceIdMap.remove(key);
	}
	
	public synchronized ISession removeBySessionKey(String key){
		ISession removeSess = this.sessionKeyMap.remove(key);
		this.deviceIdMap.remove(removeSess.getDeviceId());
		return removeSess;
	}
	
	public synchronized ISession removeByDeviceId(String id){
		ISession removeSess = this.deviceIdMap.remove(id);
		this.sessionKeyMap.remove(removeSess.getSessionKey());
		return removeSess;
	}

	@Override
	public synchronized void clear() {
		this.deviceIdMap.clear();
		this.sessionKeyMap.clear();
		
	}

	@Override
	public synchronized void putAll(Map<? extends String, ? extends ISession> m) {
		putAll((Collection<ISession>)m.values());
		
	}
	
	public synchronized void putAll(Collection<ISession> sessionList){
		Iterator<ISession> iter = sessionList.iterator();
		ISession temp;
		while(iter.hasNext()){
			temp = iter.next();
			this.deviceIdMap.put(temp.getDeviceId(), temp);
			this.sessionKeyMap.put(temp.getSessionKey(), temp);
		}
	}

	@Override
	public Set<String> keySet() {
		return this.deviceIdMap.keySet();
	}
	
	public Set<String> sessionKeySet(){
		return this.sessionKeyMap.keySet();
	}
	
	public Set<String> deviceIdSet(){
		return this.deviceIdMap.keySet();
	}

	@Override
	public Collection<ISession> values() {
		return this.deviceIdMap.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, ISession>> entrySet() {
		return this.deviceIdMap.entrySet();
	}
	
	public Set<java.util.Map.Entry<String, ISession>> sessionKeyEntrySet(){
		return this.sessionKeyMap.entrySet();
	}

	public Set<java.util.Map.Entry<String, ISession>> deviceIdEntrySet(){
		return this.deviceIdMap.entrySet();
	}
	



}
