package in.co.itlabs.minierp.views;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.layouts.GuestLayout;
import in.co.itlabs.minierp.services.AuthService;
import in.co.itlabs.minierp.services.EmailService;
import in.co.itlabs.minierp.services.AuthService.AuthenticatedUser;
import in.co.itlabs.minierp.services.AuthService.Credentials;

@PageTitle(value = "Login")
@Route(value = "login", layout = GuestLayout.class)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

	private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

	// ui

	private TextField userNameField = new TextField("User name");
	private PasswordField passwordField = new PasswordField("Password");
	private Button loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
	private Button forgotPasswordButton = new Button("Forgot your password?");

	private Binder<Credentials> binder;
	// non-ui

	@Inject
	private AuthService authService;

	@Inject
	private EmailService emailService;

	private final List<String> messages = new ArrayList<String>();

	@PostConstruct
	public void init() {

		setAlignItems(Alignment.CENTER);

		// left is graphic
		Image image = new Image("https://picsum.photos/800/600", "miniERP");
//		image.getStyle().set("objectFit", "contain");
		image.addClassName("card-photo");
		image.setWidth("650px");

		userNameField = new TextField("Username");
		userNameField.setWidthFull();

		passwordField = new PasswordField("Password");
		passwordField.setWidthFull();

		binder = new Binder<>(Credentials.class);

		binder.forField(userNameField).asRequired("Username can not be blank").bind("username");
		binder.forField(passwordField).asRequired("Password can not be blank").bind("password");

		binder.setBean(new Credentials());

		// right id form
		VerticalLayout loginForm = new VerticalLayout();
		loginForm.setWidth("350px");
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

		loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		loginButton.addClickShortcut(Key.ENTER);
		loginButton.addClickListener(e -> {
			// store the user and his/her privileges in session
			// check if any colleges exist.

			if (binder.validate().isOk()) {
				messages.clear();

				AuthenticatedUser authUser = null;

				if (binder.getBean().getUsername().equalsIgnoreCase("su")) {
					authUser = authService.authenticateSuperUser(messages, binder.getBean());
				} else {
					authUser = authService.authenticate(messages, binder.getBean());
				}

				if (authUser == null) {
					Notification.show(messages.toString(), 5000, Position.TOP_CENTER);
				} else {
					VaadinSession.getCurrent().setAttribute(AuthenticatedUser.class, authUser);

					List<College> colleges = authUser.getColleges();
					if (colleges != null && !colleges.isEmpty()) {
						VaadinSession.getCurrent().setAttribute(College.class, colleges.get(0));
						logger.info("College set in server-session: " + colleges.get(0).getName());
					}

					UI.getCurrent().navigate(DashboardView.class);

				}
			}
		});

		forgotPasswordButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		forgotPasswordButton.addClickListener(e -> {
			// CHANGE THIS
			// send test email
			String password = "testing 123";
			Email message = EmailBuilder.startingBlank().from("miniERP", "miniERP@itlabs.co.in")
					.to("Vikrant Thakur", "webvikrant@gmail.com").withSubject("Test email")
					.withPlainText("Your password is: " + password).buildEmail();

			Mailer mailer = emailService.getMailer();
			mailer.sendMail(message);
			Notification.show("Email sent", 5000, Position.TOP_CENTER);
		});

		HorizontalLayout buttonBar = new HorizontalLayout();
		Span blank = new Span();

		buttonBar.add(loginButton, blank, forgotPasswordButton);
		buttonBar.setWidthFull();
		buttonBar.expand(blank);

		root.add(appName, clientName, userNameField, passwordField, buttonBar);

	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		AuthenticatedUser user = VaadinSession.getCurrent().getAttribute(AuthenticatedUser.class);
		if (user != null) {
			// user already logged in
			event.forwardTo(DashboardView.class);
		}
	}
}
