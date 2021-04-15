package in.co.itlabs.minierp.business.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.cdi.annotation.VaadinServiceScoped;

@VaadinServiceScoped
public class ExecutorService {
	private static ThreadPoolExecutor onDemandExecutor;

	private static final Logger logger = LoggerFactory.getLogger(ExecutorService.class);

	public ExecutorService() {
		logger.info("ExecutorService instance created");
//		System.out.println("ExecutorService instance created...");
		onDemandExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
	}

	public ThreadPoolExecutor getExecutor() {
		return onDemandExecutor;
	}

	@PreDestroy
	public void destroy() {
		logger.info("ExecutorService instance about to be destroyed...");
		onDemandExecutor.shutdown();
		logger.info("ExecutorService instance destroyed...");
	}
}
