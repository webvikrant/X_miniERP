package in.co.itlabs.minierp.layouts;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

import in.co.itlabs.minierp.components.Footer;
import in.co.itlabs.minierp.components.Header;
import in.co.itlabs.minierp.components.Navigation;

@CssImport("./styles/shared-styles.css")
public class AppLayout extends VerticalLayout implements RouterLayout {

	@Inject
	private Header header;

	@Inject
	private Navigation navigation;

	private VerticalLayout content;

	@Inject
	private Footer footer;

	@PostConstruct
	public void init() {
		addClassName("app-layout");

		header.setWidthFull();

		navigation.setWidthFull();

		content = new VerticalLayout();
		content.setPadding(false);
		content.setWidthFull();
		content.addClassName("card");

		footer.setWidthFull();

		VerticalLayout root = new VerticalLayout();

		root.getStyle().set("margin", "auto");
		root.setPadding(false);
		root.setSpacing(false);
		root.setWidth("1000px");

		root.add(header, navigation, content, footer);

		add(root);
	}

	@Override
	public void removeRouterLayoutContent(HasElement oldContent) {
		// TODO Auto-generated method stub
		content.getElement().removeAllChildren();
	}

	@Override
	public void showRouterLayoutContent(HasElement newContent) {
		// TODO Auto-generated method stub
		if (newContent != null) {
			content.getElement().appendChild(Objects.requireNonNull(newContent.getElement()));
		}
	}
}
