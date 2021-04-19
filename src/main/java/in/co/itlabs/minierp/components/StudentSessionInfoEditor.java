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
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Program;
import in.co.itlabs.minierp.entities.StudentSessionInfo;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.util.Editor;
import in.co.itlabs.minierp.util.ScholarshipStatus;
import in.co.itlabs.minierp.util.Semester;
import in.co.itlabs.minierp.util.SemesterStatus;

public class StudentSessionInfoEditor extends VerticalLayout implements Editor {

	// ui
	private TextField sessionField;
	private ComboBox<Program> programCombo;
	private ComboBox<Semester> semesterCombo;
	private ComboBox<SemesterStatus> semesterStatusCombo;

	private Checkbox hostelCheck;

	private Checkbox scholarshipCheck;
	private TextField scholarshipFormNoField;
	private BigDecimalField scholarshipAmountField;
	private ComboBox<ScholarshipStatus> collegeScholarshipStatusCombo;
	private ComboBox<ScholarshipStatus> dswoScholarshipStatusCombo;

	private Tabs tabs;
	private Tab hostelTab;
	private Tab scholarshipTab;
	private Tab marksTab;

	private VerticalLayout content;

	private VerticalLayout hostelContent;
	private VerticalLayout scholarshipContent;
	private VerticalLayout marksContent;

	private Button saveButton;
	private Button cancelButton;
	private Button deleteButton;

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

		hostelCheck = new Checkbox();
		configureHostelCheck();

		scholarshipCheck = new Checkbox();
		configureScholarshipCheck();

		scholarshipFormNoField = new TextField();
		configureScholarshipFormNoField();

		scholarshipAmountField = new BigDecimalField();
		configureScholarshipAmountField();

		collegeScholarshipStatusCombo = new ComboBox<ScholarshipStatus>();
		configureCollegeScholarshipStatusCombo();

		dswoScholarshipStatusCombo = new ComboBox<ScholarshipStatus>();
		configureDswoScholarshipStatusCombo();

		binder = new Binder<>(StudentSessionInfo.class);

		binder.forField(programCombo).asRequired("Program can not be blank").bind("program");
		binder.forField(semesterCombo).bind("semester");
		binder.forField(semesterStatusCombo).bind("semesterStatus");
		binder.forField(hostelCheck).bind("hostel");
		binder.forField(scholarshipCheck).bind("scholarship");
		binder.forField(scholarshipFormNoField).bind("scholarshipFormNo");
		binder.forField(scholarshipAmountField).bind("scholarshipAmount");
		binder.forField(collegeScholarshipStatusCombo).bind("collegeScholarshipStatus");
		binder.forField(dswoScholarshipStatusCombo).bind("dswoScholarshipStatus");

		saveButton = new Button("Save", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());
		deleteButton = new Button("Delete", VaadinIcon.TRASH.create());

		HorizontalLayout buttonBar = buildActionBar();
		buttonBar.setWidthFull();

		FlexLayout sessionFlex = new FlexLayout();
		configureFlex(sessionFlex);

		hostelTab = new Tab("Hostel");
		scholarshipTab = new Tab("Scholarship");
		marksTab = new Tab("Marks");

		tabs = new Tabs();

		content = new VerticalLayout();

		hostelContent = new VerticalLayout();
		buildHostelContent(hostelContent);

		scholarshipContent = new VerticalLayout();
		buildScholarshipContent(scholarshipContent);

		marksContent = new VerticalLayout();
		buildMarksContent(marksContent);

		configureTabs();

		sessionFlex.add(sessionField, programCombo, semesterCombo, semesterStatusCombo);

		add(buttonBar, sessionFlex, tabs, content);

		tabs.setSelectedTab(null);
		tabs.setSelectedTab(hostelTab);
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

	private void configureHostelCheck() {
		hostelCheck.setLabel("Applied for hostel");
		hostelCheck.setWidth("200px");
	}

	private void configureScholarshipCheck() {
		scholarshipCheck.setLabel("Applied for scholarship");
		scholarshipCheck.setWidth("2000px");
	}

	private void configureScholarshipFormNoField() {
		scholarshipFormNoField.setLabel("Scholarship form no");
		scholarshipFormNoField.setWidth("150px");
	}

	private void configureScholarshipAmountField() {
		scholarshipAmountField.setLabel("Scholarship amount");
		scholarshipAmountField.setWidth("150px");
	}

	private void configureCollegeScholarshipStatusCombo() {
		collegeScholarshipStatusCombo.setWidth("150px");
		collegeScholarshipStatusCombo.setLabel("Status at college");
		collegeScholarshipStatusCombo.setItems(ScholarshipStatus.values());
	}

	private void configureDswoScholarshipStatusCombo() {
		dswoScholarshipStatusCombo.setWidth("150px");
		dswoScholarshipStatusCombo.setLabel("Status at DSWO");
		dswoScholarshipStatusCombo.setItems(ScholarshipStatus.values());
	}

	private void configureTabs() {
		content.setPadding(false);
		content.setSpacing(false);

		tabs.add(hostelTab, scholarshipTab, marksTab);

		tabs.addSelectedChangeListener(event -> {
			content.removeAll();

			Tab tab = event.getSelectedTab();

			if (tab == hostelTab) {
				content.add(hostelContent);

			} else if (tab == scholarshipTab) {
				content.add(scholarshipContent);

			} else if (tab == marksTab) {
				content.add(marksContent);

			}
		});
	}

	private void buildHostelContent(VerticalLayout root) {
		root.add(hostelCheck);
	}

	private void buildScholarshipContent(VerticalLayout root) {
		FlexLayout flex = new FlexLayout();
		configureFlex(flex);

		flex.add(scholarshipFormNoField, scholarshipAmountField, collegeScholarshipStatusCombo,
				dswoScholarshipStatusCombo);

		root.add(scholarshipCheck, flex);
	}

	private void buildMarksContent(VerticalLayout root) {
		Div div = new Div();
		div.setText("Marks related info");
		root.add(div);
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
				binder.getBean().setProgramId(binder.getBean().getProgram().getId());
				fireEvent(new SaveEvent(this, binder.getBean()));
			}
		});

		cancelButton.addClickListener(e -> {
			fireEvent(new CancelEvent(this, binder.getBean()));
		});

		deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		deleteButton.addClickListener(e -> {

		});

		Span blank = new Span();

		root.add(saveButton, cancelButton, blank, deleteButton);
		root.expand(blank);

		return root;
	}

	@Override
	public void setEditable(boolean editable) {

		saveButton.setVisible(editable);
		cancelButton.setVisible(editable);
		deleteButton.setVisible(editable);

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