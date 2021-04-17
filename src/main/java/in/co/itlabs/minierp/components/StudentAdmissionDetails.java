package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Program;
import in.co.itlabs.minierp.entities.Session;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.StudentService;
import in.co.itlabs.minierp.util.AdmissionCategory;
import in.co.itlabs.minierp.util.AdmissionMode;
import in.co.itlabs.minierp.util.Stage;

@UIScoped
public class StudentAdmissionDetails extends VerticalLayout {

	@Inject
	private AcademicService academicService;

	@Inject
	private StudentService studentService;

	private Checkbox editCheck;

	private ComboBox<Session> sessionCombo;
	private ComboBox<Program> programCombo;
	private ComboBox<Stage> stageCombo;
	private ComboBox<AdmissionCategory> admissionCategoryCombo;

	private RadioButtonGroup<AdmissionMode> admissionModeRadio;

	private Checkbox hostelCheck = new Checkbox("Hostel");

	private TextField prnNofield;
	private TextField admissionIdField;

	private TextField upseeRollNoField;
	private NumberField upseeRankField;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Student> binder;

	@PostConstruct
	public void init() {

		editCheck = new Checkbox("Edit");

		sessionCombo = new ComboBox<Session>();
		configureSessionCombo();

		programCombo = new ComboBox<Program>();
		configureProgramCombo();

		stageCombo = new ComboBox<Stage>();
		configureStageCombo();

		admissionCategoryCombo = new ComboBox<AdmissionCategory>();
		configureAdmissionCategoryCombo();

		admissionModeRadio = new RadioButtonGroup<AdmissionMode>();
		configureAdmissionModeRadio();

		upseeRollNoField = new TextField();
		configureUpseeRollNoField();

		upseeRankField = new NumberField();
		configureUpseeRankField();

		prnNofield = new TextField();
		configurePrnNoField();

		admissionIdField = new TextField();
		configurAdmissionIdfield();

		saveButton = new Button("Save", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		binder = new Binder<>(Student.class);

		binder.forField(sessionCombo).asRequired("Session can not be blank").bind("session");

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);

		FlexLayout flex1 = new FlexLayout();
		configureFlex(flex1);

		FlexLayout flex2 = new FlexLayout();
		configureFlex(flex2);

		FlexLayout flex3 = new FlexLayout();
		configureFlex(flex3);

		FlexLayout flex4 = new FlexLayout();
		configureFlex(flex4);

		FlexLayout flex5 = new FlexLayout();
		configureFlex(flex5);

		FlexLayout flex6 = new FlexLayout();
		configureFlex(flex6);

		flex1.add(prnNofield, admissionIdField, admissionModeRadio);
		flex2.add(sessionCombo, programCombo, stageCombo);
		flex3.add(admissionCategoryCombo, hostelCheck);
		flex4.add(upseeRollNoField, upseeRankField);

		add(editCheck, buttonBar, flex1, flex2, flex3, flex4, flex5, flex6);
		setAlignSelf(Alignment.CENTER, buttonBar);
	}

	private void configureSessionCombo() {
		sessionCombo.setLabel("Session");
		sessionCombo.setWidth("200px");
		sessionCombo.setItemLabelGenerator(session -> {
			return session.getName();
		});
		sessionCombo.setItems(academicService.getAllSessions());
	}

	private void configureProgramCombo() {
		programCombo.setLabel("Program");
		programCombo.setWidth("200px");
		programCombo.setItemLabelGenerator(program -> {
			return program.getName();
		});
		programCombo.setItems(academicService.getAllPrograms());
	}

	private void configureStageCombo() {
		stageCombo.setLabel("Stage");
		stageCombo.setWidth("200px");
		stageCombo.setItems(Stage.values());
	}

	private void configureAdmissionCategoryCombo() {
		admissionCategoryCombo.setLabel("Admission Category");
		admissionCategoryCombo.setWidth("200px");
		admissionCategoryCombo.setItems(AdmissionCategory.values());
	}

	private void configureAdmissionModeRadio() {
		admissionModeRadio.setItems(AdmissionMode.values());
		admissionModeRadio.setValue(AdmissionMode.DIRECT);
	}

	private void configureUpseeRankField() {
		upseeRankField.setLabel("UPSEE Rank");
		upseeRankField.setWidth("200px");
	}

	private void configureUpseeRollNoField() {
		upseeRollNoField.setLabel("UPSEE Roll No");
		upseeRollNoField.setWidth("200px");
	}

	private void configurePrnNoField() {
		prnNofield.setLabel("PRN No");
		prnNofield.setWidth("200px");
	}

	private void configurAdmissionIdfield() {
		admissionIdField.setLabel("Admission Id");
		admissionIdField.setWidth("200px");
	}

	private void configureFlex(FlexLayout flexLayout) {
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setAlignItems(Alignment.END);
		flexLayout.getElement().getStyle().set("padding", "8px");
		flexLayout.getElement().getStyle().set("gap", "12px");
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

		root.add(saveButton, cancelButton);
	}

	public void setStudentId(int studentId) {
		Student student = studentService.getStudentById(studentId);
		binder.setBean(student);
	}

	public void setEditable(boolean editable) {

	}

	public static abstract class StudentAdmissionEvent extends ComponentEvent<StudentAdmissionDetails> {
		private Student student;

		protected StudentAdmissionEvent(StudentAdmissionDetails source, Student student) {

			super(source, false);
			this.student = student;
		}

		public Student getStudent() {
			return student;
		}
	}

	public static class SaveEvent extends StudentAdmissionEvent {
		SaveEvent(StudentAdmissionDetails source, Student student) {
			super(source, student);
		}
	}

	public static class CancelEvent extends StudentAdmissionEvent {
		CancelEvent(StudentAdmissionDetails source, Student student) {
			super(source, student);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}

}
