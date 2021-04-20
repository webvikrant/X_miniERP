package in.co.itlabs.minierp.components;

import java.io.ByteArrayInputStream;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

import in.co.itlabs.minierp.entities.Media;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.entities.StudentSessionInfo;

public class StudentInfoCard extends VerticalLayout {

	// ui

	private Image photographImage;
	private Image signatureImage;

	private TextField nameField;
	private TextField admissionIdField;
	private TextField currentSessionField;
	private TextField currentProgramField;
	private TextField currentSemesterField;
	private TextField currentSemesterStatusField;

	// non-ui

	public StudentInfoCard() {

		setAlignItems(Alignment.CENTER);

		photographImage = new Image();
		configurePhotographImage();

		signatureImage = new Image();
		configureSignatureImage();

		nameField = new TextField("Name");
		nameField.setWidthFull();
		nameField.setReadOnly(true);

		admissionIdField = new TextField("Admission Id");
		admissionIdField.setWidthFull();
		admissionIdField.setReadOnly(true);

		currentSessionField = new TextField("Session");
		currentSessionField.setWidthFull();
		currentSessionField.setReadOnly(true);

		currentProgramField = new TextField("Program");
		currentProgramField.setWidthFull();
		currentProgramField.setReadOnly(true);

		currentSemesterField = new TextField("Semester");
		currentSemesterField.setWidthFull();
		currentSemesterField.setReadOnly(true);

		currentSemesterStatusField = new TextField("Status");
		currentSemesterStatusField.setWidthFull();
		currentSemesterStatusField.setReadOnly(true);

		add(photographImage, signatureImage, nameField, admissionIdField, currentSessionField, currentSemesterField,
				currentSemesterStatusField);
	}

	private void configurePhotographImage() {
		photographImage.addClassName("photo");
		photographImage.getStyle().set("objectFit", "contain");
		photographImage.setHeight("100px");
		photographImage.setWidth("100px");
	}

	private void configureSignatureImage() {
		signatureImage.addClassName("photo");
		signatureImage.getStyle().set("objectFit", "contain");
		signatureImage.setHeight("50px");
		signatureImage.setWidth("200px");
	}

	public void setStudent(Student student) {

		Media photographMedia = student.getPhotographMedia();

		if (photographMedia != null) {
			if (photographMedia.isImage()) {
				byte[] imageBytes = photographMedia.getFileBytes();
				StreamResource resource = new StreamResource(photographMedia.getFileName(),
						() -> new ByteArrayInputStream(imageBytes));
				photographImage.setSrc(resource);
			}
		}

		Media signatureMedia = student.getSignatureMedia();

		if (signatureMedia != null) {
			if (signatureMedia.isImage()) {
				byte[] imageBytes = signatureMedia.getFileBytes();
				StreamResource resource = new StreamResource(signatureMedia.getFileName(),
						() -> new ByteArrayInputStream(imageBytes));
				signatureImage.setSrc(resource);
			}
		}

		nameField.setValue(student.getName());
		if (student.getAdmissionId() != null) {
			admissionIdField.setValue(student.getAdmissionId());
		}

		StudentSessionInfo latestSessionInfo = student.getLastestSessionInfo();
		if (latestSessionInfo != null) {
			currentSessionField.setValue(latestSessionInfo.getSession().getName());
			currentProgramField.setValue(latestSessionInfo.getProgram().getName());
			currentSemesterField.setValue(latestSessionInfo.getSemester().toString());
			currentSemesterStatusField.setValue(latestSessionInfo.getSemesterStatus().toString());
		}
	}
}
