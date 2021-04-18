package in.co.itlabs.minierp.components;

import java.util.List;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.AcademicService;

public class Navigation extends HorizontalLayout implements AfterNavigationObserver {

	// ui

	private HorizontalLayout menuHLayout;
	private HorizontalLayout userHLayout;

	private Button dashboardButton;
	private Button studentsButton;
	private Button studentDetailsButton;

	private Select<College> collegeSelect;
	private Button userButton;
	private Button logoutButton;

	// non-ui

	private AcademicService academicService;

	public Navigation(AcademicService academicService) {
		this.academicService = academicService;

		addClassName("navbar");

		menuHLayout = new HorizontalLayout();
		menuHLayout.setMargin(false);
		menuHLayout.setPadding(true);
		menuHLayout.setSpacing(true);
		menuHLayout.setAlignItems(Alignment.END);

		userHLayout = new HorizontalLayout();
		userHLayout.setMargin(false);
		userHLayout.setPadding(true);
		userHLayout.setSpacing(true);
		userHLayout.setAlignItems(Alignment.END);

		dashboardButton = new Button("Dashboard", VaadinIcon.DASHBOARD.create());
		studentsButton = new Button("Students", VaadinIcon.USERS.create());
		studentDetailsButton = new Button("Student details", VaadinIcon.USER_CARD.create());

		collegeSelect = new Select<>();
		configureCollegeSelect();

		userButton = new Button("Vikrant Thakur", VaadinIcon.USER.create());
		logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());

		configureButtons();

		menuHLayout.add(dashboardButton, studentsButton, studentDetailsButton);

		userHLayout.add(collegeSelect, userButton, logoutButton);

		Span blank = new Span();

		add(menuHLayout, blank, userHLayout);
		expand(blank);

	}

	private void configureCollegeSelect() {
		collegeSelect.setWidth("250px");
		collegeSelect.setLabel("College");

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

	private void configureButtons() {
		// TODO Auto-generated method stub
		dashboardButton.addClickListener(evt -> {
			UI.getCurrent().navigate("dashboard");
		});

		studentsButton.addClickListener(evt -> {
			UI.getCurrent().navigate("students");
		});

		studentDetailsButton.addClickListener(evt -> {
			UI.getCurrent().navigate("student-details");
		});

		userButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {

		dashboardButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
		studentsButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
		studentDetailsButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);

		String location = event.getLocation().getFirstSegment();

		switch (location) {
		case "dashboard":
			dashboardButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			break;

		case "students":
			studentsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			break;

		case "student-details":
			studentDetailsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			break;

		default:
			break;
		}
	}

	public static abstract class NavigationEvent extends ComponentEvent<Navigation> {
		private College college;

		protected NavigationEvent(Navigation source, College college) {
			super(source, false);
			this.college = college;
		}

		public College getCollege() {
			return college;
		}
	}

	public static class CollegeSelectedEvent extends NavigationEvent {
		CollegeSelectedEvent(Navigation source, College college) {
			super(source, college);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}
