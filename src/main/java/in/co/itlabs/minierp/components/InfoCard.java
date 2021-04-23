package in.co.itlabs.minierp.components;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class InfoCard extends HorizontalLayout {

	// ui
	private Div numberDiv;
	private Div titleDiv;
	private Div subTitleDiv;
	private Button button;

	// non-ui

	public InfoCard() {
		addClassName("info-card");

		VerticalLayout numberVLayout = new VerticalLayout();
		numberVLayout.setWidth("80px");

		VerticalLayout descriptionVLayout = new VerticalLayout();
		descriptionVLayout.setWidth("200px");

		numberDiv = new Div();
		numberDiv.addClassName("number");

		titleDiv = new Div();
		titleDiv.addClassName("title");

		subTitleDiv = new Div();
		subTitleDiv.addClassName("sub-title");

		button = new Button("Show", VaadinIcon.EYE.create());

		numberVLayout.add(numberDiv);
		
		descriptionVLayout.add(titleDiv, subTitleDiv, button);
		descriptionVLayout.setAlignSelf(Alignment.END, button);
		
		add(numberVLayout, descriptionVLayout);
	}

	public void setInfo(String number, String title, String subTitle) {
		numberDiv.setText(number);
		titleDiv.setText(title);
		subTitleDiv.setText(subTitle);
	}

	public static abstract class InfoCardEvent extends ComponentEvent<InfoCard> {

		protected InfoCardEvent(InfoCard source) {
			super(source, false);
		}
	}

	public static class ShowDetailsEvent extends InfoCardEvent {
		ShowDetailsEvent(InfoCard source) {
			super(source);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {

		return getEventBus().addListener(eventType, listener);
	}
}
