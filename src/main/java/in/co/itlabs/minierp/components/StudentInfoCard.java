package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.StudentService;

@UIScoped
public class StudentInfoCard extends VerticalLayout {

	@Inject
	private StudentService studentService;

	private TextField nameField;
	private TextField admissionIdField;

	@PostConstruct
	public void init() {

		nameField = new TextField("Name");
		nameField.setReadOnly(true);

		admissionIdField = new TextField("Admission Id");
		admissionIdField.setReadOnly(true);

		add(nameField, admissionIdField);
	}

	public void setStudentId(int studentId) {
		Student student = studentService.getStudentById(studentId);
		nameField.setValue(student.getName());
		if (student.getAdmissionId() != null) {
			admissionIdField.setValue(student.getAdmissionId());
		}
	}
}
