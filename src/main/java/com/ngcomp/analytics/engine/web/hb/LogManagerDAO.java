package com.ngcomp.analytics.engine.web.hb;

import com.ngcomp.analytics.engine.domain.RunLog;

import java.util.Date;
import java.util.List;

public interface LogManagerDAO {
	
	public void writeLog(RunLog log);
	
	public List<RunLog> readLog(String sourceId);
	
	public List<RunLog> readLog(Date tillDate);
}
