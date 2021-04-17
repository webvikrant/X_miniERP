package in.co.itlabs.minierp.components;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Caste;
import in.co.itlabs.minierp.entities.Category;
import in.co.itlabs.minierp.entities.Media;
import in.co.itlabs.minierp.entities.Occupation;
import in.co.itlabs.minierp.entities.Relation;
import in.co.itlabs.minierp.entities.Religion;
import in.co.itlabs.minierp.entities.Student;
import in.co.itlabs.minierp.services.MediaService;
import in.co.itlabs.minierp.services.StudentService;
import in.co.itlabs.minierp.util.Gender;

@UIScoped
public class StudentPersonalDetails extends VerticalLayout {

	@Inject
	private StudentService studentService;

	@Inject
	private MediaService mediaService;

	private Checkbox editCheck;

	private Select<Media> photographSelect;
	private Select<Media> signatureSelect;

	private TextField nameField;
	private DatePicker birthDatePicker;
	private RadioButtonGroup<Gender> genderRadio;

	private TextField motherNameField;
	private TextField fatherNameField;
	private TextField localGuardianNameField;

	private ComboBox<Religion> religionCombo;
	private ComboBox<Caste> casteCombo;
	private ComboBox<Category> categoryCombo;
	private ComboBox<Relation> relationCombo;

	private ComboBox<Occupation> motherOccupationCombo;
	private ComboBox<Occupation> fatherOccupationCombo;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Student> binder;

	@PostConstruct
	public void init() {

		editCheck = new Checkbox("Edit");

		photographSelect = new Select<Media>();
		configurePhotographSelect();

		signatureSelect = new Select<Media>();
		configureSignatureSelect();

		nameField = new TextField();
		configureNameField();

		birthDatePicker = new DatePicker();
		configureBirthDatePicker();

		genderRadio = new RadioButtonGroup<Gender>();
		configureGenderRadio();

		religionCombo = new ComboBox<Religion>();
		configureReligionCombo();

		casteCombo = new ComboBox<Caste>();
		configureCasteCombo();

		categoryCombo = new ComboBox<Category>();
		configureCategoryCombo();

		motherNameField = new TextField();
		configureMotherNameField();

		fatherNameField = new TextField();
		configureFatherNameField();

		localGuardianNameField = new TextField();
		configureGuardianNameField();

		relationCombo = new ComboBox<Relation>();
		configureRelationCombo();

		motherOccupationCombo = new ComboBox<Occupation>();
		configureMotherOccupationCombo();

		fatherOccupationCombo = new ComboBox<Occupation>();
		configureFatherOccupationCombo();

		saveButton = new Button("Save", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		binder = new Binder<>(Student.class);

//		binder.forField(collegeSelect).asRequired("College can not be blank").bind("college");
		binder.forField(photographSelect).bind("photographMedia");
		binder.forField(signatureSelect).bind("signatureMedia");
		binder.forField(nameField).asRequired("Name can not be blank").bind("name");
		binder.forField(motherNameField).asRequired("Mother name can not be blank").bind("motherName");
		binder.forField(fatherNameField).asRequired("Father name can not be blank").bind("fatherName");
		binder.forField(localGuardianNameField).asRequired("Local guardian name can not be blank").bind("localGuardianName");

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);

		FlexLayout flex1 = new FlexLayout();
		configureFlex(flex1);

		FlexLayout flex2 = new FlexLayout();
		configureFlex(flex2);

		FlexLayout flex3 = new FlexLayout();
		configureFlex(flex3);

		FlexLayout flex4 = new FlexLayout();
		configureFlex(flex4);

		FlexLayout flex5 = new FlexLayout();
		configureFlex(flex5);

		FlexLayout flex6 = new FlexLayout();
		configureFlex(flex6);

		flex1.add(photographSelect, signatureSelect);
		flex2.add(nameField, birthDatePicker, genderRadio);
		flex3.add(religionCombo, casteCombo, categoryCombo);
		flex4.add(motherNameField, motherOccupationCombo);
		flex5.add(fatherNameField, fatherOccupationCombo);
		flex6.add(localGuardianNameField, relationCombo);

		add(editCheck, buttonBar, flex1, flex2, flex3, flex4, flex5, flex6);
		setAlignSelf(Alignment.CENTER, buttonBar);
	}

	private void configurePhotographSelect() {
		photographSelect.setWidth("150px");
		photographSelect.setLabel("Photograph");
		photographSelect.setRenderer(new ComponentRenderer<Image, Media>(media -> {
			Image photograph = new Image();
			photograph.addClassName("photo");
			photograph.getStyle().set("objectFit", "contain");
			photograph.setHeight("90px");

			if (media != null) {
				if (media.isImage()) {
					byte[] imageBytes = media.getFileBytes();
					StreamResource resource = new StreamResource(media.getFileName(),
							() -> new ByteArrayInputStream(imageBytes));
					photograph.setSrc(resource);
				}
			}

			return photograph;
		}));
	}

