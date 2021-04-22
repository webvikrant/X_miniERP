package in.co.itlabs.minierp.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.User;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.AuthService;

public class UserCollegesComponent extends VerticalLayout {

	// ui
	private CheckboxGroup<College> collegeChecks;

	private Button saveButton;
	private Button cancelButton;

	private Binder<User> binder;

	// non-ui

	private AcademicService academicService;
	private AuthService authService;

	private int userId;
	private final List<String> messages = new ArrayList<String>();

	public UserCollegesComponent(AuthService authService, AcademicService academicService) {
		this.authService = authService;
		this.academicService = academicService;

		collegeChecks = new CheckboxGroup<College>();
		configureCollegeChecks();

		saveButton = new Button("Save", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		binder = new Binder<>(User.class);

		binder.forField(collegeChecks).asRequired("At least one College must be selected").bind("colleges");

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);
		buttonBar.setWidthFull();

		add(collegeChecks, buttonBar);
	}

	private void configureCollegeChecks() {
		collegeChecks.setLabel("Permitted colleges");
		collegeChecks.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
		collegeChecks.setItemLabelGenerator(college -> {
			return college.getCode() + " - " + college.getName();
		});

		List<College> colleges = academicService.getAllColleges();
		collegeChecks.setItems(colleges);
	}

	private void buildButtonBar(HorizontalLayout root) {
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
//				fireEvent(new SaveEvent(this, binder.getBean()));
				// call the studentService to update the details
				messages.clear();
				authService.updateUserColleges(messages, binder.getBean().getId(), binder.getBean().getColleges());
			}
		});

		cancelButton.addClickListener(e -> {
//			fireEvent(new CancelEvent(this, binder.getBean()));
			setUserId(userId);
		});

		root.add(saveButton, cancelButton);
	}

	public void setUserId(int userId) {
		User user = authService.getUserById(userId);
		binder.setBean(user);
	}

	public void setEditable(boolean editable) {
		collegeChecks.setReadOnly(!editable);

		saveButton.setVisible(editable);
		cancelButton.setVisible(editable);
	}

//	public static abstract class StudentAdmissionEvent extends ComponentEvent<UserCollegeDetails> {
//		private Student student;
//
//		protected StudentAdmissionEvent(UserCollegeDetails source, Student student) {
//
//			super(source, false);
//			this.student = student;
//		}
//
//		public Student getStudent() {
//			return student;
//		}
//	}
//
//	public static class SaveEvent extends StudentAdmissionEvent {
//		SaveEvent(UserCollegeDetails source, Student student) {
//			super(source, student);
//		}
//	}
//
//	public static class CancelEvent extends StudentAdmissionEvent {
//		CancelEvent(UserCollegeDetails source, Student student) {
//			super(source, student);
//		}
//	}
//
//	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
//			ComponentEventListener<T> listener) {
//
//		return getEventBus().addListener(eventType, listener);
//	}

}
