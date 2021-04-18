package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Program;
import in.co.itlabs.minierp.entities.Session;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.util.Semester;

public class NewStudentForm extends VerticalLayout {

	// ui

	private Div collegeDiv;
	private ComboBox<Session> sessionCombo;
	private ComboBox<Program> programCombo;
	private ComboBox<Semester> semesterCombo;
	private TextField prnNoField;
	private TextField admissionIdField;
	private TextField nameField;
	private Checkbox hostelCheck;
	private Checkbox scholarshipCheck;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Student> binder;

	// non-ui

	private AcademicService academicService;
	private int collegeId = 0;

	public NewStudentForm(AcademicService academicService) {
		this.academicService = academicService;

		setAlignItems(Alignment.CENTER);

		collegeDiv = new Div();

		College college = VaadinSession.getCurrent().getAttribute(College.class);
		if (college != null) {
			collegeId = college.getId();
			collegeDiv.setText(college.getCode() + " - " + college.getName());
		}

		sessionCombo = new ComboBox<Session>();
		configureSessionSelect();

		programCombo = new ComboBox<Program>();
		configureProgramSelect();

		semesterCombo = new ComboBox<Semester>();
		configureSemesterSelect();

		prnNoField = new TextField();
		configurePrnNoField();

		admissionIdField = new TextField();
		configureAdmissionIdField();

		nameField = new TextField();
		configureNameField();

		hostelCheck = new Checkbox("Hostel");
		scholarshipCheck = new Checkbox("Scholarship");
		HorizontalLayout checks = new HorizontalLayout();
		checks.add(hostelCheck, scholarshipCheck);

		binder = new Binder<>(Student.class);

		binder.forField(sessionCombo).asRequired("Session can not be blank").bind("admissionSession");
		binder.forField(programCombo).asRequired("Program can not be blank").bind("admissionProgram");
		binder.forField(semesterCombo).asRequired("Semester can not be blank").bind("admissionSemester");

		binder.forField(prnNoField).asRequired("PRN No can not be blank").bind("prnNo");
		binder.forField(admissionIdField).asRequired("Admission Id can not be blank").bind("admissionId");
		binder.forField(nameField).asRequired("Name can not be blank").bind("name");

		binder.forField(hostelCheck).bind("hostel");
		binder.forField(scholarshipCheck).bind("scholarship");

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);

		buttonBar.setWidthFull();

		add(collegeDiv, sessionCombo, programCombo, semesterCombo, prnNoField, admissionIdField, nameField, checks,
				buttonBar);

	}

	private void configureSessionSelect() {
		// TODO Auto-generated method stub
		sessionCombo.setWidthFull();
		sessionCombo.setLabel("Session");
		sessionCombo.setPlaceholder("Select a session");

		sessionCombo.setItemLabelGenerator(session -> {
			return session.getName();
		});

		sessionCombo.setItems(academicService.getAllSessions());
	}

	private void configureProgramSelect() {
		programCombo.setWidthFull();
		programCombo.setLabel("Program");
		programCombo.setPlaceholder("Select a program");
		programCombo.setItemLabelGenerator(program -> {
			return program.getName();
		});

		programCombo.setItems(academicService.getAllPrograms());
	}

	private void configureSemesterSelect() {
		semesterCombo.setWidthFull();
		semesterCombo.setLabel("Semester");
		semesterCombo.setPlaceholder("Select a semester");
		semesterCombo.setItems(Semester.Semester_1, Semester.Semester_3);
	}

	private void configurePrnNoField() {
		prnNoField.setWidthFull();
		prnNoField.setLabel("Prn No");
		prnNoField.setPlaceholder("Type PRN no");
	}

	private void configureAdmissionIdField() {
		admissionIdField.setWidthFull();
		admissionIdField.setLabel("Admission Id");
		admissionIdField.setPlaceholder("Type admission id");
	}

	private void configureNameField() {
		nameField.setWidthFull();
		nameField.setLabel("Name");
		nameField.setPlaceholder("Type name");

	}

	public void setStudent(Student student) {
//		College college = VaadinSession.getCurrent().getAttribute(College.class);
		student.setCollegeId(collegeId);
		binder.setBean(student);
	}

	private void buildButtonBar(HorizontalLayout root) {

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				binder.getBean().setAdmissionSessionId(binder.getBean().getAdmissionSession().getId());
				binder.getBean().setAdmissionProgramId(binder.getBean().getAdmissionProgram().getId());
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

	public static abstract class NewStudentFormEvent extends ComponentEvent<NewStudentForm> {
		private Student student;

		protected NewStudentFormEvent(NewStudentForm source, Student student) {

			super(source, false);
			this.student = student;
		}

		public Student getStudent() {
			return student;
		}
	}

	public static class SaveEvent extends NewStudentFormEvent {
		SaveEvent(NewStudentForm source, Student student) {
			super(source, student);
		}
	}

	public static class CancelEvent extends NewStudentFormEvent {
		CancelEvent(NewStudentForm source, Student student) {
			super(source, student);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}

}
