package com.hdbsnc.smartiot.service.command.common.util.text.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hdbsnc.smartiot.service.command.common.util.text.IHeaderColumn;
import com.hdbsnc.smartiot.service.command.common.util.text.IHeaderRow;

public class StringHeaderRow implements IHeaderRow{

	List<IHeaderColumn> list;
	private int ratioSum = 0;
	public StringHeaderRow(List<String> nameList){
		list = new ArrayList<IHeaderColumn>(nameList.size());
		Iterator<String> iter = nameList.iterator();
		String name;
		StringHeaderColumn column;
		while(iter.hasNext()){
			name = iter.next();
			column = new StringHeaderColumn(name);
			ratioSum += column.ratio();
			list.add(column);
		}
	}
	
	public StringHeaderRow(){
		list = new ArrayList<IHeaderColumn>();
	}
	
	public StringHeaderRow add(String name, int ratio){
		ratioSum += ratio;
		list.add(new StringHeaderColumn(name, ratio));
		return this;
	}
	
	@Override
	public List<IHeaderColumn> getHeaderColumns() {
		return this.list;
	}

	@Override
	public IHeaderColumn getHeaderColumn(int index) {
		return this.list.get(index);
	}

	@Override
	public int getHeaderColumnCount() {
		return list.size();
	}

	@Override
	public int getSumRatio() {
		return ratioSum;
	}

}
