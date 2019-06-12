package com.hdbsnc.smartiot.service.command.common.util.text;

import java.util.List;

public interface IHeaderRow {

	List<IHeaderColumn> getHeaderColumns();
	
	IHeaderColumn getHeaderColumn(int index);
	
	int getHeaderColumnCount();
	
	int getSumRatio();
	
}
