package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

@ViewScoped
public class Navigation extends HorizontalLayout implements AfterNavigationObserver {

	private HorizontalLayout menuHLayout;
	private HorizontalLayout userHLayout;

	private Button dashboardButton;
	private Button studentsButton;
	private Button studentDetailsButton;

	private Button userButton;
	private Button logoutButton;

	@PostConstruct
	public void init() {
		addClassName("navbar");

		menuHLayout = new HorizontalLayout();
		menuHLayout.setMargin(false);
		menuHLayout.setPadding(true);
		menuHLayout.setSpacing(true);

		userHLayout = new HorizontalLayout();
		userHLayout.setMargin(false);
		userHLayout.setPadding(true);
		userHLayout.setSpacing(true);

		dashboardButton = new Button("Dashboard", VaadinIcon.DASHBOARD.create());
		studentsButton = new Button("Students", VaadinIcon.USERS.create());
		studentDetailsButton = new Button("Student details", VaadinIcon.USER_CARD.create());

		userButton = new Button("Vikrant Thakur", VaadinIcon.USER.create());
		logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());

		configureButtons();

		menuHLayout.add(dashboardButton, studentsButton, studentDetailsButton);

		userHLayout.setAlignItems(Alignment.CENTER);
		userHLayout.add(userButton, logoutButton);

		Span blank = new Span();

		add(menuHLayout, blank, userHLayout);
		expand(blank);

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
}
