package in.co.itlabs.minierp.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import in.co.itlabs.minierp.entities.Contact;
import in.co.itlabs.minierp.services.ContactService;

@UIScoped
public class StudentContactDetails extends VerticalLayout {

	@Inject
	private ContactService contactService;

	@Inject
	private ContactEditor contactEditor;
	private Contact contact;

	private int studentId;

	private Grid<Contact> grid = new Grid<>(Contact.class);

	private Dialog dialog = new Dialog();
	private List<String> messages = new ArrayList<String>();

	@PostConstruct
	public void init() {

		contactEditor.addListener(ContactEditor.SaveEvent.class, this::handleSaveEvent);
		contactEditor.addListener(ContactEditor.CancelEvent.class, this::handleCancelEvent);

		dialog.setWidth("400px");
		dialog.setModal(true);
		dialog.setDraggable(true);

		configureGrid();

		add(grid);

		reload();
	}

	private void configureGrid() {
		grid.setHeightByRows(true);

		grid.addComponentColumn(contact -> {

			Button editButton = new Button("Edit", VaadinIcon.PENCIL.create());
			editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
			editButton.addClickListener(e -> {
				dialog.removeAll();
				dialog.add(contactEditor);
				dialog.open();
				contactEditor.setContact(contact);
			});

			Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
			deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
			deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
			deleteButton.addClickListener(e -> {
				dialog.removeAll();
				dialog.open();
			});

			HorizontalLayout buttonBar = new HorizontalLayout();
			buttonBar.add(editButton, deleteButton);

			return buttonBar;

		}).setHeader("Edit").setWidth("120px");

	}

	public void reload() {
		List<Contact> contacts = contactService.getAllContacts(studentId);
		grid.setItems(contacts);
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
		reload();
	}

	public void handleSaveEvent(ContactEditor.SaveEvent event) {
		Contact contact = event.getContact();
		messages.clear();
		boolean success = contactService.updateContact(messages, contact);
		if (success) {
			Notification.show("Contact updated successfully", 3000, Position.TOP_CENTER);
			contact.setStudentId(studentId);
			contactEditor.setContact(contact);
			reload();
		} else {
			Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
		}
	}

	public void handleCancelEvent(ContactEditor.CancelEvent event) {
		dialog.close();
	}
}
