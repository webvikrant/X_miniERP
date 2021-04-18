package in.co.itlabs.minierp.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;

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

	private int studentId;

	private Qualification qualification;

	private Dialog dialog;

	private final List<String> messages = new ArrayList<String>();

	@PostConstruct
	public void init() {

		editCheck = new Checkbox("Edit");

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

		createQualificationButton = new Button("Add", VaadinIcon.PLUS.create());
		configureCreateQualificationButton();

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

		add(editCheck, buttonBar, flex1, flex2, createQualificationButton, grid);
		
		setAlignSelf(Alignment.CENTER, buttonBar);

		setAlignSelf(Alignment.END, createQualificationButton);

		reload();
		// dialog related

		qualificationEditor.addListener(QualificationEditor.SaveEvent.class, this::handleSaveQualificationEvent);

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

	private void configureNumberField(NumberField numberField) {
		numberField.setWidth("100px");
		numberField.setReadOnly(true);
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
				} else {
					Notification.show(messages.toString(), 5000, Position.TOP_CENTER);
				}
			}
		});

		cancelButton.addClickListener(e -> {
//			fireEvent(new CancelEvent(this, binder.getBean()));
		});

		root.add(saveButton, cancelButton);
	}

	public void setStudentId(int id) {
		this.studentId = id;
		reload();
	}

	private void reload() {
		Student student = studentService.getStudentById(studentId);
		binder.setBean(student);
		reloadGrid();
	}

	public void handleSaveQualificationEvent(QualificationEditor.SaveEvent event) {
		Qualification qualification = event.getQualification();

		if (qualification.getId() == 0) {
// 		create new
			int id = qualificationService.createQualification(event.getQualification());
			if (id > 0) {
				Notification.show("Qualification added successfully", 3000, Position.TOP_CENTER);
				qualification.clear();
				qualification.setStudentId(studentId);
				qualificationEditor.setAcademicQualification(qualification);
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