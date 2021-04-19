package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Program;
import in.co.itlabs.minierp.entities.StudentSessionInfo;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.util.Editor;
import in.co.itlabs.minierp.util.Semester;
import in.co.itlabs.minierp.util.SemesterStatus;

public class StudentSessionInfoEditor extends VerticalLayout implements Editor {

	// ui
	private TextField sessionField;
	private ComboBox<Program> programCombo;
	private ComboBox<Semester> semesterCombo;
	private ComboBox<SemesterStatus> semesterStatusCombo;

	private Button saveButton;
	private Button cancelButton;

	private Binder<StudentSessionInfo> binder;

	// non-ui

	private AcademicService academicService;

	public StudentSessionInfoEditor(AcademicService academicService) {
		this.academicService = academicService;

		sessionField = new TextField();
		configureSessionField();

		programCombo = new ComboBox<Program>();
		configureProgramCombo();

		semesterCombo = new ComboBox<Semester>();
		configureSemesterCombo();

		semesterStatusCombo = new ComboBox<SemesterStatus>();
		configureSemesterStatusCombo();

		binder = new Binder<>(StudentSessionInfo.class);

		binder.forField(programCombo).bind("program");
		binder.forField(semesterCombo).bind("semester");
		binder.forField(semesterStatusCombo).bind("semesterStatus");

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout buttonBar = buildActionBar();

		FlexLayout flex1 = new FlexLayout();
		configureFlex(flex1);

		FlexLayout flex2 = new FlexLayout();
		configureFlex(flex2);

		FlexLayout flex3 = new FlexLayout();
		configureFlex(flex3);

		flex1.add(sessionField, programCombo, semesterCombo, semesterStatusCombo);

		add(flex1, flex2, flex3, buttonBar);

		setAlignSelf(Alignment.CENTER, buttonBar);

	}

	private void configureSessionField() {
		sessionField.setLabel("Session");
		sessionField.setWidth("150px");
		sessionField.setReadOnly(true);
	}

	private void configureProgramCombo() {
		programCombo.setLabel("Program");
		programCombo.setWidth("150px");
		programCombo.setItemLabelGenerator(program -> {
			return program.getName();
		});

		programCombo.setItems(academicService.getAllPrograms());
	}

	private void configureSemesterCombo() {
		semesterCombo.setLabel("Semester");
		semesterCombo.setWidth("150px");
		semesterCombo.setItems(Semester.values());
	}

	private void configureSemesterStatusCombo() {
		semesterStatusCombo.setLabel("Status");
		semesterStatusCombo.setWidth("150px");
		semesterStatusCombo.setItems(SemesterStatus.values());
	}

	private void configureFlex(FlexLayout flexLayout) {
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setAlignItems(Alignment.END);
		flexLayout.getElement().getStyle().set("padding", "8px");
		flexLayout.getElement().getStyle().set("gap", "12px");
	}

	public void setStudentSessionInfo(StudentSessionInfo sessionInfo) {
		binder.setBean(sessionInfo);

		sessionField.setValue(sessionInfo.getSession().getName());
	}

	private HorizontalLayout buildActionBar() {
		HorizontalLayout root = new HorizontalLayout();

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

		return root;
	}

	@Override
	public void setEditable(boolean editable) {

		saveButton.setVisible(editable);
		cancelButton.setVisible(editable);

	}

	public static abstract class StudentSessionInfoEditorEvent extends ComponentEvent<StudentSessionInfoEditor> {
		private StudentSessionInfo sessionInfo;

		protected StudentSessionInfoEditorEvent(StudentSessionInfoEditor source, StudentSessionInfo sessionInfo) {

			super(source, false);
			this.sessionInfo = sessionInfo;
		}

		public StudentSessionInfo getStudentSessionInfo() {
			return sessionInfo;
		}
	}

	public static class SaveEvent extends StudentSessionInfoEditorEvent {
		SaveEvent(StudentSessionInfoEditor source, StudentSessionInfo sessionInfo) {
			super(source, sessionInfo);
		}
	}

	public static class CancelEvent extends StudentSessionInfoEditorEvent {
		CancelEvent(StudentSessionInfoEditor source, StudentSessionInfo sessionInfo) {
			super(source, sessionInfo);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}