package in.co.itlabs.minierp.views;

import javax.annotation.PostConstruct;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import in.co.itlabs.minierp.layouts.AppLayout;

@PageTitle(value = "Dashboard")
@Route(value = "dashboard", layout = AppLayout.class)
public class DashboardView extends VerticalLayout {

	private Div div;

	@PostConstruct
	public void init() {
		div = new Div();
		div.setText("Dashboard");

		add(div);
	}
}
