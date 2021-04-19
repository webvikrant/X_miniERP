package in.co.itlabs.minierp.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.User;
import in.co.itlabs.minierp.layouts.GuestLayout;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.util.ErpModule;
import in.co.itlabs.minierp.util.Permission;

@PageTitle(value = "Login")
@Route(value = "", layout = GuestLayout.class)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

	@Inject
	private AcademicService academicService;

	@PostConstruct
	public void init() {

		setAlignItems(Alignment.CENTER);

		// left is graphic
		Image image = new Image("https://picsum.photos/800/600", "miniERP");
//		image.getStyle().set("objectFit", "contain");
		image.addClassName("card-photo");
		image.setWidth("600px");

		// right id form
		VerticalLayout loginForm = new VerticalLayout();
		loginForm.setWidth("400px");
		buildLogin(loginForm);

		HorizontalLayout main = new HorizontalLayout();
		main.setWidthFull();
		main.addClassName("card");
		main.setSpacing(false);
		main.add(image, loginForm);

		add(main);
	}

	private void buildLogin(VerticalLayout root) {
		root.setMargin(true);
		root.setAlignItems(Alignment.CENTER);

		H2 appName = new H2("miniERP");
		H2 clientName = new H2("IEC");

		TextField userNameField = new TextField("User name");
		userNameField.setWidthFull();

		PasswordField passwordField = new PasswordField("Password");
		passwordField.setWidthFull();

		Button loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
		loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		loginButton.addClickListener(e -> {
			// assuming login is successful.
			// store the user and his/her privileges in session
			// check if any colleges exist.
			User user = new User();
			user.setId(1);
			user.setUsername("webvikrant");

			Permission privilege = new Permission(true, true, true, true);
			Map<ErpModule, Permission> accessMap = new HashMap<>();
			accessMap.put(ErpModule.Students, privilege);

			VaadinSession.getCurrent().setAttribute(User.class, user);
			logger.info("User authenticated and stored in server-session: "
					+ VaadinSession.getCurrent().getSession().getId() + ", user: " + user.toString());

			List<College> colleges = academicService.getAllColleges();
			if (colleges != null && !colleges.isEmpty()) {
				VaadinSession.getCurrent().setAttribute(College.class, colleges.get(0));
				logger.info("College set in server-session: " + colleges.get(0).getName());
			}

			UI.getCurrent().navigate(DashboardView.class);

		});

		Button forgotPasswordButton = new Button("Forgot your password?");
		forgotPasswordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		HorizontalLayout buttonBar = new HorizontalLayout();
		Span blank = new Span();

		buttonBar.add(loginButton, blank, forgotPasswordButton);
		buttonBar.setWidthFull();
		buttonBar.expand(blank);

		root.add(appName, clientName, userNameField, passwordField, buttonBar);

	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		System.out.println("Login view...nav target: " + event.getNavigationTarget());
//		System.out.println("Login view...getForwardTargetParameters: "+event.getForwardTargetParameters());
//		System.out.println("Login view...getForwardTargetRouteParameters: "+event.getForwardTargetRouteParameters());
//		System.out.println("Login view...getRerouteTargetRouteParameters: "+event.getRerouteTargetRouteParameters());
		System.out.println("Login view...getRouteParameters: "+event.getRouteParameters());
	}
}
