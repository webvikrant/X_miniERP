package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import in.co.itlabs.minierp.entities.Qualification;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.QualificationService;
import in.co.itlabs.minierp.services.StudentService;

@UIScoped
public class StudentQualificationDetails extends VerticalLayout {

	@Inject
	private StudentService studentService;

	@Inject
	private QualificationService qualificationService;

	@Inject
	private QualificationEditor qualificationEditor;

	private TextField interPhysicsPercentField;
	private TextField interChemistryPercentField;
	private TextField interMathematicsPercentField;
	private TextField interBiologyPercentField;
	private TextField interEnglishPercentField;

	private TextField pcmPercentField;
	private TextField pcbPercentField;

	private Button createQualificationButton;
	private Grid<Qualification> grid;

	private int studentId;
	private Student student;

	private Qualification qualification;

	private Dialog dialog;

	@PostConstruct
	public void init() {

		interPhysicsPercentField = new TextField("Physics (%)");
		configureTextField(interPhysicsPercentField);

		interChemistryPercentField = new TextField("Chemistry (%)");
		configureTextField(interChemistryPercentField);

		interMathematicsPercentField = new TextField("Math (%)");
		configureTextField(interMathematicsPercentField);

		interBiologyPercentField = new TextField("Biology (%)");
		configureTextField(interBiologyPercentField);

		interEnglishPercentField = new TextField("English (%)");
		configureTextField(interEnglishPercentField);

		pcmPercentField = new TextField("PCM (%)");
		configureTextField(pcmPercentField);

		pcbPercentField = new TextField("PCB (%)");
		configureTextField(pcbPercentField);

		createQualificationButton = new Button("Add", VaadinIcon.PLUS.create());
		configureCreateQualificationButton();

		grid = new Grid<>(Qualification.class);
		configureGrid();

		FlexLayout flex1 = new FlexLayout();
		configureFlex(flex1);

		FlexLayout flex2 = new FlexLayout();
		configureFlex(flex2);

		FlexLayout flex3 = new FlexLayout();
		flex3.setWidthFull();
		flex3.setJustifyContentMode(JustifyContentMode.END);

		flex2.add(interPhysicsPercentField, interChemistryPercentField, interMathematicsPercentField,
				interBiologyPercentField, interEnglishPercentField, pcmPercentField, pcbPercentField);

		flex3.add(createQualificationButton);

		add(flex1, flex2, flex3, grid);

		// dialog related

		qualificationEditor.addListener(QualificationEditor.SaveEvent.class,
				this::handleSaveAcademicQualificationEvent);

		qualificationEditor.addListener(QualificationEditor.CancelEvent.class,
				this::handleCloseAcademicQualificationEvent);

		qualification = new Qualification();
		dialog = new Dialog();
		configureDialog();
	}

	private void configureTextField(TextField textField) {
		textField.setWidth("100px");
		textField.setReadOnly(true);
	}

	private void configureFlex(FlexLayout flexLayout) {
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.getElement().getStyle().set("padding", "8px");
		flexLayout.getElement().getStyle().set("gap", "8px");
	}

	public void setStudentId(int id) {
		this.studentId = id;
		if (id == 0) {
			StudentQualificationDetails.this.setVisible(false);
		} else {
			StudentQualificationDetails.this.setVisible(true);
			reload();
		}
	}

	private void configureGrid() {
		grid.setHeight("200px");
		grid.removeAllColumns();

		grid.addComponentColumn(item -> {
			Button button = new Button("Edit", VaadinIcon.PENCIL.create());
			button.addThemeVariants(ButtonVariant.LUMO_SMALL);
			return button;
		}).setHeader("Edit").setFrozen(true);

		grid.addColumn(aq -> {
			return aq.getLevel().toString();
		}).setHeader("Level").setFrozen(true);

		grid.addColumn(aq -> {
			return aq.getExam().getName();
		}).setHeader("Exam").setFrozen(true);

		grid.addColumn("year").setFrozen(true);

		grid.addColumn(aq -> {
			return aq.getBoard().getName();
		}).setHeader("Board/University");

		grid.addColumn("rollNo");

		grid.addColumn(aq -> {
			return aq.getSchool().getName();
		}).setHeader("School");

		grid.addColumn("obtainedMarks").setHeader("MO");
		grid.addColumn("maximumMarks").setHeader("MM");
		grid.addColumn("percentMarks").setHeader("%");

		grid.getColumns().forEach(col -> col.setAutoWidth(true));

		reloadGrid();
	}

	private void reloadGrid() {
		grid.setItems(qualificationService.getQualifications(studentId));
	}

	private void configureCreateQualificationButton() {
		createQualificationButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		createQualificationButton.addClickListener(e -> {
			dialog.removeAll();
			dialog.add(qualificationEditor);
			dialog.open();
			qualification.setStudentId(studentId);
			qualificationEditor.setAcademicQualification(qualification);
		});
	}

	private void configureDialog() {
		// TODO Auto-generated method stub
		dialog.setWidth("400px");
		dialog.setModal(true);
		dialog.setDraggable(true);
	}

	private void reload() {
		student = studentService.getStudentById(studentId);
		reloadGrid();
	}

	public void handleSaveAcademicQualificationEvent(QualificationEditor.SaveEvent event) {
		Qualification academicQualification = event.getAcademicQualification();

		if (academicQualification.getId() == 0) {
// 		create new
			int id = qualificationService.createQualification(event.getAcademicQualification());
			if (id > 0) {
				Notification.show("Qualification added successfully", 3000, Position.TOP_CENTER);
				academicQualification.clear();
				academicQualification.setStudentId(studentId);
				qualificationEditor.setAcademicQualification(academicQualification);
				reloadGrid();
			}
		} else {
// 		update existing
			boolean success = false;
//			academicService.updateAcademicQualification(event.getAcademicQualification());
		}
	}

	public void handleCloseAcademicQualificationEvent(QualificationEditor.CancelEvent event) {
		dialog.close();
	}

}
