package in.co.itlabs.minierp.components;

import java.util.List;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.AuthService.AuthenticatedUser;
import in.co.itlabs.minierp.views.DashboardView;
import in.co.itlabs.minierp.views.LoginView;
import in.co.itlabs.minierp.views.StudentDetailsView;
import in.co.itlabs.minierp.views.StudentsView;

public class NavBar extends HorizontalLayout {

	// ui
	private Select<College> collegeSelect;
	private MenuBar menuBar;
	private Button userButton;
	private Button logoutButton;

	// non-ui

	private AcademicService academicService;

	public NavBar(AcademicService academicService) {
		this.academicService = academicService;

		setAlignItems(Alignment.CENTER);

		addClassName("navbar");

		collegeSelect = new Select<>();
		configureCollegeSelect();

		menuBar = new MenuBar();
		configureMenuBar();

		userButton = new Button("", VaadinIcon.USER.create());
		logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());

		configureButtons();

		Span blank = new Span();

		add(collegeSelect, menuBar, blank, userButton, logoutButton);
		expand(blank);

	}

	private void configureButtons() {
		AuthenticatedUser authuUser = VaadinSession.getCurrent().getAttribute(AuthenticatedUser.class);
		if (authuUser != null) {
			userButton.setText(authuUser.getName());
		}

		logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		logoutButton.addClickListener(e -> {
			VaadinSession.getCurrent().getSession().invalidate();
			UI.getCurrent().navigate(LoginView.class);
		});
	}

	private void configureMenuBar() {

		menuBar.setOpenOnHover(true);
		menuBar.addThemeVariants(MenuBarVariant.LUMO_PRIMARY);

		MenuItem mainMenuItem = menuBar.addItem(VaadinIcon.MENU.create());
		mainMenuItem.add("Menu");

		SubMenu subMenu = mainMenuItem.getSubMenu();
		subMenu.addItem("Dashboard", e -> UI.getCurrent().navigate(DashboardView.class));
		subMenu.addItem("Students", e -> UI.getCurrent().navigate(StudentsView.class));
		subMenu.addItem("Student details", e -> UI.getCurrent().navigate(StudentDetailsView.class));

	}

	private void configureCollegeSelect() {
		collegeSelect.setWidth("250px");
		collegeSelect.setPlaceholder("Select a college");

		List<College> colleges = academicService.getAllColleges();
		collegeSelect.setItems(colleges);

		collegeSelect.setItemLabelGenerator(college -> {
			return college.getCode() + " - " + college.getName();
		});

		collegeSelect.setValue(VaadinSession.getCurrent().getAttribute(College.class));

		collegeSelect.addValueChangeListener(e -> {
			VaadinSession.getCurrent().setAttribute(College.class, e.getValue());
			VaadinSession.getCurrent().setAttribute(Student.class, null);
			UI.getCurrent().getPage().reload();
		});

	}

	public static abstract class NavigationEvent extends ComponentEvent<NavBar> {
		private College college;

		protected NavigationEvent(NavBar source, College college) {
			super(source, false);
			this.college = college;
		}

		public College getCollege() {
			return college;
		}
	}

	public static class CollegeSelectedEvent extends NavigationEvent {
		CollegeSelectedEvent(NavBar source, College college) {
			super(source, college);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}
