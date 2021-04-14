package in.co.itlabs.minierp.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExecutorService {
	private static ThreadPoolExecutor onDemandExecutor;

	private static final Logger logger = LoggerFactory.getLogger(ExecutorService.class);

	public ExecutorService() {
		logger.info("ExecutorService instance created", ExecutorService.class.getSimpleName());
//		System.out.println("ExecutorService instance created...");
		onDemandExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
	}

	public ThreadPoolExecutor getExecutor() {
		return onDemandExecutor;
	}

	@PreDestroy
	public void destroy() {
		System.out.println("ExecutorService instance about to be destroyed...");
		onDemandExecutor.shutdown();
		System.out.println("ExecutorService instance destroyed...");
	}
}
