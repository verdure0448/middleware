package com.hdbsnc.smartiot.service.command.common.util.text.impl;

import java.util.ArrayList;
import java.util.List;

import com.hdbsnc.smartiot.service.command.common.util.text.IColumn;
import com.hdbsnc.smartiot.service.command.common.util.text.IHeaderColumn;
import com.hdbsnc.smartiot.service.command.common.util.text.IHeaderRow;
import com.hdbsnc.smartiot.service.command.common.util.text.IRow;
import com.hdbsnc.smartiot.service.command.common.util.text.ITable;

public class StringTable implements ITable{
	
	private int maxWidth;
	private IHeaderRow header = null;
	private List<IRow> bodys = null;
	
	public StringTable(int maxWidth){
		this.maxWidth = maxWidth;
	}
	
	public void setHeader(IHeaderRow headerRow){
		this.header = headerRow;
	}
	
	public void setBodys(List<IRow> rows){
		this.bodys = rows;
	}

	@Override
	public IHeaderRow getHeader() {
		return this.header;
	}

	@Override
	public List<IRow> getBodys() {
		return this.bodys;
	}

	@Override
	public ITable addRowData(IRow row) {
		if(bodys==null) {
			bodys = new ArrayList<IRow>();
		}
		bodys.add(row);
		return this;
	}
	
	public ITable addRowData(String... values){
		if(bodys==null) {
			bodys = new ArrayList<IRow>();
		}
		if(this.header!=null){
			StringRow row = new StringRow();
			for(int i=0,s=values.length;i<s;i++){
				row.add(header.getHeaderColumn(i).getName(), values[i]);
			}
			bodys.add(row);
		}else{
			bodys.add(new StringRow(values));
		}
		return this;
	}

	@Override
	public IRow getRowData(int index) {
		if(bodys!=null) return bodys.get(index);
		return null;
	}

	@Override
	public int getRowCount() {
		if(bodys!=null) return bodys.size();
		return 0;
	}

	@Override
	public int getMaxWidth() {
		return this.maxWidth;
	}
	
	public void simplePrint(){
		StringBuilder out = new StringBuilder();
		lineText(out, header, ' ');
		out.append("\r\n");
		
		IRow row;
		for(int i=0, s=bodys.size();i<s;i++){
			row = bodys.get(i);
			lineText(out, row, ' ');
			out.append("\r\n");
		}
		
		System.out.println(out.toString());
	}
	
	public void headerOrderPrint(){
		StringBuilder out = new StringBuilder();
		lineText(out, header, ' ');
		out.append("\r\n");
		
		IRow row;
		for(int i=0, s=bodys.size();i<s;i++){
			row = bodys.get(i);
			lineTextByHeaderOrder(out, row, ' ');
			out.append("\r\n");
		}
		
		System.out.println(out.toString());
	}
	
	private static final String SKIP_STRING = "..";
	
	private void lineText(StringBuilder out, IHeaderRow data, char div){
		int columnCount;
		IHeaderColumn headerColumn;
		for (int i = 0, s=data.getHeaderColumnCount(); i < s; i++) {
			headerColumn = header.getHeaderColumn(i);			
			columnCount = maxWidth * headerColumn.ratio() / data.getSumRatio();
			if(columnCount < headerColumn.getName().length() + SKIP_STRING.length() + 1){
				out.append(headerColumn.getName(), 0, columnCount - SKIP_STRING.length() - 1);
				out.append(SKIP_STRING).append(div);
			}else{
				out.append(headerColumn.getName());
				for(int k=headerColumn.getName().length();k<columnCount;k++){
					out.append(div);
				}
			}
		}
	}
	
	/** 
	 * 해더 순서대로 라인을 출력한다.
	 *
	 * @param out
	 * @param data
	 * @param div
	 */
	private void lineTextByHeaderOrder(StringBuilder out, IRow data, char div){
		int columnCount;
		IColumn column;
		IHeaderColumn headerColumn;
		for (int i=0, s=header.getHeaderColumnCount(); i<s;i++){
			headerColumn = header.getHeaderColumn(i);
			column = data.getColumn(headerColumn.getName());
			columnCount = maxWidth * headerColumn.ratio() / header.getSumRatio();
			if(columnCount < column.getValueLength() + SKIP_STRING.length() + 1){
				out.append(column.getValue(), 0, columnCount - SKIP_STRING.length() - 1);
				out.append(SKIP_STRING).append(div);
			}else{
				out.append(column.getValue());
				for(int k=column.getValueLength();k<columnCount;k++){
					out.append(div);
				}
			}
		}
	}
	
	/**
	 * 데이터 배열 순서대로 라인을 출력한다.
	 * 
	 * @param out
	 * @param data
	 * @param div
	 */
	private void lineText(StringBuilder out, IRow data, char div){
		int columnCount;
		IColumn column;
		IHeaderColumn headerColumn;
		for (int i = 0, s=data.getColumnCount(); i < s; i++) {
			column = data.getColumn(i);
			headerColumn = header.getHeaderColumn(i);
			columnCount = maxWidth * headerColumn.ratio() / header.getSumRatio();
			if(columnCount < column.getValueLength() + SKIP_STRING.length() + 1){
				out.append(column.getValue(), 0, columnCount - SKIP_STRING.length() - 1);
				out.append(SKIP_STRING).append(div);
			}else{
				out.append(column.getValue());
				for(int k=column.getValueLength();k<columnCount;k++){
					out.append(div);
				}
			}
		}
	}

}
