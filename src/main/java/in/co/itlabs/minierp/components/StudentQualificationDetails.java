package in.co.itlabs.minierp.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;

import in.co.itlabs.minierp.entities.Qualification;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.QualificationService;
import in.co.itlabs.minierp.services.StudentService;
import in.co.itlabs.minierp.util.Editor;

public class StudentQualificationDetails extends VerticalLayout implements Editor {

	// ui

	private Checkbox editCheck;

	private TextField interPhysicsPercentField;
	private TextField interChemistryPercentField;
	private TextField interMathematicsPercentField;
	private TextField interBiologyPercentField;
	private BigDecimalField interEnglishPercentField;

	private TextField pcmPercentField;
	private TextField pcbPercentField;

	private Button createQualificationButton;

	private Grid<Qualification> grid;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Student> binder;

	private Dialog dialog;
	private QualificationEditor qualificationEditor;

	// non-ui

	private StudentService studentService;
	private QualificationService qualificationService;

	private Qualification qualification;
	private final List<String> messages = new ArrayList<String>();

	private Student student;

	public StudentQualificationDetails(StudentService studentService, QualificationService qualificationService,
			AcademicService academicService) {
		this.studentService = studentService;
		this.qualificationService = qualificationService;

		editCheck = new Checkbox("Edit");
		configureEditCheck();

		interEnglishPercentField = new BigDecimalField("English (%)");
		interEnglishPercentField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);

		interPhysicsPercentField = new TextField("Physics (%)");
		configureTextField(interPhysicsPercentField);

		interChemistryPercentField = new TextField("Chemistry (%)");
		configureTextField(interChemistryPercentField);

		interMathematicsPercentField = new TextField("Math (%)");
		configureTextField(interMathematicsPercentField);

		interBiologyPercentField = new TextField("Biology (%)");
		configureTextField(interBiologyPercentField);

		pcmPercentField = new TextField("PCM (%)");
		configureTextField(pcmPercentField);

		pcbPercentField = new TextField("PCB (%)");
		configureTextField(pcbPercentField);

		createQualificationButton = new Button("Add Qualification", VaadinIcon.PLUS.create());
		HorizontalLayout menuBar = new HorizontalLayout();
		menuBar.setWidthFull();
		buildMenuBar(menuBar);

		grid = new Grid<>(Qualification.class);
		configureGrid();

		saveButton = new Button("Save", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		binder = new Binder<>(Student.class);

		binder.forField(interEnglishPercentField).bind("interEnglishPercent");

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);

		FlexLayout flex1 = new FlexLayout();
		configureFlex(flex1);

		FlexLayout flex2 = new FlexLayout();
		configureFlex(flex2);

		FlexLayout flex3 = new FlexLayout();
		configureFlex(flex3);

		flex1.add(interEnglishPercentField, interPhysicsPercentField, interChemistryPercentField,
				interMathematicsPercentField, interBiologyPercentField);

		flex2.add(pcmPercentField, pcbPercentField);

		add(editCheck, flex1, flex2, buttonBar, menuBar, grid);

		// dialog related
		qualificationEditor = new QualificationEditor(academicService);
		qualificationEditor.addListener(QualificationEditor.SaveEvent.class, this::handleSaveQualificationEvent);

		qualificationEditor.addListener(QualificationEditor.CancelEvent.class,
				this::handleCloseAcademicQualificationEvent);

