package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.StudentService;

public class StudentInfoCard extends VerticalLayout {

	// ui

	private TextField nameField;
	private TextField admissionIdField;

	// non-ui

	private StudentService studentService;

	public StudentInfoCard(StudentService studentService) {
		this.studentService = studentService;

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
