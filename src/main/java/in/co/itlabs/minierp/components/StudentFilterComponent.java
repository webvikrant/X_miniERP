package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import in.co.itlabs.minierp.util.StudentFilterParams;
import in.co.itlabs.minierp.util.StudentFilterParams.FilterType;

@UIScoped
public class StudentFilterComponent extends VerticalLayout {

	private Select<StudentFilterParams.FilterType> searchTypeSelect;
	private VerticalLayout content;

	private TextField queryField;
	private Button okButton;
	private Button clearButton;

	private Binder<StudentFilterParams> binder;

	@PostConstruct
	public void init() {

		content = new VerticalLayout();
		content.addClassName("card");

		searchTypeSelect = new Select<>();
		configureSearchTypeSelect();

		queryField = new TextField("Name /Roll No/ Admission No");
		queryField.setWidthFull();

		okButton = new Button("Search", VaadinIcon.SEARCH.create());
		configureOkButton();

		clearButton = new Button("Clear", VaadinIcon.CLOSE.create());
		configureClearButton();

		binder = new Binder<>(StudentFilterParams.class);

		binder.forField(searchTypeSelect).asRequired("Search type can not be blank").bind("filterType");
		binder.forField(queryField).bind("query");

		Span blank = new Span();

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setWidthFull();
		buttonBar.add(okButton, blank, clearButton);
		buttonBar.expand(blank);

		add(searchTypeSelect, content, buttonBar);

		searchTypeSelect.setValue(StudentFilterParams.FilterType.BASIC);
	}

	private void configureSearchTypeSelect() {
		searchTypeSelect.setLabel("Select a filter");
		searchTypeSelect.setWidthFull();
		searchTypeSelect.setItems(StudentFilterParams.FilterType.values());
		searchTypeSelect.addValueChangeListener(e -> {
			content.removeAll();
			if (e.getValue() == null) {
				return;
			}
			switch (e.getValue()) {
			case BASIC:
				content.add(queryField);
				break;

			default:
				break;
			}
		});
	}

	private void configureOkButton() {
		okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		okButton.addClickListener(e -> {
			if (binder.validate().isOk()) {
				fireEvent(new StudentFilterEvent(this, binder.getBean()));
			}
		});
	}

	private void configureClearButton() {
		clearButton.addClickListener(e -> {
			clearFields();
			if (binder.validate().isOk()) {
				fireEvent(new StudentFilterEvent(this, binder.getBean()));
			}
		});
	}

	private void clearFields() {
		searchTypeSelect.setValue(FilterType.BASIC);
		queryField.clear();
	}

	public void setFilterParams(StudentFilterParams searchParams) {
		binder.setBean(searchParams);
	}

	public static abstract class SearchEvent extends ComponentEvent<StudentFilterComponent> {
		private StudentFilterParams searchParams;

		protected SearchEvent(StudentFilterComponent source, StudentFilterParams searchParams) {

			super(source, false);
			this.searchParams = searchParams;
		}

		public StudentFilterParams getFilterParams() {
			return searchParams;
		}
	}

	public static class StudentFilterEvent extends SearchEvent {
		StudentFilterEvent(StudentFilterComponent source, StudentFilterParams searchParams) {
			super(source, searchParams);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}
