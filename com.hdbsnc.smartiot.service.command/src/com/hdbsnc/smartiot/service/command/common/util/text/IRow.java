package com.hdbsnc.smartiot.service.command.common.util.text;

import java.util.List;

public interface IRow {

	List<IColumn> getColumns();
	
	IColumn getColumn(int index);
	
	IColumn getColumn(String columnName);
	
	int getColumnCount();
}
