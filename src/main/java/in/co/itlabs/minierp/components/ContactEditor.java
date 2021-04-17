package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Contact;
import in.co.itlabs.minierp.util.Editor;

public class ContactEditor extends VerticalLayout implements Editor {

	private Text typeText;
	private Text nameText;

	private TextField mobileNoField;
	private TextField whatsappNoField;
	private TextField emailIdField;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Contact> binder;

	public ContactEditor() {

		typeText = new Text("");
		nameText = new Text("");

		mobileNoField = new TextField();
		configureMobileNoField();

		whatsappNoField = new TextField();
		configureWhatsappNoField();

		emailIdField = new TextField();
		configureEmailIdField();

		binder = new Binder<>(Contact.class);

		binder.forField(mobileNoField).bind("mobileNo");
		binder.forField(whatsappNoField).bind("whatsappNo");
		binder.forField(emailIdField).bind("emailId");

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);
		buttonBar.setWidthFull();

		add(typeText, nameText, mobileNoField, whatsappNoField, emailIdField, buttonBar);

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

	}

	private void buildButtonBar(HorizontalLayout root) {

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
//					fireEvent(new SaveEvent(this, binder.getBean()));
			}
		});

		cancelButton.addClickListener(e -> {
//			fireEvent(new CancelEvent(this, binder.getBean()));
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