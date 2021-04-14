package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

@ViewScoped
public class StudentBasicSearch extends VerticalLayout {

	private TextField queryField;
	private Button okButton;

	@PostConstruct
	public void init() {

		queryField = new TextField("Name /Roll No/ Admission No");

		okButton = new Button("Search", VaadinIcon.SEARCH.create());
		configureOkButton();

		add(queryField, okButton);
	}

	private void configureOkButton() {
		okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		okButton.addClickListener(e -> {
			System.out.println("Search Clicked "+queryField.getValue());
			fireEvent(new BasicSearchEvent(this, queryField.getValue()));
		});
	}

	public static abstract class SearchEvent extends ComponentEvent<StudentBasicSearch> {
		private String query;

		protected SearchEvent(StudentBasicSearch source, String query) {

			super(source, false);
			this.query = query;
		}

		public String getQuery() {
			return query;
		}
	}

	public static class BasicSearchEvent extends SearchEvent {
		BasicSearchEvent(StudentBasicSearch source, String query) {
			super(source, query);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}
