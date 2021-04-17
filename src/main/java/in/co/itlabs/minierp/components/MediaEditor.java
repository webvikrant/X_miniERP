package in.co.itlabs.minierp.components;

import java.io.ByteArrayInputStream;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.entities.Media;
import in.co.itlabs.minierp.util.Editor;

public class MediaEditor extends VerticalLayout implements Editor {

	private FileField fileField;
	private TextField remarkField;

	private Button saveButton;
	private Button cancelButton;

	private Binder<Media> binder;

	public MediaEditor() {

		fileField = new FileField();
		configureFileField();

		remarkField = new TextField("Remark");
		remarkField.setWidthFull();

		configureRemarkField();

		binder = new Binder<>(Media.class);

		binder.forField(remarkField).bind("remark");

		saveButton = new Button("OK", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout actionBar = buildActionBar();
		actionBar.setWidthFull();

		add(fileField, remarkField, actionBar);

	}

	private void configureRemarkField() {
		// TODO Auto-generated method stub

	}

	private void configureFileField() {
		fileField.setWidthFull();
		fileField.setPadding(false);

		Upload upload = fileField.getUpload();
		upload.setAutoUpload(true);
		upload.setMaxFiles(1);
		upload.setDropLabel(new Span("Upload a 512 KB file (JPEG or PNG)"));
		upload.setAcceptedFileTypes("image/jpeg", "image/png", "application/pdf");
		upload.setMaxFileSize(1024 * 512);
	}

	public void setMedia(Media media) {
		binder.setBean(media);

		// are we creating a new mdeia file or editing an existing one
		if (media.getId() == 0) {
			// new media file
			// do nothing
		} else {
			// existing media file
			byte[] fileBytes = media.getFileBytes();
			StreamResource resource = new StreamResource(media.getFileName(),
					() -> new ByteArrayInputStream(fileBytes));
			fileField.setResource(resource, media.getFileMime(), media.getFileName());
		}
	}

	private HorizontalLayout buildActionBar() {
		HorizontalLayout root = new HorizontalLayout();

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			if (binder.validate().isOk()) {

				String fileName = fileField.getFileName();
				String fileMime = fileField.getFileMime();
				byte[] fileBytes = fileField.getFileBytes();

				if (fileName != null && fileMime != null && fileBytes != null) {
					binder.getBean().setFileName(fileName);
					binder.getBean().setFileMime(fileMime);
					binder.getBean().setFileBytes(fileBytes);

					fireEvent(new SaveEvent(this, binder.getBean()));
				}
			}
		});

		cancelButton.addClickListener(e -> {
			fireEvent(new CancelEvent(this, binder.getBean()));
		});

		Span blank = new Span();

		root.add(saveButton, blank, cancelButton);
		root.expand(blank);

		return root;
	}

	@Override
	public void setEditable(boolean editable) {
		fileField.setReadOnly(!editable);

		saveButton.setVisible(editable);
		cancelButton.setVisible(editable);

	}

	public static abstract class MediaEvent extends ComponentEvent<MediaEditor> {
		private Media media;

		protected MediaEvent(MediaEditor source, Media media) {

			super(source, false);
			this.media = media;
		}

		public Media getMedia() {
			return media;
		}
	}

	public static class SaveEvent extends MediaEvent {
		SaveEvent(MediaEditor source, Media media) {
			super(source, media);
		}
	}

	public static class CancelEvent extends MediaEvent {
		CancelEvent(MediaEditor source, Media media) {
			super(source, media);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}