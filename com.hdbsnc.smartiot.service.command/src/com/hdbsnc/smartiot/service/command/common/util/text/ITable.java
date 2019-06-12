package com.hdbsnc.smartiot.service.command.common.util.text;

import java.util.List;

public interface ITable {

	IHeaderRow getHeader();
	List<IRow> getBodys();
	
	ITable addRowData(IRow row);
	IRow getRowData(int index);
	int getRowCount();
	
	int getMaxWidth();
}
