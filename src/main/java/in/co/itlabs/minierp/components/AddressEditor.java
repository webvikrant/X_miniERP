package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Address;
import in.co.itlabs.minierp.entities.Address.Type;
import in.co.itlabs.minierp.entities.District;
import in.co.itlabs.minierp.entities.State;
import in.co.itlabs.minierp.services.ContactService;
import in.co.itlabs.minierp.util.Editor;

@UIScoped
public class AddressEditor extends VerticalLayout implements Editor {

	@Inject
	private ContactService contactService;
	
	private ComboBox<Type> typeCombo;
	private ComboBox<State> stateCombo;
	private ComboBox<District> districtCombo;
	private TextArea descriptionField;
	private TextField pincodeField;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Address> binder;

	@PostConstruct
	public void init() {

		typeCombo = new ComboBox<Address.Type>("Address type");
		configureTypeCombo();

		stateCombo = new ComboBox<State>();
		configureStateCombo();

		districtCombo = new ComboBox<District>();
		configureDistrictCombo();

		descriptionField = new TextArea("Description");
		configureAddressField();

		pincodeField = new TextField("Pincode");
		configurePincodeField();

		binder = new Binder<>(Address.class);

		binder.forField(typeCombo).asRequired("Type can not be blank").bind("type");
		binder.forField(stateCombo).asRequired("State can not be blank").bind("state");
		binder.forField(districtCombo).asRequired("District can not be blank").bind("district");
		binder.forField(descriptionField).asRequired("Address can not be blank").bind("description");
		binder.forField(pincodeField).asRequired("Pincode can not be blank").bind("pincode");

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout actionBar = buildActionBar();
		actionBar.setWidthFull();

		add(typeCombo, descriptionField, districtCombo, stateCombo, pincodeField, actionBar);
	}

	private void configureTypeCombo() {
		typeCombo.setWidthFull();
		typeCombo.setReadOnly(true);
		typeCombo.setItems(Address.Type.values());
	}

	private void configureStateCombo() {
		stateCombo.setWidthFull();
		stateCombo.setItemLabelGenerator(state -> {
			return state.getName();
		});
		stateCombo.setItems(contactService.getAllStates());
		stateCombo.addValueChangeListener(e -> {
			districtCombo.clear();
			if (e.getValue() != null) {
				districtCombo.setItems(contactService.getDistricts(e.getValue().getId()));
			}
		});
	}

	private void configureDistrictCombo() {
		districtCombo.setWidthFull();
		districtCombo.setItemLabelGenerator(district -> {
			return district.getName();
		});
	}

	private void configureAddressField() {
		descriptionField.setWidthFull();
		descriptionField.getElement().getStyle().set("minHeight", "120px");
	}

	private void configurePincodeField() {
		// TODO Auto-generated method stub
		pincodeField.setWidthFull();
	}

	public void setAddress(Address address) {
		binder.setBean(address);
	}

	private HorizontalLayout buildActionBar() {
		HorizontalLayout root = new HorizontalLayout();

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {

				binder.getBean().setDistrictId(binder.getBean().getDistrict().getId());

				fireEvent(new SaveEvent(this, binder.getBean()));
			}
		});

		cancelButton.addClickListener(e -> {
			fireEvent(new CancelEvent(this, binder.getBean()));
		});

		Span blank = new Span();

		root.add(saveButton, blank, cancelButton);
		root.expand(blank);

		return root;
	}

	public static abstract class AddressEvent extends ComponentEvent<AddressEditor> {
		private Address address;

		protected AddressEvent(AddressEditor source, Address address) {

			super(source, false);
			this.address = address;
		}

		public Address getAddress() {
			return address;
		}
	}

	public static class SaveEvent extends AddressEvent {
		SaveEvent(AddressEditor source, Address address) {
			super(source, address);
		}
	}

	public static class CancelEvent extends AddressEvent {
		CancelEvent(AddressEditor source, Address address) {
			super(source, address);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}

	@Override
	public void setEditable(boolean enabled) {
//		typeCombo.setReadOnly(!enabled);
		stateCombo.setReadOnly(!enabled);
		districtCombo.setReadOnly(!enabled);
		descriptionField.setReadOnly(!enabled);
		pincodeField.setReadOnly(!enabled);

		saveButton.setVisible(enabled);
		cancelButton.setVisible(enabled);
		
	}

}