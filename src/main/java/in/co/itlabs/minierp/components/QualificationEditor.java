package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Board;
import in.co.itlabs.minierp.entities.Exam;
import in.co.itlabs.minierp.entities.Exam.Level;
import in.co.itlabs.minierp.entities.Qualification;
import in.co.itlabs.minierp.entities.School;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.util.Editor;

public class QualificationEditor extends VerticalLayout implements Editor {

	// ui

	private ComboBox<Level> levelCombo;
	private ComboBox<Exam> examCombo;
	private ComboBox<Board> boardCombo;
	private ComboBox<School> schoolCombo;
	private IntegerField yearField;
	private TextField rollNoField;
	private IntegerField obtainedMarksField;
	private IntegerField maximumMarksField;
	private BigDecimalField percentMarksField;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Qualification> binder;

	// non-ui

	private AcademicService academicService;

	public QualificationEditor(AcademicService academicService) {
		this.academicService = academicService;

		levelCombo = new ComboBox<Level>("Level");
		configureLevelCombo();

		examCombo = new ComboBox<Exam>("Examintaion");
		configureExamCombo();

		boardCombo = new ComboBox<Board>("Board/University");
		configureBoardCombo();

		schoolCombo = new ComboBox<School>("School/College");
		configureSchoolCombo();

		yearField = new IntegerField("Year of Passing");
		rollNoField = new TextField("Roll No");

		obtainedMarksField = new IntegerField("MO");
		configureObtainedMarksField();

		maximumMarksField = new IntegerField("MM");
		configureMaximumMarksField();

		percentMarksField = new BigDecimalField("% Marks");
		configurePercentMarksField();

		binder = new Binder<>(Qualification.class);

		binder.forField(levelCombo).asRequired("Level can not be blank").bind("level");
		binder.forField(examCombo).asRequired("Exam can not be blank").bind("exam");
		binder.forField(boardCombo).asRequired("Board/University can not be blank").bind("board");
		binder.forField(schoolCombo).asRequired("School/College can not be blank").bind("school");

		binder.forField(yearField).asRequired("Year can not be blank").bind("year");
		binder.forField(rollNoField).asRequired("Roll No can not be blank").bind("rollNo");

		binder.forField(obtainedMarksField).asRequired("Marks obtained can not be blank").bind("obtainedMarks");
		binder.forField(maximumMarksField).asRequired("Maximum marks can not be blank").bind("maximumMarks");
		binder.forField(percentMarksField).asRequired("Percent marks can not be blank").bind("percentMarks");

		HorizontalLayout marksBar = buildMarksBar();
		marksBar.setWidthFull();

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout actionBar = buildActionBar();
		actionBar.setWidthFull();

		add(levelCombo, examCombo, boardCombo, schoolCombo, yearField, rollNoField, marksBar, actionBar);

	}

	private void configureObtainedMarksField() {
		obtainedMarksField.setWidth("80px");
		obtainedMarksField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
	}

	private void configureMaximumMarksField() {
		maximumMarksField.setWidth("80px");
		maximumMarksField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
	}

	private void configurePercentMarksField() {
		percentMarksField.setWidth("80px");
		percentMarksField.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
	}

	private void configureLevelCombo() {
		levelCombo.setWidthFull();
		levelCombo.setItemLabelGenerator(level -> {
			return level.name();
		});
		levelCombo.setItems(Level.CLASS_10, Level.CLASS_12, Level.DIPLOMA, Level.DEGREE);
		levelCombo.addValueChangeListener(e -> {
			examCombo.clear();
			if (e.getValue() != null) {
				examCombo.setItems(academicService.getExams(e.getValue()));
			}
		});
	}

	private void configureExamCombo() {
		examCombo.setWidthFull();
		examCombo.setItemLabelGenerator(exam -> {
			return exam.getName();
		});
		examCombo.setItems(academicService.getAllExams());
	}

	private void configureBoardCombo() {
		boardCombo.setWidthFull();
		boardCombo.setItemLabelGenerator(board -> {
			return board.getName();
		});
		boardCombo.setItems(academicService.getAllBoards());
	}

	private void configureSchoolCombo() {
		schoolCombo.setWidthFull();
		schoolCombo.setItemLabelGenerator(school -> {
			return school.getName();
		});
		schoolCombo.setItems(academicService.getAllSchools());
	}

	public void setAcademicQualification(Qualification academicQualification) {
		binder.setBean(academicQualification);
	}

	private HorizontalLayout buildMarksBar() {
		HorizontalLayout root = new HorizontalLayout();
		root.add(obtainedMarksField, maximumMarksField, percentMarksField);
		return root;
	}

	private HorizontalLayout buildActionBar() {
		HorizontalLayout root = new HorizontalLayout();

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {

				binder.getBean().setExamId(binder.getBean().getExam().getId());
				binder.getBean().setBoardId(binder.getBean().getBoard().getId());
				binder.getBean().setSchoolId(binder.getBean().getSchool().getId());

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

	public static abstract class QualificationEvent extends ComponentEvent<QualificationEditor> {
		private Qualification qualification;

		protected QualificationEvent(QualificationEditor source, Qualification qualification) {

			super(source, false);
			this.qualification = qualification;
		}

		public Qualification getQualification() {
			return qualification;
		}
	}

	public static class SaveEvent extends QualificationEvent {
		SaveEvent(QualificationEditor source, Qualification qualification) {
			super(source, qualification);
		}
	}

	public static class CancelEvent extends QualificationEvent {
		CancelEvent(QualificationEditor source, Qualification qualification) {
			super(source, qualification);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}

	@Override
	public void setEditable(boolean enabled) {
		levelCombo.setReadOnly(!enabled);
		examCombo.setReadOnly(!enabled);
		boardCombo.setReadOnly(!enabled);
		schoolCombo.setReadOnly(!enabled);
		yearField.setReadOnly(!enabled);
		rollNoField.setReadOnly(!enabled);
		obtainedMarksField.setReadOnly(!enabled);
		maximumMarksField.setReadOnly(!enabled);
		percentMarksField.setReadOnly(!enabled);

		saveButton.setVisible(enabled);
		cancelButton.setVisible(enabled);
	}
}