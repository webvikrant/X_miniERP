package in.co.itlabs.minierp.layouts;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import in.co.itlabs.minierp.components.Footer;
import in.co.itlabs.minierp.components.Header;
import in.co.itlabs.minierp.components.NavBar;
import in.co.itlabs.minierp.entities.College;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.AuthService.AuthenticatedUser;
import in.co.itlabs.minierp.views.LoginView;

@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@Theme(Lumo.class)
@CssImport("./styles/shared-styles.css")
@Push(PushMode.MANUAL)
public class AppLayout extends VerticalLayout implements RouterLayout, BeforeEnterObserver {

	// ui

	private Header header;
	private NavBar navBar;
	private VerticalLayout content;
	private Footer footer;

	// non-ui

	@Inject
	private AcademicService academicService;

	@PostConstruct
	public void init() {

		header = new Header();
		header.setWidthFull();

		navBar = new NavBar(academicService);
		navBar.setWidthFull();

		content = new VerticalLayout();
		content.setPadding(false);
		content.setWidthFull();
		content.addClassName("card");

		footer = new Footer();
		footer.setWidthFull();

		VerticalLayout root = new VerticalLayout();

		root.getStyle().set("margin", "auto");
		root.setPadding(false);
		root.setWidth("1000px");

		root.add(header, navBar, content, footer);

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
			College college = VaadinSession.getCurrent().getAttribute(College.class);
			if (college == null) {
				content.getElement()
						.appendChild(new Text("Please select a college from the dropdown above.").getElement());
			} else {
				content.getElement().appendChild(Objects.requireNonNull(newContent.getElement()));
			}
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		AuthenticatedUser authUser = event.getUI().getSession().getAttribute(AuthenticatedUser.class);
		if (authUser == null) {
			event.forwardTo(LoginView.class);
		}
	}
}
