package in.co.itlabs.minierp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.cdi.CdiVaadinServlet;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;

@WebListener
@WebServlet(urlPatterns = "/web/*", asyncSupported = true)
public class Application extends CdiVaadinServlet
		implements ServletContextListener, SessionInitListener, SessionDestroyListener {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Context initialized, Application up.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Context destoyed, Application down.");
	}

	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(this);
		getService().addSessionDestroyListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		logger.info("Session initialized, id: "+event.getSession().getSession().getId());
	}

	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		logger.info("Session destroyed, id: "+event.getSession().getSession().getId());;
	}

}
