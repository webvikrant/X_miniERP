package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Contact;
import in.co.itlabs.minierp.util.Editor;

public class ContactEditor extends VerticalLayout implements Editor {

	private Div info;

	private TextField mobileNoField;
	private TextField whatsappNoField;
	private TextField emailIdField;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Contact> binder;

	public ContactEditor() {

		info = new Div();

		mobileNoField = new TextField("Mobile No");
		configureMobileNoField();

		whatsappNoField = new TextField("Whatsapp No");
		configureWhatsappNoField();

		emailIdField = new TextField("Email Id");
		configureEmailIdField();

		binder = new Binder<>(Contact.class);

		binder.forField(mobileNoField).withValidator(string -> string.length() == 10, "Must be a 10 digit number")
				.bind("mobileNo");

		binder.forField(whatsappNoField).withValidator(string -> string.length() == 10, "Must be a 10 digit number")
				.bind("whatsappNo");

		binder.forField(emailIdField).withValidator(new EmailValidator("Doesn't look like a valid email address"))
				.bind("emailId");

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);
		buttonBar.setWidthFull();

		add(info, mobileNoField, whatsappNoField, emailIdField, buttonBar);
		setAlignSelf(Alignment.CENTER, info);

	}

	private void configureMobileNoField() {
		mobileNoField.setWidthFull();

	}

	private void configureWhatsappNoField() {
		whatsappNoField.setWidthFull();

	}

	private void configureEmailIdField() {
		emailIdField.setWidthFull();

	}

	public void setContact(Contact contact) {
		binder.setBean(contact);
		info.setText(contact.getType() + " - " + contact.getName());
	}

	private void buildButtonBar(HorizontalLayout root) {

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				fireEvent(new SaveEvent(this, binder.getBean()));
			}
		});

		cancelButton.addClickListener(e -> {
			fireEvent(new CancelEvent(this, binder.getBean()));
		});

		Span blank = new Span();

		root.add(saveButton, blank, cancelButton);
		root.expand(blank);
	}

	@Override
	public void setEditable(boolean editable) {
		mobileNoField.setReadOnly(!editable);

		saveButton.setVisible(editable);
		cancelButton.setVisible(editable);

	}

	public static abstract class ContactEvent extends ComponentEvent<ContactEditor> {
		private Contact contact;

		protected ContactEvent(ContactEditor source, Contact contact) {

			super(source, false);
			this.contact = contact;
		}

		public Contact getContact() {
			return contact;
		}
	}

	public static class SaveEvent extends ContactEvent {
		SaveEvent(ContactEditor source, Contact contact) {
			super(source, contact);
		}
	}

	public static class CancelEvent extends ContactEvent {
		CancelEvent(ContactEditor source, Contact contact) {
			super(source, contact);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}