package in.co.itlabs.minierp.views;

import java.util.ArrayList;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.components.NewUserForm;
import in.co.itlabs.minierp.components.UserFilterForm;
import in.co.itlabs.minierp.entities.User;
import in.co.itlabs.minierp.layouts.AppLayout;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.AuthService;
import in.co.itlabs.minierp.services.AuthService.AuthenticatedUser;

@PageTitle(value = "Users")
@Route(value = "users", layout = AppLayout.class)
public class UsersView extends VerticalLayout implements BeforeEnterObserver {

	// ui

	private UserFilterForm filterForm;
	private NewUserForm newUserForm;
	private Grid<User> grid;
	private Div resultCount;
	private Dialog dialog;

	// non-ui

	@Inject
	private AuthService authService;

	@Inject
	private AcademicService academicService;

	private String queryString;

	@PostConstruct
	public void init() {

		setPadding(false);
		setAlignItems(Alignment.CENTER);

		Div titleDiv = new Div();
		buildTitle(titleDiv);

		dialog = new Dialog();
		dialog.setModal(true);
		dialog.setDraggable(true);

		newUserForm = new NewUserForm(academicService);
		newUserForm.setUser(new User());
		newUserForm.addListener(NewUserForm.SaveEvent.class, this::handleSaveEvent);
		newUserForm.addListener(NewUserForm.CancelEvent.class, this::handleCancelEvent);

		queryString = null;

		filterForm = new UserFilterForm();
		filterForm.setPadding(false);
		filterForm.addListener(UserFilterForm.FilterEvent.class, this::handleFilterEvent);

		resultCount = new Div();
		resultCount.addClassName("small-text");
		resultCount.setWidth("150px");

		grid = new Grid<>(User.class);
		configureGrid();

		HorizontalLayout toolBar = new HorizontalLayout();
		buildToolBar(toolBar);
		toolBar.setWidthFull();

		VerticalLayout root = new VerticalLayout();
		root.add(toolBar, grid);

		add(titleDiv, root);

		reload();
	}

	private void configureGrid() {
		grid.removeAllColumns();

		grid.addColumn("name").setHeader("name").setWidth("100px");
		grid.addColumn("username").setHeader("Username").setWidth("100px");
		grid.addColumn("entity").setHeader("Linked entity").setWidth("150px");
		grid.addColumn("entityId").setHeader("Linked entity Id").setWidth("150px");
		grid.addColumn("entityName").setHeader("Linked entity Name").setWidth("150px");

		grid.addComponentColumn(student -> {
			Button button = new Button("More", VaadinIcon.ARROW_FORWARD.create());
			button.addThemeVariants(ButtonVariant.LUMO_SMALL);
			button.addClickListener(e -> {
				VaadinSession.getCurrent().setAttribute(User.class, student);
				UI.getCurrent().navigate("student-details");
			});

			return button;
		}).setHeader("More");

	}

	private void buildToolBar(HorizontalLayout root) {
//		root.setAlignItems(Alignment.END);

		Button createButton = new Button("New", VaadinIcon.PLUS.create());
		createButton.setWidth("100px");
		createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		createButton.addClickListener(e -> {
			dialog.setWidth("400px");
			dialog.removeAll();
			dialog.open();
			dialog.add(newUserForm);
		});

		Span blank = new Span();

		root.add(resultCount, filterForm, blank, createButton);
		root.setAlignItems(Alignment.CENTER);
		root.expand(blank);

	}

	private void buildTitle(Div root) {
		root.addClassName("view-title");
		root.add("Users");
	}

	public void handleFilterEvent(UserFilterForm.FilterEvent event) {
		queryString = event.getQueryString();
		reload();
	}

	public void handleSaveEvent(NewUserForm.SaveEvent event) {
		List<String> messages = new ArrayList<String>();
		User user = event.getUser();

		int userId = authService.createUser(messages, user);
		if (userId > 0) {
			Notification.show("Student created successfully", 3000, Position.TOP_CENTER);
			reload();
//			user.clear();
			newUserForm.setUser(user);
		} else {
			Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
		}
	}

	public void handleCancelEvent(NewUserForm.CancelEvent event) {
		dialog.close();
	}

	public void reload() {
		List<User> users = authService.getAllUsers(queryString);
		resultCount.setText("Record(s) found: " + users.size());
		grid.setItems(users);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		AuthenticatedUser authUser = VaadinSession.getCurrent().getAttribute(AuthenticatedUser.class);
		if (!authUser.isSuperUser()) {
			VaadinSession.getCurrent().setAttribute("error-message", "Access denied.");
			event.forwardTo(ErrorView.class);
		}
	}
}