		qualification = new Qualification();
		dialog = new Dialog();
		configureDialog();
	}

	private void configureEditCheck() {
		editCheck.addValueChangeListener(e -> {
			setEditable(e.getValue());
		});
	}

	private void configureTextField(TextField textField) {
		textField.setWidth("100px");
	}

	private void configureFlex(FlexLayout flexLayout) {
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.getElement().getStyle().set("padding", "8px");
		flexLayout.getElement().getStyle().set("gap", "8px");
	}

	private void configureGrid() {
		grid.setHeight("200px");
		grid.removeAllColumns();

		grid.addComponentColumn(item -> {
			Button button = new Button("Edit", VaadinIcon.PENCIL.create());
			button.addThemeVariants(ButtonVariant.LUMO_SMALL);
			return button;
		}).setHeader("Edit").setWidth("100px").setFrozen(true);

		grid.addColumn(aq -> {
			return aq.getLevel().toString();
		}).setHeader("Level").setWidth("100px").setFrozen(true);

		grid.addColumn(aq -> {
			return aq.getExam().getName();
		}).setHeader("Exam").setWidth("100px").setFrozen(true);

		grid.addColumn("year").setWidth("80px").setFrozen(true);

		grid.addColumn(aq -> {
			return aq.getBoard().getName();
		}).setHeader("Board/University").setWidth("100px");

		grid.addColumn("rollNo").setWidth("100px");

		grid.addColumn(aq -> {
			return aq.getSchool().getName();
		}).setHeader("School").setWidth("100px");

		grid.addColumn("obtainedMarks").setHeader("MO").setWidth("80px");
		grid.addColumn("maximumMarks").setHeader("MM").setWidth("80px");
		grid.addColumn("percentMarks").setHeader("%").setWidth("80px");

	}

	private void buildMenuBar(HorizontalLayout root) {
		root.setAlignItems(Alignment.END);
		
		Div gridLabel = new Div();
		gridLabel.addClassName("small-text");
		gridLabel.setText("Academic Qualifications");
		
		createQualificationButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		createQualificationButton.addClickListener(e -> {
			dialog.removeAll();
			dialog.add(qualificationEditor);
			dialog.open();
			qualification.setStudentId(student.getId());
			qualificationEditor.setAcademicQualification(qualification);
		});
		
		Span blank = new Span();
		root.add(gridLabel, blank, createQualificationButton);
		root.expand(blank);
	}

	private void configureDialog() {
		// TODO Auto-generated method stub
		dialog.setWidth("400px");
		dialog.setModal(true);
		dialog.setDraggable(true);
	}

	private void buildButtonBar(HorizontalLayout root) {
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
//				fireEvent(new SaveEvent(this, binder.getBean()));
				Student student = binder.getBean();
				messages.clear();
				boolean success = studentService.updateStudentInterMarks(messages, student);
				if (success) {
					Notification.show("Inter marks updated successfully", 5000, Position.TOP_CENTER);
					setStudentId(student.getId());
				} else {
					Notification.show(messages.toString(), 5000, Position.TOP_CENTER);
				}
			}
		});

		cancelButton.addClickListener(e -> {
//			fireEvent(new CancelEvent(this, binder.getBean()));
			setStudentId(student.getId());
		});

		root.add(saveButton, cancelButton);
	}

	public void setStudentId(int studentId) {
		student = studentService.getStudentById(studentId);
		binder.setBean(student);
		reloadGrid();

		editCheck.setValue(true);
		editCheck.setValue(false);
	}

	private void reloadGrid() {
		grid.setItems(qualificationService.getQualifications(student.getId()));
	}

	public void handleSaveQualificationEvent(QualificationEditor.SaveEvent event) {
		Qualification qualification = event.getQualification();

		if (qualification.getId() == 0) {
// 		create new
			int id = qualificationService.createQualification(event.getQualification());
			if (id > 0) {
				Notification.show("Qualification added successfully", 3000, Position.TOP_CENTER);
				qualification.clear();
				qualification.setStudentId(student.getId());
				qualificationEditor.setAcademicQualification(qualification);
				reloadGrid();
			}
		} else {
// 		update existing
//			boolean success = false;
//			academicService.updateAcademicQualification(event.getAcademicQualification());
		}
	}

	public void handleCloseAcademicQualificationEvent(QualificationEditor.CancelEvent event) {
		dialog.close();
	}

	@Override
	public void setEditable(boolean enabled) {
		interPhysicsPercentField.setReadOnly(!enabled);
		interChemistryPercentField.setReadOnly(!enabled);
		interMathematicsPercentField.setReadOnly(!enabled);
		interBiologyPercentField.setReadOnly(!enabled);
		interEnglishPercentField.setReadOnly(!enabled);

		pcmPercentField.setReadOnly(!enabled);
		pcbPercentField.setReadOnly(!enabled);

		saveButton.setVisible(enabled);
		cancelButton.setVisible(enabled);
	}

}
