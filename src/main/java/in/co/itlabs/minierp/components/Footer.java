package in.co.itlabs.minierp.components;

import javax.annotation.PostConstruct;

import com.vaadin.cdi.annotation.UIScoped;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@UIScoped
public class Footer extends HorizontalLayout {

	private Icon copyrightIcon;
	private Anchor link;
	private Button button;
	private Span email = new Span("webvikrant@gmail.com");

	@PostConstruct
	public void init() {
		setMargin(false);
		setPadding(false);
		setSpacing(false);

		setJustifyContentMode(JustifyContentMode.BETWEEN);
		setAlignItems(Alignment.CENTER);

		copyrightIcon = VaadinIcon.COPYRIGHT.create();
		copyrightIcon.setSize("10px");

		button = new Button("IT Labs", VaadinIcon.COPYRIGHT.create());
		button.addThemeVariants(ButtonVariant.LUMO_SMALL);
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		link = new Anchor("https://itlabs.co.in", button);

		email.addClassName("small-text");

		add(link, email);
	}
}
