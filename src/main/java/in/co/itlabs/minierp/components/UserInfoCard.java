package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import in.co.itlabs.minierp.entities.User;

public class UserInfoCard extends VerticalLayout {

	// ui

	private TextField nameField;
	private TextField usernameField;

	// non-ui

	public UserInfoCard() {

		setAlignItems(Alignment.CENTER);

		nameField = new TextField("Name");
		nameField.setWidthFull();
		nameField.setReadOnly(true);

		usernameField = new TextField("Username");
		usernameField.setWidthFull();
		usernameField.setReadOnly(true);

		add(nameField, usernameField);
	}

	public void setUser(User user) {
		nameField.setValue(user.getName());
		usernameField.setValue(user.getUsername());
	}
}
