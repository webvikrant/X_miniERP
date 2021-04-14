package in.co.itlabs.minierp.views;

import javax.annotation.PostConstruct;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import in.co.itlabs.minierp.layouts.GuestLayout;

@PageTitle(value = "Login")
@Route(value = "", layout = GuestLayout.class)
public class LoginView extends VerticalLayout {

	@PostConstruct
	public void init() {

		setAlignItems(Alignment.CENTER);

		// left is graphic
		Image image = new Image("https://picsum.photos/800/600", "miniERP");
//		image.getStyle().set("objectFit", "contain");
		image.addClassName("photo");
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
		TextField userNameField = new TextField("User name");
		userNameField.setWidthFull();

		PasswordField passwordField = new PasswordField("Password");
		passwordField.setWidthFull();

		Button loginButton = new Button("Login", VaadinIcon.SIGN_IN.create());
		loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button resetPasswordButton = new Button("Reset my password");

		root.add(userNameField, passwordField, loginButton, resetPasswordButton);
	}
}
