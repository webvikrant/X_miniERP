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

		Div div1 = new Div();
		div1.setText("1. Scholarship applications and their status");

		Div div2 = new Div();
		div2.setText("2. Certificate applications and their status");

		add(div, div1, div2);
	}
}
