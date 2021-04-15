package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Session;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.CollegeService;

@UIScoped
public class NewStudentComponent extends VerticalLayout {

	private Select<College> collegeSelect;
	private Select<Session> sessionSelect;
	private TextField prnNoField;
	private TextField admissionIdField;
	private TextField nameField;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Student> binder;

	@Inject
	private CollegeService collegeService;

	@PostConstruct
	public void init() {

		setPadding(false);

		collegeSelect = new Select<College>();
		configureCollegeSelect();

		sessionSelect = new Select<Session>();
		configureSessionSelect();

		prnNoField = new TextField("PRN No");
		prnNoField.setWidthFull();

		admissionIdField = new TextField("Admission Id");
		admissionIdField.setWidthFull();

		nameField = new TextField("Name");
		nameField.setWidthFull();

		binder = new Binder<>(Student.class);

		binder.forField(collegeSelect).asRequired("College can not be blank").bind("college");
		binder.forField(sessionSelect).asRequired("Session can not be blank").bind("session");
		binder.forField(prnNoField).asRequired("PRN No can not be blank").bind("prnNo");
		binder.forField(admissionIdField).asRequired("Admission Id can not be blank").bind("admissionId");
		binder.forField(nameField).asRequired("Name can not be blank").bind("name");

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout buttonBar = buildActionBar();
		buttonBar.setWidthFull();

		add(collegeSelect, sessionSelect, prnNoField, admissionIdField, nameField, buttonBar);

	}

	private void configureCollegeSelect() {
		collegeSelect.setWidthFull();
		collegeSelect.setItemLabelGenerator(college -> {
			return college.getCode() + " - " + college.getName();
		});
		collegeSelect.setItems(collegeService.getAllColleges());
		collegeSelect.setReadOnly(true);
	}

	private void configureSessionSelect() {
		// TODO Auto-generated method stub
		sessionSelect.setWidthFull();
		sessionSelect.setPlaceholder("Select a session");

		sessionSelect.setItemLabelGenerator(session -> {
			return session.getName();
		});

		sessionSelect.setItems(collegeService.getAllSessions());
	}

	public void setStudent(Student student) {
		College college = VaadinSession.getCurrent().getAttribute(College.class);
		student.setCollege(college);
		binder.setBean(student);
	}

	private HorizontalLayout buildActionBar() {
		HorizontalLayout root = new HorizontalLayout();

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {

				binder.getBean().setCollegeId(binder.getBean().getCollege().getId());
				binder.getBean().setSessionId(binder.getBean().getSession().getId());

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

	public static abstract class StudentEvent extends ComponentEvent<NewStudentComponent> {
		private Student student;

		protected StudentEvent(NewStudentComponent source, Student student) {

			super(source, false);
			this.student = student;
		}

		public Student getStudent() {
			return student;
		}
	}

	public static class SaveEvent extends StudentEvent {
		SaveEvent(NewStudentComponent source, Student student) {
			super(source, student);
		}
	}

	public static class CancelEvent extends StudentEvent {
		CancelEvent(NewStudentComponent source, Student student) {
			super(source, student);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}

}
