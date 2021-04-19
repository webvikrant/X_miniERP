package in.co.itlabs.minierp.components;

import java.util.List;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import in.co.itlabs.minierp.entities.Program;
import in.co.itlabs.minierp.entities.Session;
import in.co.itlabs.minierp.entities.StudentSessionInfo;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.StudentService;

public class StudentSessionDetails extends VerticalLayout {

	// ui

	private VerticalLayout content;
	private Div resultCount;

	// non-ui

	private StudentService studentService;
	private AcademicService academicService;

	private int studentId;

	public StudentSessionDetails(StudentService stduentService, AcademicService academicService) {
		this.studentService = stduentService;
		this.academicService = academicService;

		content = new VerticalLayout();
		resultCount = new Div();
		resultCount.addClassName("small-text");

		add(resultCount, content);
	}

	public void reload() {
		content.removeAll();

		List<StudentSessionInfo> sessionInfos = studentService.getAllSessionInfos(studentId);
		resultCount.setText("Record(s) found: " + sessionInfos.size());

		for (StudentSessionInfo sessionInfo : sessionInfos) {

			StudentSessionInfoEditor sessionInfoEditor = new StudentSessionInfoEditor(academicService);
			sessionInfoEditor.setEditable(false);

			Checkbox editCheck = new Checkbox("Edit");
			editCheck.addValueChangeListener(e -> {
				sessionInfoEditor.setEditable(e.getValue());
			});

			Session session = academicService.getSessionById(sessionInfo.getSessionId());
			sessionInfo.setSession(session);

			Program program = academicService.getProgramById(sessionInfo.getProgramId());
			sessionInfo.setProgram(program);
			
			sessionInfoEditor.setStudentSessionInfo(sessionInfo);

			VerticalLayout root = new VerticalLayout();
			root.addClassName("card");
			root.add(editCheck, sessionInfoEditor);

			content.add(root);
		}
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
		reload();
	}

}
