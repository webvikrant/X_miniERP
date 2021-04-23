package in.co.itlabs.minierp.views;

import javax.annotation.PostConstruct;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import in.co.itlabs.minierp.components.InfoCard;
import in.co.itlabs.minierp.components.VerticalBarChartExample;
import in.co.itlabs.minierp.layouts.AppLayout;

@PageTitle(value = "Dashboard")
@Route(value = "dashboard", layout = AppLayout.class)
public class DashboardView extends VerticalLayout {

	@PostConstruct
	public void init() {

		setPadding(false);
		setAlignItems(Alignment.CENTER);

		Div titleDiv = new Div();
		buildTitle(titleDiv);

		InfoCard infoCard1 = new InfoCard();
		infoCard1.setInfo("45", "Scholarship Applications", "pending at our end");

		InfoCard infoCard2 = new InfoCard();
		infoCard2.setInfo("15", "Kashmiri Migrants", "in all branches, all semesters");

		InfoCard infoCard3 = new InfoCard();
		infoCard3.setInfo("11", "Fee-waiver Students", "in all branches, all semesters");

		VerticalBarChartExample chart1 = new VerticalBarChartExample();
		chart1.setWidth("450px");

		VerticalBarChartExample chart2 = new VerticalBarChartExample();
		chart2.setWidth("450px");

		FlexLayout flex1 = new FlexLayout();
		flex1.setWidthFull();
		configureFlex(flex1);

		FlexLayout flex2 = new FlexLayout();
		flex2.setWidthFull();
		configureFlex(flex2);

		flex1.add(infoCard1, infoCard2, infoCard3);
		flex2.add(chart1, chart2);

		VerticalLayout main = new VerticalLayout();
		main.setAlignItems(Alignment.CENTER);
		main.add(flex1, flex2);

		add(titleDiv, main);
	}

	private void buildTitle(Div root) {
		root.addClassName("view-title");
		root.add("Dashboard");
	}

	private void configureFlex(FlexLayout flexLayout) {
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.getElement().getStyle().set("padding", "8px");
		flexLayout.getElement().getStyle().set("gap", "12px");
	}
}
