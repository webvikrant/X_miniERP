package in.co.itlabs.minierp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
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
import in.co.itlabs.minierp.components.StudentSessionDetails;
import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.layouts.AppLayout;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.ContactService;
import in.co.itlabs.minierp.services.MediaService;
import in.co.itlabs.minierp.services.QualificationService;
import in.co.itlabs.minierp.services.StudentService;

@PageTitle(value = "Student details")
@Route(value = "student-details", layout = AppLayout.class)
public class StudentDetailsView extends VerticalLayout {

	// ui

	private StudentInfoCard studentInfoCard;

	private StudentPersonalDetails personalDetails;
	private StudentContactDetails contactDetails;
	private StudentAdmissionDetails admissionDetails;
	private StudentQualificationDetails qualificationDetails;
	private StudentMediaDetails mediaDetails;
	private StudentSessionDetails sessionDetails;

	private ComboBox<Student> studentCombo;
	private SplitLayout splitLayout;

	private Tabs tabs;
	private Tab personalTab;
	private Tab contactTab;
	private Tab admissionTab;
	private Tab qualificationTab;
	private Tab mediaTab;
	private Tab sessionTab;
	private Tab formatsTab;

	private VerticalLayout content;
	private Tab currentTab = null;

	// non-ui

	@Inject
	private StudentService studentService;

	@Inject
	private ContactService contactService;

	@Inject
	private AcademicService academicService;

	@Inject
	private QualificationService qualificationService;

	@Inject
	private MediaService mediaService;

	private int collegeId = 0;

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

		studentInfoCard = new StudentInfoCard(studentService);

		tabs = new Tabs();
		personalTab = new Tab("Personal");
		contactTab = new Tab("Contact");
		admissionTab = new Tab("Admission");
		qualificationTab = new Tab("Qualification");
		mediaTab = new Tab("Media");
		sessionTab = new Tab("Sessions");
		formatsTab = new Tab("Formats");

		content = new VerticalLayout();

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
		tabs.add(sessionTab);
		tabs.add(formatsTab);

		tabs.addSelectedChangeListener(event -> {
			content.removeAll();
			Student student = VaadinSession.getCurrent().getAttribute(Student.class);
			Tab tab = event.getSelectedTab();
			if (tab == personalTab) {
				if (personalDetails == null) {
					personalDetails = new StudentPersonalDetails(studentService, mediaService);
					personalDetails.addListener(StudentPersonalDetails.RefreshEvent.class,
							this::handlePersonalDetailsRefreshEvent);
				}
				content.add(personalDetails);
				if (student != null) {
					personalDetails.setStudentId(student.getId());
					currentTab = personalTab;
				}

			} else if (tab == contactTab) {
				if (contactDetails == null) {
					contactDetails = new StudentContactDetails(studentService, contactService);
				}
				content.add(contactDetails);
				if (student != null) {
					contactDetails.setStudentId(student.getId());
					currentTab = contactTab;
				}

			} else if (tab == admissionTab) {
				if (admissionDetails == null) {
					admissionDetails = new StudentAdmissionDetails(studentService, academicService);
				}

				content.add(admissionDetails);
				if (student != null) {
					admissionDetails.setStudentId(student.getId());
					currentTab = admissionTab;
				}

			} else if (tab == qualificationTab) {
				if (qualificationDetails == null) {
					qualificationDetails = new StudentQualificationDetails(studentService, qualificationService,
							academicService);
				}

				content.add(qualificationDetails);
				if (student != null) {
					qualificationDetails.setStudentId(student.getId());
					currentTab = qualificationTab;
				}

			} else if (tab == mediaTab) {
				if (mediaDetails == null) {
					mediaDetails = new StudentMediaDetails(mediaService);
				}
				content.add(mediaDetails);
				if (student != null) {
					mediaDetails.setStudentId(student.getId());
					currentTab = mediaTab;
				}

			} else if (tab == sessionTab) {
				if (sessionDetails == null) {
					sessionDetails = new StudentSessionDetails(studentService, academicService);
				}
				content.add(sessionDetails);
				if (student != null) {
					sessionDetails.setStudentId(student.getId());
					currentTab = sessionTab;
				}

			}
		});
	}

	private void handlePersonalDetailsRefreshEvent(StudentPersonalDetails.RefreshEvent event) {
		Student student = event.getStudent();
		studentInfoCard.setStudentId(student.getId());
	}
}
