package in.co.itlabs.minierp.views;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.components.NewStudentComponent;
import in.co.itlabs.minierp.components.StudentFilterComponent;
import in.co.itlabs.minierp.components.StudentsExporter;
import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.layouts.AppLayout;
import in.co.itlabs.minierp.services.StudentService;
import in.co.itlabs.minierp.util.StudentReport;
import in.co.itlabs.minierp.util.StudentFilterParams;
import in.co.itlabs.minierp.util.StudentFilterParams.FilterType;

@PageTitle(value = "Students")
@Route(value = "students", layout = AppLayout.class)
public class StudentsView extends VerticalLayout {

	@Inject
	private StudentService studentService;

	@Inject
	private StudentFilterComponent filterComponent;

	@Inject
	private NewStudentComponent newStudentComponent;

	@Inject
	private StudentsExporter exporter;

	private int collegeId = 0;

	private Grid<Student> grid = new Grid<>(Student.class);
	private final Div resultCount = new Div();

	private StudentFilterParams filterParams = new StudentFilterParams();

	private Dialog dialog = new Dialog();

	@PostConstruct
	public void init() {

		setPadding(false);
		setAlignItems(Alignment.CENTER);

		College college = VaadinSession.getCurrent().getAttribute(College.class);
		if (college != null) {
			collegeId = college.getId();
		}

		Div titleDiv = new Div();
		buildTitle(titleDiv);

		dialog.setModal(true);
		dialog.setDraggable(true);

		newStudentComponent.setStudent(new Student());
		newStudentComponent.addListener(NewStudentComponent.SaveEvent.class, this::handleNewStudentSaveEvent);
		newStudentComponent.addListener(NewStudentComponent.CancelEvent.class, this::handleNewStudentCancelEvent);

		filterParams.setFilterType(FilterType.BASIC);

		filterComponent.setFilterParams(filterParams);
		filterComponent.addListener(StudentFilterComponent.StudentFilterEvent.class, this::handleFilterEvent);
		resultCount.addClassName("small-text");

		configureGrid();

		HorizontalLayout toolBar = new HorizontalLayout();
		buildToolBar(toolBar);
		toolBar.setWidthFull();

		VerticalLayout main = new VerticalLayout();
		main.add(toolBar, grid);

		SplitLayout splitLayout = new SplitLayout();
		splitLayout.setWidthFull();
		splitLayout.setSplitterPosition(25);
		splitLayout.addToPrimary(filterComponent);
		splitLayout.addToSecondary(main);

		add(titleDiv, splitLayout);

		reload();
	}

	private void configureGrid() {
		grid.removeAllColumns();

		grid.addColumn("prnNo").setHeader("PRN No").setWidth("100px");
		grid.addColumn("admissionId").setHeader("Admission No").setWidth("100px");
		grid.addColumn("name").setHeader("Name").setWidth("150px");

		grid.addComponentColumn(student -> {
			Button button = new Button("More", VaadinIcon.ARROW_FORWARD.create());
			button.addThemeVariants(ButtonVariant.LUMO_SMALL);
			button.addClickListener(e -> {
				VaadinSession.getCurrent().setAttribute(Student.class, student);
				UI.getCurrent().navigate("student-details");
			});

			return button;
		}).setHeader("More");

	}

	private void buildToolBar(HorizontalLayout root) {
		root.setAlignItems(Alignment.END);

		Button exportButton = new Button("Export", VaadinIcon.ARROW_FORWARD.create());
		exportButton.addClickListener(e -> {
			dialog.setWidth("600px");
			dialog.removeAll();
			dialog.open();
			dialog.add(exporter);

		});

		Select<StudentReport> reportSelect = new Select<>();
		reportSelect.setWidth("250px");
		reportSelect.setLabel("Generate report");
		reportSelect.setPlaceholder("Select a report type");
		reportSelect.setItems(StudentReport.values());

		Button createButton = new Button("New", VaadinIcon.PLUS.create());
		createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		createButton.addClickListener(e -> {
			dialog.setWidth("400px");
			dialog.removeAll();
			dialog.open();
			dialog.add(newStudentComponent);
		});

		Button importButton = new Button("Import", VaadinIcon.ARROW_BACKWARD.create());
		importButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

		root.add(resultCount, exportButton, reportSelect, createButton, importButton);
		root.expand(resultCount);

	}

	private void buildTitle(Div root) {
		root.addClassName("view-title");
		root.add("Students");
	}

	public void handleFilterEvent(StudentFilterComponent.StudentFilterEvent event) {
		filterParams = event.getFilterParams();
		exporter.setStudentFilterParams(filterParams);
		reload();
	}

	public void handleNewStudentSaveEvent(NewStudentComponent.SaveEvent event) {
		List<String> messages = new ArrayList<String>();
		Student student = event.getStudent();
		int studentId = studentService.createStudent(messages, student);
		if (studentId > 0) {
			Notification.show("Student created successfully", 3000, Position.TOP_CENTER);
			reload();
			student.clear();
			newStudentComponent.setStudent(student);
		} else {
			Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
		}
	}

	public void handleNewStudentCancelEvent(NewStudentComponent.CancelEvent event) {
		dialog.close();
	}

	public void reload() {
		List<Student> students = studentService.getStudents(collegeId, filterParams);
		resultCount.setText("Record(s) found: " + students.size());
		grid.setItems(students);
	}
}
