package in.co.itlabs.minierp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import in.co.itlabs.minierp.components.StudentBasicSearch;
import in.co.itlabs.minierp.layouts.AppLayout;

@PageTitle(value = "Students")
@Route(value = "students", layout = AppLayout.class)
public class StudentsView extends VerticalLayout {

	@Inject
	private StudentBasicSearch studentBasicSearch;

	private final Text resultCount = new Text("Toolbar");

	@PostConstruct
	public void init() {

		setPadding(false);
		setAlignItems(Alignment.CENTER);

		Div titleDiv = new Div();
		buildTitle(titleDiv);

		studentBasicSearch.addListener(StudentBasicSearch.BasicSearchEvent.class, this::handleBasicSearchEvent);

		HorizontalLayout toolBar = new HorizontalLayout();
		toolBar.setWidthFull();
		toolBar.addClassName("card");
		buildToolBar(toolBar);

		VerticalLayout main = new VerticalLayout();
		main.add(toolBar);
		
		SplitLayout splitLayout = new SplitLayout();
		splitLayout.setWidthFull();
		splitLayout.setSplitterPosition(30);
		splitLayout.addToPrimary(studentBasicSearch);
		splitLayout.addToSecondary(main);

		add(titleDiv, splitLayout);

	}

	private void buildToolBar(HorizontalLayout root) {
		root.add(resultCount);
	}

	private Div buildTitle(Div root) {
		root.addClassName("view-title");
		root.add("Students");
		return root;
	}

	public void handleBasicSearchEvent(StudentBasicSearch.SearchEvent event) {
		String query = event.getQuery();
		resultCount.setText("Search query: " + query);
		Notification.show(query, 3000, Position.TOP_CENTER);
	}
}
