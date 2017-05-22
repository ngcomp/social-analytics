package com.ngcomp.analytics.engine.web.hb;

import com.ngcomp.analytics.engine.domain.Sources;
import com.ngcomp.analytics.engine.test.AddItemToFBSourceQueue;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.List;

public class Master {
	
	private static final Logger logger = Logger.getLogger(Master.class);

	public static void main(String[] args) throws IOException, InterruptedException {

        try{
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
            context.register(JndiConfig.class, HibernateConfig.class);
            context.refresh();
            SourcesDAO bean = context.getBean(SourcesDAO.class);
            List<Sources> list = bean.getSources();
            AddItemToFBSourceQueue.addMessage(list, bean);
            
        } catch (Exception e) {
            logger.error("Unable to read the Sources ", e);
        }

	}
	private int EXPIRATION_TIME_IN_HOUR = 2;
	

//	public Master() {
//		Executors.newScheduledThreadPool(1).scheduleWithFixedDelay( fetchSources(), 0, EXPIRATION_TIME_IN_HOUR, TimeUnit.HOURS);
//	}

//	private static Runnable fetchSources() {
//		return new Runnable() {
//			public void run() {
//				try {
//				AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//				context.register(JndiConfig.class, HibernateConfig.class);
//				context.refresh();
//				SourcesDAO bean = context.getBean(SourcesDAO.class);
//				//List<Sources> list = bean.getSourceListForBrandId(1L);
//                List<Sources> list = bean.getSources();
//
//                AddItemToFBSourceQueue.addMessage(list);
//
//				} catch (Exception e) {
//					logger.error("Unable to read the Sources ", e);
//				}
//			}
//		};
//	}
}
