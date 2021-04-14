package in.co.itlabs.minierp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

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

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("contextInitialized()...");
		System.out.println("Application up...");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("contextDestroyed()...");
		System.out.println("Application down...");
	}

	@Override
	protected void servletInitialized() throws ServletException {
		super.servletInitialized();
		getService().addSessionInitListener(this);
		getService().addSessionDestroyListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		System.out.println("sessionInit()...");
		System.out.println("Session initialized...");
	}

	@Override
	public void sessionDestroy(SessionDestroyEvent event) {
		System.out.println("sessionDestroy()...");
		System.out.println("Session destroyed...");
	}

}
