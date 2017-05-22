package com.ngcomp.analytics.engine.web.hb;

import com.ngcomp.analytics.engine.domain.RunLog;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository(value = "LogManagerDAO")
@Transactional
public class LogManagerDAOImpl implements LogManagerDAO {
	
	private static final Logger logger = Logger.getLogger(LogManagerDAO.class);
	 
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void writeLog(RunLog log) {
		
		logger.info("Writing log for "+ log.toString());
		try{
			sessionFactory.getCurrentSession().save(log);
			
		}catch(Exception e){
			logger.error("Unable to write logs for "+ log, e);
		}

	}

	@Override
	public List<RunLog> readLog(String sourceId) {
		return null;
	}

	@Override
	public List<RunLog> readLog(Date tillDate) {
		return null;
	}

}
