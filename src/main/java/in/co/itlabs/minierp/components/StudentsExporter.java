package in.co.itlabs.minierp.components;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.io.ByteStreams;
import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.server.StreamResource;

import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.ExecutorService;
import in.co.itlabs.minierp.services.StudentService;
import in.co.itlabs.minierp.util.StudentFilterParams;

@UIScoped
public class StudentsExporter extends VerticalLayout {

	@Inject
	private ExecutorService executorService;

	@Inject
	private StudentService studentService;

	private FieldsPicker fieldsPicker;

	private ExportTask task;
	private UI ui;

	private int rowNum = 0;

	private int recordsProcessed = 0;

	private double progress = 0d;

	// components
	private Button startButton;

	private Div infoLabel;
	private Div primaryInfoLabel;

	private ProgressBar progressBar;

	private Button cancelButton;
	private boolean cancelFlag = false;
	private Anchor downloadLink;

//	private List<Student> students;
	private StudentFilterParams filterParams;

	@PostConstruct
	public void init() {

		ui = UI.getCurrent();

		H4 helpText = new H4("To select fields, just drag & drop. You can also re-order selected fields.");

		fieldsPicker = new FieldsPicker(Student.getFields());
		fieldsPicker.setWidthFull();
		fieldsPicker.setHeight("300px");

		startButton = new Button("Export");
		startButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		startButton.setDisableOnClick(true);

		infoLabel = new Div();
		primaryInfoLabel = new Div();

		progressBar = new ProgressBar();

		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

		downloadLink = new Anchor();

		readyState();
		// listeners
		startButton.addClickListener(e -> {
			List<String> columns = fieldsPicker.getSelectedFields();
			if (columns == null || columns.size() == 0) {
				ui.access(() -> {
					readyState();
					infoLabel.setText("No column selected...");
					ui.push();
				});
				return;
			}

			ui.access(() -> {
				busyState();
				ui.push();
			});

			// thread
//			ThreadPoolExecutor executor = Application.getExecutor();
			task = new ExportTask();
			executorService.getExecutor().execute(task);
		});

		cancelButton.addClickListener(e -> {
			cancelFlag = true;
		});

		add(helpText, fieldsPicker, startButton, infoLabel, primaryInfoLabel, progressBar, cancelButton, downloadLink);
	}

//	public void setStudents(List<Student> students) {
//		this.students = students;
//		readyState();
//	}

	public void setStudentFilterParams(StudentFilterParams filterParams) {
		this.filterParams = filterParams;
		readyState();
	}

	private void readyState() {
		// TODO Auto-generated method stub
		recordsProcessed = 0;

		progress = 0d;

		cancelFlag = false;

		infoLabel.setText("");
		primaryInfoLabel.setText("");

		progressBar.setVisible(false);

		startButton.setEnabled(true);
		cancelButton.setVisible(false);
		downloadLink.setVisible(false);
	}

	private void busyState() {
		// TODO Auto-generated method stub
		startButton.setEnabled(false);
		progressBar.setVisible(true);
		cancelButton.setVisible(true);
		downloadLink.setVisible(false);
	}

	private void finishedState() {
		// TODO Auto-generated method stub
		recordsProcessed = 0;
//		total = 0;
		progress = 0d;

		cancelFlag = false;

		startButton.setEnabled(true);
		progressBar.setVisible(true);
		cancelButton.setVisible(false);
	}

	class ExportTask implements Runnable {

		public void run() {
			try {

				ui.access(() -> {
					infoLabel.setText("Processing records, please wait...");
					ui.push();
				});

//				total = userTests.size();

				List<String> columns = fieldsPicker.getSelectedFields();

				// create file
				Workbook workbook = new XSSFWorkbook();
				CreationHelper creationHelper = workbook.getCreationHelper();

				// Create a Sheet
				Sheet sheet = workbook.createSheet("Title");

				// Create a Font for styling header cells
				Font headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerFont.setFontHeightInPoints((short) 12);
				headerFont.setColor(IndexedColors.BLACK.getIndex());

				// Create a CellStyle with the font
				CellStyle headerCellStyle = workbook.createCellStyle();
				headerCellStyle.setFont(headerFont);

				// Create a Row
				rowNum = 0;
				Row headerRow = sheet.createRow(rowNum);

				// Create cells
				for (int i = 0; i < columns.size(); i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(columns.get(i));
					cell.setCellStyle(headerCellStyle);
				}

				// Create Cell Style for formatting Date
				CellStyle dateCellStyle = workbook.createCellStyle();
				dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));

				List<Student> students = studentService.getStudents(filterParams);

				if (students != null) {
					for (Student student : students) {
						if (cancelFlag) {
							break;
						}

						TimeUnit.MILLISECONDS.sleep(3000);

						recordsProcessed++;

						ui.access(() -> {
							progress = Double.valueOf(recordsProcessed) / Double.valueOf(students.size());
							progressBar.setValue(progress);
							primaryInfoLabel
									.setText(recordsProcessed + "/" + students.size() + " - " + student.getName());
							ui.push();
						});

						Row row = null;
						rowNum++;
						row = sheet.createRow(rowNum);
						addRecord(row, student, columns);
					}
				}

				// Resize all columns to fit the content size
				for (int i = 0; i < columns.size(); i++) {
					sheet.autoSizeColumn(i);
				}

				// update state
				ui.access(() -> {
					cancelButton.setVisible(false);
					infoLabel.setText("Processing finished, saving file...");
					ui.push();
				});

				// xslx
				// Write the output to a file

				File reportFile = File.createTempFile("sis-", "-students.xlsx", null);
				reportFile.deleteOnExit();

				FileOutputStream fileOut = new FileOutputStream(reportFile);
				workbook.write(fileOut);
				fileOut.close();

				// Closing the workbook
				workbook.close();

				System.out.println("File name: " + reportFile.getName());

				InputStream in = new FileInputStream(reportFile);
				byte[] bytes = ByteStreams.toByteArray(in);

				StreamResource resource = new StreamResource(reportFile.getName(),
						() -> new ByteArrayInputStream(bytes));

				ui.access(() -> {
					downloadLink.setVisible(true);
					downloadLink.setText("Download file");
					downloadLink.setHref(resource);
					downloadLink.setTarget("_blank");
					downloadLink.getElement().setAttribute("download", true);

					infoLabel.setText("Records exported, click the link below to download file");
					Notification.show("Records exported, click the link below to download file", 3000,
							Position.TOP_CENTER);
					finishedState();
					ui.push();
				});

			} catch (

			InterruptedException e) {
				ui.access(() -> {
					Notification.show(e.getMessage(), 3000, Position.TOP_CENTER);
					ui.push();
				});
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				ui.access(() -> {
					Notification.show(e.getMessage(), 3000, Position.TOP_CENTER);
					ui.push();
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				ui.access(() -> {
					Notification.show(e.getMessage(), 3000, Position.TOP_CENTER);
					ui.push();
				});
			}
		}

	}

	private void addRecord(Row row, Student student, List<String> columns) {

		int colIndex = 0;
		for (String column : columns) {
			switch (column) {
			case Student.ID:
				// id
				Cell idCell = row.createCell(colIndex);
				idCell.setCellValue(student.getId());

				break;

			case Student.NAME:
				// name
				Cell nameCell = row.createCell(colIndex);
				nameCell.setCellValue(student.getName());

				break;

			case Student.ADMISSION_ID:
				// name
				Cell admissionIdCell = row.createCell(colIndex);
				admissionIdCell.setCellValue(student.getAdmissionId());

				break;

			default:
				break;
			}

			colIndex++;
		}

	}

}
