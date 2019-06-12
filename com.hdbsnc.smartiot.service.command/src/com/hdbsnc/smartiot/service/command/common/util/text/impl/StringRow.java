package com.hdbsnc.smartiot.service.command.common.util.text.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hdbsnc.smartiot.service.command.common.util.text.IColumn;
import com.hdbsnc.smartiot.service.command.common.util.text.IRow;

public class StringRow implements IRow{

	List<IColumn> columns;
	
	public StringRow(){
		columns = new ArrayList<IColumn>();
	}
	
	public StringRow(String... data){
		this(Arrays.asList(data));
	}
	
	public StringRow(List<String> data){
		this.columns = new ArrayList<IColumn>(data.size());
		Iterator<String> iter = data.iterator();
		String value;
		while(iter.hasNext()){
			value = iter.next();
			columns.add(new StringColumn(value));
		}
	}
	
	public StringRow(Map<String, String> data){
		this.columns = new ArrayList<IColumn>(data.size());
		Iterator<Entry<String, String>> iter = data.entrySet().iterator();
		Entry<String, String> entry;
		while(iter.hasNext()){
			entry = iter.next();
			columns.add(new StringColumn(entry.getKey(),entry.getValue()));
		}
	}
	
	@Override
	public List<IColumn> getColumns() {
		return this.columns;
	}

	@Override
	public IColumn getColumn(int index) {
		return this.columns.get(index);
	}
	
	@Override
	public IColumn getColumn(String name){
		IColumn col;
		for(int i=0,s=columns.size();i<s;i++){
			col = columns.get(i);
			if(col.getName().equals(name)) return col;
		}
		return null;
	}

	@Override
	public int getColumnCount() {
		return this.columns.size();
	}
	
	public StringRow add(String name, String data){
		this.columns.add(new StringColumn(name, data));
		return this;
	}
	
	public StringRow add(String data){
		this.columns.add(new StringColumn(data));
		return this;
	}

}
