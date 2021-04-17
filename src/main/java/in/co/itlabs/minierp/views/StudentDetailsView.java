package in.co.itlabs.minierp.views;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.components.StudentAdmissionDetails;
import in.co.itlabs.minierp.components.StudentContactDetails;
import in.co.itlabs.minierp.components.StudentInfoCard;
import in.co.itlabs.minierp.components.StudentMediaDetails;
import in.co.itlabs.minierp.components.StudentPersonalDetails;
import in.co.itlabs.minierp.components.StudentQualificationDetails;
import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.layouts.AppLayout;
import in.co.itlabs.minierp.services.StudentService;

@PageTitle(value = "Student details")
@Route(value = "student-details", layout = AppLayout.class)
public class StudentDetailsView extends VerticalLayout {

	@Inject
	private StudentService studentService;

	@Inject
	private StudentInfoCard studentInfoCard;

	@Inject
	private StudentPersonalDetails personalDetails;

	@Inject
	private StudentContactDetails contactDetails;

	@Inject
	private StudentAdmissionDetails admissionDetails;
	
	@Inject
	private StudentQualificationDetails qualificationDetails;
	
	@Inject
	private StudentMediaDetails mediaDetails;

	private int collegeId = 0;
//	private int studentId = 0;
	private Tab currentTab = null;

	private ComboBox<Student> studentCombo;
	private SplitLayout splitLayout;

	private Tabs tabs;
	private Tab personalTab;
	private Tab contactTab;
	private Tab admissionTab;
	private Tab qualificationTab;
	private Tab mediaTab;
	private Tab hostelTab;
	private Tab scholarshipTab;
	private Tab marksTab;
	private Tab formatsTab;

	private VerticalLayout content;

	private final List<String> messages = new ArrayList<>();

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

		studentCombo = new ComboBox<Student>();
		configureCombo(studentCombo);

		tabs = new Tabs();
		personalTab = new Tab("Personal");
		contactTab = new Tab("Contact");
		admissionTab = new Tab("Admission");
		qualificationTab = new Tab("Qualification");
		mediaTab = new Tab("Media");
		hostelTab = new Tab("Hostel");
		scholarshipTab = new Tab("Scholarship");
		marksTab = new Tab("Marks");
		formatsTab = new Tab("Formats");

		content = new VerticalLayout();

		personalDetails.addListener(StudentPersonalDetails.SaveEvent.class, this::handlePersonalDetailsSaveEvent);

		configureTabs();

		splitLayout = new SplitLayout();
		configureSplitLayout();

		add(titleDiv, studentCombo, splitLayout);
		splitLayout.setVisible(false);

		// check if SudentsView has put the selected student into the session
		Student student = VaadinSession.getCurrent().getAttribute(Student.class);
		studentCombo.setValue(student);
//		reload();
	}

	private void buildTitle(Div root) {
		root.addClassName("view-title");
		root.add("Student details");
	}

	private void configureCombo(ComboBox<Student> studentCombo) {

		studentCombo.setWidth("300px");
		studentCombo.setPlaceholder("Select a student");
		studentCombo.setItems(studentService.getStudents(collegeId, null));
		studentCombo.setItemLabelGenerator(student -> {
			return student.getAdmissionId() + " " + student.getName();
		});

		studentCombo.addValueChangeListener(event -> {
//			studentId = 0;
//			if (event.getValue() != null) {
//				studentId = event.getValue().getId();
//			}
			VaadinSession.getCurrent().setAttribute(Student.class, event.getValue());
			reload();
		});
	}

	private void reload() {
		Student student = VaadinSession.getCurrent().getAttribute(Student.class);
		if (student == null) {
			splitLayout.setVisible(false);
		} else {
			splitLayout.setVisible(true);
			studentInfoCard.setStudentId(student.getId());

			tabs.setSelectedTab(null);
			if (currentTab == null) {
				currentTab = personalTab;
			}
			tabs.setSelectedTab(currentTab);
		}
	}

	private void configureSplitLayout() {

		splitLayout.setSplitterPosition(25);
		splitLayout.setSizeFull();
		splitLayout.addToPrimary(studentInfoCard);

		VerticalLayout tabsLayout = new VerticalLayout();
		tabsLayout.setPadding(false);
		tabsLayout.setSpacing(false);
		tabsLayout.add(tabs, content);

		splitLayout.addToSecondary(tabsLayout);
	}

	private void configureTabs() {
		content.setPadding(false);
		content.setSpacing(false);

		tabs.add(personalTab);
		tabs.add(contactTab);
		tabs.add(admissionTab);
		tabs.add(qualificationTab);
		tabs.add(mediaTab);
		tabs.add(hostelTab);
		tabs.add(scholarshipTab);
		tabs.add(marksTab);
		tabs.add(formatsTab);

		tabs.addSelectedChangeListener(event -> {
			content.removeAll();
			Student student = VaadinSession.getCurrent().getAttribute(Student.class);
			Tab tab = event.getSelectedTab();
			if (tab == personalTab) {
				content.add(personalDetails);
				if (student != null) {
					personalDetails.setStudentId(student.getId());
					currentTab = personalTab;
				}

			} else if (tab == contactTab) {
				content.add(contactDetails);
				if (student != null) {
					contactDetails.setStudentId(student.getId());
					currentTab = contactTab;
				}

			} else if (tab == admissionTab) {
				content.add(admissionDetails);
				if (student != null) {
					admissionDetails.setStudentId(student.getId());
					currentTab = admissionTab;
				}

			} else if (tab == qualificationTab) {
				content.add(qualificationDetails);
				if (student != null) {
					qualificationDetails.setStudentId(student.getId());
					currentTab = qualificationTab;
				}

			} else if (tab == mediaTab) {
				content.add(mediaDetails);
				if (student != null) {
					mediaDetails.setStudentId(student.getId());
					currentTab = mediaTab;
				}

			}
		});
	}

	private void handlePersonalDetailsSaveEvent(StudentPersonalDetails.SaveEvent event) {
		Student student = event.getStudent();
		boolean success = studentService.updateStudentPersonalDetails(messages, student);
		if (success) {
			Notification.show("Personal details saved successfully", 3000, Position.TOP_CENTER);
			reload();
		} else {
			Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
		}
	}
}
