package in.co.itlabs.minierp.components;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

import in.co.itlabs.minierp.entities.Media;
import in.co.itlabs.minierp.services.MediaService;

public class StudentMediaDetails extends VerticalLayout {

	// ui

	private Grid<Media> grid = new Grid<>(Media.class);
	private final Div resultCount = new Div();

	private Dialog dialog = new Dialog();
	private MediaEditor mediaEditor;

	// non-ui
	
	private MediaService mediaService;
	
	private List<String> messages = new ArrayList<String>();
	private int studentId;
	private Media media;

	public StudentMediaDetails(MediaService mediaService) {
		this.mediaService = mediaService;

		mediaEditor = new MediaEditor();
		mediaEditor.addListener(MediaEditor.SaveEvent.class, this::handleSaveEvent);
		mediaEditor.addListener(MediaEditor.CancelEvent.class, this::handleCancelEvent);

		dialog.setWidth("400px");
		dialog.setModal(true);
		dialog.setDraggable(true);

		media = new Media();

		resultCount.addClassName("small-text");

		configureGrid();

		HorizontalLayout toolBar = new HorizontalLayout();
		toolBar.setWidthFull();
		buildToolBar(toolBar);

		add(toolBar, grid);
		setAlignSelf(Alignment.END, toolBar);

		reload();
	}

	private void configureGrid() {
		grid.setHeightByRows(true);
		grid.removeAllColumns();

		grid.addComponentColumn(media -> {
			Image photo = new Image();
			photo.addClassName("photo");
			photo.getStyle().set("objectFit", "contain");
			photo.setHeight("50px");

			if (media != null) {
				if (media.isImage()) {
					byte[] imageBytes = media.getFileBytes();
					StreamResource resource = new StreamResource(media.getFileName(),
							() -> new ByteArrayInputStream(imageBytes));
					photo.setSrc(resource);
				}
			}

			return photo;

		}).setHeader("Image").setWidth("80px");

		grid.addComponentColumn(media -> {
			Anchor downloadLink = new Anchor();

			byte[] imageBytes = media.getFileBytes();
			StreamResource resource = new StreamResource(media.getFileName(),
					() -> new ByteArrayInputStream(imageBytes));

			downloadLink.setText(media.getFileName());
			downloadLink.setHref(resource);
			downloadLink.setTarget("_blank");
			downloadLink.getElement().setAttribute("download", true);

			return downloadLink;

		}).setHeader("File").setWidth("150px");

		grid.addColumn("remark").setHeader("Remark").setWidth("50px");

		grid.addComponentColumn(media -> {

			Button editButton = new Button("Edit", VaadinIcon.PENCIL.create());
			editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
			editButton.addClickListener(e -> {
				dialog.removeAll();
				dialog.add(mediaEditor);
				dialog.open();
				mediaEditor.setMedia(media);
			});

			Button deleteButton = new Button("Delete", VaadinIcon.TRASH.create());
			deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
			deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
			deleteButton.addClickListener(e -> {
				dialog.removeAll();
				dialog.open();
			});

			HorizontalLayout buttonBar = new HorizontalLayout();
			buttonBar.add(editButton, deleteButton);

			return buttonBar;

		}).setHeader("Edit").setWidth("120px");

	}

	private void buildToolBar(HorizontalLayout root) {
		root.setAlignItems(Alignment.END);

		Button createButton = new Button("New", VaadinIcon.PLUS.create());
		createButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
		createButton.addClickListener(e -> {
			dialog.removeAll();
			dialog.add(mediaEditor);
			dialog.open();

			mediaEditor.setMedia(media);
			media.setStudentId(studentId);
		});

		Span blank = new Span();

		root.add(resultCount, blank, createButton);
		root.expand(blank);
	}

	public void reload() {
		List<Media> medias = mediaService.getAllMedias(studentId);
		resultCount.setText("Record(s) found: " + medias.size());
		grid.setItems(medias);
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
		reload();
	}

	public void handleSaveEvent(MediaEditor.SaveEvent event) {
		Media mediaFile = event.getMedia();

		if (mediaFile.getId() == 0) {
// 		create new
			mediaFile.setCreatedAt(LocalDateTime.now());
			messages.clear();
			int id = mediaService.createMedia(messages, event.getMedia());
			if (id > 0) {
				Notification.show("Media created successfully", 3000, Position.TOP_CENTER);
				mediaFile.clear();
				mediaFile.setStudentId(studentId);
				mediaEditor.setMedia(mediaFile);
				reload();
			} else {
				Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
			}
		} else {
// 		update existing
			messages.clear();
			boolean success = mediaService.updateMedia(messages, mediaFile);
			if (success) {
				Notification.show("Media updated successfully", 3000, Position.TOP_CENTER);
				mediaFile.clear();
				mediaFile.setStudentId(studentId);
				mediaEditor.setMedia(mediaFile);
				reload();
			} else {
				Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
			}
		}
	}

	public void handleCancelEvent(MediaEditor.CancelEvent event) {
		dialog.close();
	}
}
