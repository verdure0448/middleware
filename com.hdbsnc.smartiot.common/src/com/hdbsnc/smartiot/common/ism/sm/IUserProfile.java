package com.hdbsnc.smartiot.common.ism.sm;

import java.util.List;
import java.util.regex.Pattern;

public interface IUserProfile {
	
	public String getUserPoolId();
	public String getUserPoolNm();
	public String getUserPoolRemark();
	
	public String getUserType();
	public String getUserNm();
	public String getCompNm();
	public String getDeptNm();
	public String getTitleNm();
	public String getRemark();
	public List<String> getUserFilterList();
	public List<Pattern> getUserFilterPatternList();

}
