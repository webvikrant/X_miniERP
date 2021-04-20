package in.co.itlabs.minierp.views;

import javax.annotation.PostConstruct;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.layouts.AppLayout;

@PageTitle(value = "Error")
@Route(value = "error", layout = AppLayout.class)
public class ErrorView extends VerticalLayout implements AfterNavigationListener {

	private Div div;

	@PostConstruct
	public void init() {
		div = new Div();
		
		add(div);
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		String message = (String) VaadinSession.getCurrent().getAttribute("error-message");
		if(message!=null) {
			div.setText(message);
		}
	}
}