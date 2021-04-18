package in.co.itlabs.minierp.components;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.data.binder.Binder;

import in.co.itlabs.minierp.entities.Program;
import in.co.itlabs.minierp.entities.Session;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.entities.StudentSessionInfo;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.StudentService;
import in.co.itlabs.minierp.util.Semester;

public class StudentSessionDetails extends VerticalLayout {

	// ui

	private ComboBox<Session> sessionCombo;
	private ComboBox<Semester> semesterCombo;
	private ComboBox<Program> programCombo;

	private Grid<StudentSessionInfo> grid = new Grid<>(StudentSessionInfo.class);
	private final Div resultCount = new Div();

	private Checkbox editCheck;
	private Button saveButton;
	private Button cancelButton;

	private Binder<StudentSessionInfo> binder;
	// non-ui

	private StudentService studentService;
	private AcademicService academicService;

	private int studentId;

	public StudentSessionDetails(StudentService stduentService, AcademicService academicService) {
		this.studentService = stduentService;
		this.academicService = academicService;

		editCheck = new Checkbox("Edit");

		saveButton = new Button("Save", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		sessionCombo = new ComboBox<Session>();
		configureSessionCombo();

		semesterCombo = new ComboBox<Semester>();
		configureSemesterCombo();

		programCombo = new ComboBox<Program>();
		configureProgramCombo();

		resultCount.addClassName("small-text");

		configureGrid();

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);

		FlexLayout flex1 = new FlexLayout();
		configureFlex(flex1);

		FlexLayout flex2 = new FlexLayout();
		configureFlex(flex2);

		flex1.add(sessionCombo, semesterCombo, programCombo);

		add(editCheck, buttonBar, flex1, flex2, grid);
		setAlignSelf(Alignment.CENTER, buttonBar);
		
		reload();
	}

	private void configureFlex(FlexLayout flexLayout) {
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setAlignItems(Alignment.END);
		flexLayout.getElement().getStyle().set("padding", "8px");
		flexLayout.getElement().getStyle().set("gap", "12px");
	}

	private void configureSessionCombo() {
		sessionCombo.setWidth("200px");
		sessionCombo.setLabel("Current session");
		sessionCombo.setItemLabelGenerator(session -> {
			return session.getName();
		});
		sessionCombo.setItems(academicService.getAllSessions());
	}

	private void configureSemesterCombo() {
		semesterCombo.setWidth("200px");
		semesterCombo.setLabel("Current semester");
		semesterCombo.setItems(Semester.values());
	}

	private void configureProgramCombo() {
		programCombo.setWidth("200px");
		programCombo.setLabel("Current program");
		programCombo.setItemLabelGenerator(program -> {
			return program.getName();
		});
		programCombo.setItems(academicService.getAllPrograms());
	}

	private void configureGrid() {
		grid.setHeightByRows(true);
		grid.removeAllColumns();

		grid.addColumn("id").setHeader("Id").setWidth("50px");

	}

	private void buildButtonBar(HorizontalLayout root) {
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {

				StudentSessionInfo sessionInfo = binder.getBean();

//				boolean success = studentService.updateStudentPersonalDetails(messages, sessionInfo);
//				if (success) {
//					Notification.show("Personal details saved successfully", 3000, Position.TOP_CENTER);
//					setStudentId(sessionInfo.getId());
//				} else {
//					Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
//				}
			}
		});

		cancelButton.addClickListener(e -> {
//			fireEvent(new CancelEvent(this, binder.getBean()));
		});

		root.add(saveButton, cancelButton);
	}

	public void reload() {
		List<StudentSessionInfo> sessionInfos = studentService.getAllSessionInfos(studentId);
		resultCount.setText("Record(s) found: " + sessionInfos.size());
		grid.setItems(sessionInfos);
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
		Student student = studentService.getStudentById(studentId);
		StudentSessionInfo sessionInfo = new StudentSessionInfo();
		
		sessionInfo.setStudentId(student.getId());
		sessionInfo.setCollegeScholarshipStatus(null);
		
		reload();
	}

}
