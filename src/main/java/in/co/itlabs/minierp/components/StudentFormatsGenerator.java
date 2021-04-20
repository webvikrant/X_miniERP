package in.co.itlabs.minierp.components;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.io.ByteStreams;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.ExecutorService;
import in.co.itlabs.minierp.services.StudentService;
import in.co.itlabs.minierp.util.StudentFormat;

public class StudentFormatsGenerator extends VerticalLayout {

	// ui

	private ComboBox<StudentFormat> formatCombo;
	private Button startButton;

	private Div infoLabel;
	private Div primaryInfoLabel;

	private ProgressBar progressBar;

	private Button cancelButton;
	private boolean cancelFlag = false;
	private Anchor downloadLink;

	// non-ui

	private static final Font font12Bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static final Font font11Bold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
	private static final Font font11Italic = new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC);
	private static final Font font10 = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
	private static final Font font10Italic = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
	private static final Font font10BoldItalic = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLDITALIC);
	private static final Font font09 = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
	private static final Font font08 = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

	private StudentService studentService;

	private int collegeId = 0;

	private ExportTask task;
	private UI ui;

	private int rowNum = 0;

	private int recordsProcessed = 0;

	private double progress = 0d;

//	private List<Student> students;
	private int studentId;

	public StudentFormatsGenerator(StudentService studentService, ExecutorService executorService) {
		this.studentService = studentService;

		College college = VaadinSession.getCurrent().getAttribute(College.class);
		if (college != null) {
			collegeId = college.getId();
		}

		ui = UI.getCurrent();

		formatCombo = new ComboBox<StudentFormat>();
		formatCombo.setItems(StudentFormat.values());

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
			StudentFormat format = formatCombo.getValue();
			if (format == null) {
				ui.access(() -> {
					readyState();
					infoLabel.setText("No format selected...");
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

		add(formatCombo, startButton, infoLabel, primaryInfoLabel, progressBar, cancelButton, downloadLink);
	}

//	public void setStudents(List<Student> students) {
//		this.students = students;
//		readyState();
//	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
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

//				List<String> columns = fieldsPicker.getSelectedFields();
				StudentFormat format = formatCombo.getValue();

				// Write the output to a file

				File reportFile = File.createTempFile("minierp-", "-student-format.pdf", null);
				reportFile.deleteOnExit();

				FileOutputStream fileOut = new FileOutputStream(reportFile);
				fileOut.close();

				// Closing the workbook

				System.out.println("File name: " + reportFile.getName());

				Document document = new Document(PageSize.A4, 50, 40, 100, 30);
				PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(reportFile));

				PageHeader events = new PageHeader();
				writer.setPageEvent(events);

				document.open();

				// meta data
				document.addTitle("Title: miniERP by itlabs.co.in");
				document.addSubject("Subject: miniERP");
				document.addAuthor("Author: Vikrant Thakur");
				document.addCreator("Creator: Vikrant Thakur");

				// data
				Student student = studentService.getStudentById(studentId);
				String message = format.toString() + " for " + student.getName() + "...";
				document.add(new Paragraph(message, font10));

				document.close();

				// update state
				ui.access(() -> {
					cancelButton.setVisible(false);
					infoLabel.setText("Processing finished, saving file...");
					ui.push();
				});

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
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class PageHeader extends PdfPageEventHelper {

		PdfTemplate headerTemplate;

		public void onOpenDocument(PdfWriter writer, Document document) {
			System.out.println("onOpenDocument");
			headerTemplate = writer.getDirectContent().createTemplate(30, 16);
		}

		public void onEndPage(PdfWriter writer, Document document) {
			PdfPTable orgTable = new PdfPTable(2);
			PdfPTable headerTable = new PdfPTable(3);
			PdfPTable footerTable = new PdfPTable(3);
			try {
				// organization
				orgTable.setWidths(new int[] { 2, 1 });
				orgTable.setTotalWidth(500);
				orgTable.setLockedWidth(true);
				orgTable.getDefaultCell().setFixedHeight(50);
				orgTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

				Paragraph p1 = new Paragraph("IEC", font12Bold);
				Paragraph p2 = new Paragraph("Faculty Feedback", font11Bold);
				Paragraph p3 = new Paragraph("report name", font11Italic);

				Paragraph p = new Paragraph();
				p.add(p1);
				addEmptyLine(p1, 1);
				p.add(p2);
				p.add(p3);

				// footer
				footerTable.setWidths(new int[] { 1, 1, 1 });
				footerTable.setTotalWidth(500);
				footerTable.setLockedWidth(true);
				footerTable.getDefaultCell().setFixedHeight(40);

				PdfPCell fCell1 = new PdfPCell(new Paragraph("miniERP", font08));
				fCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
				fCell1.setBorder(Rectangle.TOP);
				footerTable.addCell(fCell1);

				Chunk chunk = new Chunk("itlabs.co.in", font08);
				chunk.setAnchor("https://itlabs.co.in");
				Paragraph av = new Paragraph(chunk);
				PdfPCell fCell2 = new PdfPCell(av);
				fCell2.setHorizontalAlignment(Element.ALIGN_CENTER);
				fCell2.setBorder(Rectangle.TOP);
				footerTable.addCell(fCell2);

				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM, yyyy (hh:mm a)");
				String dateString = dateFormat.format(date);

				PdfPCell fCell3 = new PdfPCell(new Paragraph(dateString, font08));
				fCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				fCell3.setBorder(Rectangle.TOP);
				footerTable.addCell(fCell3);

				footerTable.writeSelectedRows(0, -1, 61, 40, writer.getDirectContent());

			} catch (DocumentException de) {
				throw new ExceptionConverter(de);
			}
		}

		public void onCloseDocument(PdfWriter writer, Document document) {
			ColumnText.showTextAligned(headerTemplate, Element.ALIGN_LEFT,
					new Phrase(String.valueOf(writer.getPageNumber() - 1), font10Italic), 2, 2, 0);
		}
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

}
