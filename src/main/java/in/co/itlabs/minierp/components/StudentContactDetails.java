package in.co.itlabs.minierp.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;

import in.co.itlabs.minierp.entities.Address;
import in.co.itlabs.minierp.entities.Contact;
import in.co.itlabs.minierp.entities.District;
import in.co.itlabs.minierp.entities.State;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.ContactService;
import in.co.itlabs.minierp.services.StudentService;

public class StudentContactDetails extends VerticalLayout {

	// ui

	private Grid<Contact> contactGrid = new Grid<>(Contact.class);
	private Grid<Address> addressGrid = new Grid<>(Address.class);

	private ContactEditor contactEditor;
	private AddressEditor addressEditor;

	private Dialog dialog = new Dialog();
	
	// non-ui

	private ContactService contactService;
	private StudentService studentService;

	private int studentId;
	private List<String> messages = new ArrayList<String>();

	public StudentContactDetails(StudentService studentService, ContactService contactService) {
		this.studentService = studentService;
		this.contactService = contactService;

		contactEditor = new ContactEditor();
		addressEditor = new AddressEditor(contactService);
		
		contactEditor.addListener(ContactEditor.SaveEvent.class, this::handleContactSaveEvent);
		contactEditor.addListener(ContactEditor.CancelEvent.class, this::handleContactCancelEvent);

		addressEditor.addListener(AddressEditor.SaveEvent.class, this::handleAddressSaveEvent);

		dialog.setWidth("400px");
		dialog.setModal(true);
		dialog.setDraggable(true);

		Div contactDiv = new Div();
		contactDiv.getStyle().set("fontSize", "small");
		contactDiv.setText("Phones & Emails");

		Div addressDiv = new Div();
		addressDiv.getStyle().set("fontSize", "small");
		addressDiv.setText("Addresses");

		configureContactGrid();
		configureAddressGrid();

		add(contactDiv, contactGrid, new Div(), addressDiv, addressGrid);

		reload();
	}

	private void configureContactGrid() {
		contactGrid.setHeightByRows(true);

		contactGrid.removeAllColumns();

		contactGrid.addColumn("type");

		contactGrid.addColumn(contact -> contact.getName()).setHeader("Name");

		contactGrid.addColumn("mobileNo").setWidth("100px");
		contactGrid.addColumn("whatsappNo").setWidth("100px");
		contactGrid.addColumn("emailId").setWidth("170px");

		contactGrid.addComponentColumn(contact -> {
			Div contactDiv = new Div();
			contactDiv.setText("Phones & Emails");

			Button editButton = new Button("Edit", VaadinIcon.PENCIL.create());
			editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
			editButton.addClickListener(e -> {
				dialog.removeAll();
				dialog.add(contactEditor);
				dialog.open();
				contactEditor.setContact(contact);
			});

			HorizontalLayout buttonBar = new HorizontalLayout();
			buttonBar.add(editButton);

			return buttonBar;

		}).setHeader("Edit").setWidth("90px");

	}

	private void configureAddressGrid() {
		addressGrid.setHeightByRows(true);

		addressGrid.removeAllColumns();

		addressGrid.addColumn("type");
		addressGrid.addComponentColumn(address -> {
			TextArea root = new TextArea();
			root.setWidthFull();
			root.setHeight("100px");
			if (address.getDescription() != null) {
				root.setValue(address.getDescription());
			}
			root.setReadOnly(true);
			return root;
		}).setHeader("Description").setWidth("250px");

		addressGrid.addComponentColumn(address -> {
			VerticalLayout root = new VerticalLayout();
			Div districtDiv = new Div();
			Div stateDiv = new Div();
			Div pincodeDiv = new Div();

			if (address.getDistrict() != null) {
				districtDiv.setText(address.getDistrict().getName());
			}
			if (address.getState() != null) {
				stateDiv.setText(address.getState().getName());
			}
			if (address.getPincode() != null) {
				pincodeDiv.setText(address.getPincode());
			}

			root.add(districtDiv, stateDiv, pincodeDiv);

			return root;
		}).setHeader("District, State & Pincode").setWidth("200px");

		addressGrid.addComponentColumn(address -> {

			Button editButton = new Button("Edit", VaadinIcon.PENCIL.create());
			editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
			editButton.addClickListener(e -> {
				dialog.removeAll();
				dialog.add(addressEditor);
				dialog.open();
				addressEditor.setAddress(address);
			});

			HorizontalLayout buttonBar = new HorizontalLayout();
			buttonBar.add(editButton);

			return buttonBar;

		}).setHeader("Edit").setWidth("90px");

	}

	public void reload() {
		Student student = studentService.getStudentById(studentId);

		List<Contact> contacts = contactService.getAllContacts(studentId);

		for (Contact contact : contacts) {
			contact.setStudent(student);
		}

		contactGrid.setItems(contacts);

		List<Address> addresses = contactService.getAllAddresses(studentId);

		for (Address address : addresses) {

			District district = contactService.getDistrict(address.getDistrictId());
			address.setDistrict(district);

			if (district != null) {
				State state = district.getState();
				address.setState(state);
			}
		}
		addressGrid.setItems(addresses);

	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
		reload();
	}

	public void handleContactSaveEvent(ContactEditor.SaveEvent event) {
		Contact contact = event.getContact();
		messages.clear();
		boolean success = contactService.updateContact(messages, contact);
		if (success) {
			Notification.show("Contact updated successfully", 3000, Position.TOP_CENTER);
//			contactEditor.setContact(contact);
			dialog.close();
			reload();
		} else {
			Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
		}
	}

	public void handleContactCancelEvent(ContactEditor.CancelEvent event) {
		dialog.close();
	}

	public void handleAddressSaveEvent(AddressEditor.SaveEvent event) {
		Address address = event.getAddress();
		messages.clear();
		boolean success = contactService.updateAddress(messages, address);
		if (success) {
			Notification.show("Address updated successfully", 3000, Position.TOP_CENTER);
//			contactEditor.setContact(contact);
			dialog.close();
			reload();
		} else {
			Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
		}
	}

	public void handleAddressCancelEvent(AddressEditor.CancelEvent event) {
		dialog.close();
	}

}