	private void configureSignatureSelect() {
		signatureSelect.setWidth("250px");
		signatureSelect.setLabel("Signature");
		signatureSelect.setRenderer(new ComponentRenderer<Image, Media>(media -> {
			Image signature = new Image();
			signature.addClassName("photo");
			signature.getStyle().set("objectFit", "contain");
			signature.setHeight("50px");

			if (media != null) {
				if (media.isImage()) {
					byte[] imageBytes = media.getFileBytes();
					StreamResource resource = new StreamResource(media.getFileName(),
							() -> new ByteArrayInputStream(imageBytes));
					signature.setSrc(resource);
				}
			}

			return signature;
		}));
	}

	private void configureFatherOccupationCombo() {
		fatherOccupationCombo.setLabel("Father's occupation");
		fatherOccupationCombo.setWidth("200px");
	}

	private void configureMotherOccupationCombo() {
		motherOccupationCombo.setLabel("Mother's occupation");
		motherOccupationCombo.setWidth("200px");
	}

	private void configureRelationCombo() {
		relationCombo.setLabel("Relation");
		relationCombo.setWidth("200px");
	}

	private void configureCategoryCombo() {
		categoryCombo.setLabel("Category");
		categoryCombo.setWidth("200px");
	}

	private void configureCasteCombo() {
		casteCombo.setLabel("Caste");
		casteCombo.setWidth("200px");
	}

	private void configureReligionCombo() {
		religionCombo.setLabel("Religion");
		religionCombo.setWidth("200px");
	}

	private void configureGuardianNameField() {
		localGuardianNameField.setLabel("Guardian's name");
		localGuardianNameField.setWidth("200px");
	}

	private void configureFatherNameField() {
		fatherNameField.setLabel("Father's name");
		fatherNameField.setWidth("200px");
	}

	private void configureMotherNameField() {
		motherNameField.setLabel("Mother's name");
		motherNameField.setWidth("200px");
	}

	private void configureNameField() {
		nameField.setLabel("Name");
		nameField.setWidth("200px");
	}

	private void configureBirthDatePicker() {
		birthDatePicker.setLabel("Date of Birth");
		birthDatePicker.setWidth("200px");
	}

	private void configureGenderRadio() {
		genderRadio.setLabel("Gender");
		genderRadio.setItems(Gender.values());
		genderRadio.setValue(Gender.MALE);
	}

	private void configureFlex(FlexLayout flexLayout) {
		flexLayout.setFlexWrap(FlexWrap.WRAP);
		flexLayout.setAlignItems(Alignment.END);
		flexLayout.getElement().getStyle().set("padding", "8px");
		flexLayout.getElement().getStyle().set("gap", "12px");
	}

	private void buildButtonBar(HorizontalLayout root) {
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				Media photographMedia = binder.getBean().getPhotographMedia();
				Media signatureMedia = binder.getBean().getSignatureMedia();
				
				if(photographMedia!=null) {
					binder.getBean().setPhotographMediaId(photographMedia.getId());	
				}
				if(signatureMedia!=null) {
					binder.getBean().setSignatureMediaId(signatureMedia.getId());	
				}

				fireEvent(new SaveEvent(this, binder.getBean()));
			}
		});

		cancelButton.addClickListener(e -> {
			fireEvent(new CancelEvent(this, binder.getBean()));
		});

		root.add(saveButton, cancelButton);
	}

	public void setStudentId(int studentId) {
		Student student = studentService.getStudentById(studentId);

		List<Media> photoMedias = mediaService.getImageMedias(studentId);
		
		photographSelect.setItems(photoMedias);
		Media photographMedia = mediaService.getMedia(student.getPhotographMediaId());
		student.setPhotographMedia(photographMedia);

		signatureSelect.setItems(photoMedias);
		Media signatureMedia = mediaService.getMedia(student.getSignatureMediaId());
		student.setSignatureMedia(signatureMedia);

		binder.setBean(student);
	}

	public void setEditable(boolean editable) {

	}

	public static abstract class StudentEvent extends ComponentEvent<StudentPersonalDetails> {
		private Student student;

		protected StudentEvent(StudentPersonalDetails source, Student student) {

			super(source, false);
			this.student = student;
		}

		public Student getStudent() {
			return student;
		}
	}

	public static class SaveEvent extends StudentEvent {
		SaveEvent(StudentPersonalDetails source, Student student) {
			super(source, student);
		}
	}

	public static class CancelEvent extends StudentEvent {
		CancelEvent(StudentPersonalDetails source, Student student) {
			super(source, student);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}

}
