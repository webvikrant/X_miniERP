package in.co.itlabs.minierp.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import in.co.itlabs.minierp.components.UserCollegesComponent;
import in.co.itlabs.minierp.components.UserInfoCard;
import in.co.itlabs.minierp.components.UserErpModulesComponent;
import in.co.itlabs.minierp.entities.User;
import in.co.itlabs.minierp.layouts.AppLayout;
import in.co.itlabs.minierp.services.AcademicService;
import in.co.itlabs.minierp.services.AuthService;

@PageTitle(value = "User details")
@Route(value = "user-details", layout = AppLayout.class)
public class UserDetailsView extends VerticalLayout {

	// ui

	private UserInfoCard userInfoCard;

	private UserCollegesComponent userCollegesComponent;
	private UserErpModulesComponent userErpModulesComponent;

	private ComboBox<User> userCombo;
	private SplitLayout splitLayout;

	private Tabs tabs;
	private Tab collegesTab;
	private Tab erpModulesTab;

	private VerticalLayout content;
	private Tab currentTab = null;

	// non-ui

	@Inject
	private AuthService authService;

	@Inject
	private AcademicService academicService;

	@PostConstruct
	public void init() {

		setPadding(false);
		setAlignItems(Alignment.CENTER);

		Div titleDiv = new Div();
		buildTitle(titleDiv);

		userCombo = new ComboBox<User>();
		configureUserCombo();

		userInfoCard = new UserInfoCard();

		tabs = new Tabs();
		collegesTab = new Tab("Colleges");
		erpModulesTab = new Tab("Modules");

		content = new VerticalLayout();

		configureTabs();

		splitLayout = new SplitLayout();
		splitLayout.setWidthFull();
		configureSplitLayout();

		add(titleDiv, userCombo, splitLayout);
		splitLayout.setVisible(false);

		// check if SudentsView has put the selected student into the session
		User user = VaadinSession.getCurrent().getAttribute(User.class);
		userCombo.setValue(user);
	}

	private void buildTitle(Div root) {
		root.addClassName("view-title");
		root.add("User details");
	}

	private void configureUserCombo() {

		userCombo.setWidth("300px");
		userCombo.setPlaceholder("Select a user");
		userCombo.setItems(authService.getAllUsers(null));
		userCombo.setItemLabelGenerator(user -> {
			return user.getUsername();
		});

		userCombo.addValueChangeListener(event -> {
//			studentId = 0;
//			if (event.getValue() != null) {
//				studentId = event.getValue().getId();
//			}
			VaadinSession.getCurrent().setAttribute(User.class, event.getValue());
			reload();
		});
	}

	private void reload() {
		User user = VaadinSession.getCurrent().getAttribute(User.class);
		if (user == null) {
			splitLayout.setVisible(false);
		} else {
			splitLayout.setVisible(true);

			userInfoCard.setUser(user);

			tabs.setSelectedTab(null);
			if (currentTab == null) {
				currentTab = collegesTab;
			}
			tabs.setSelectedTab(currentTab);
		}
	}

	private void configureSplitLayout() {

		splitLayout.setSplitterPosition(25);
		splitLayout.addToPrimary(userInfoCard);

		VerticalLayout tabsLayout = new VerticalLayout();
		tabsLayout.setWidth("700px");
		tabsLayout.setPadding(false);
		tabsLayout.setSpacing(false);
		tabsLayout.add(tabs, content);

		splitLayout.addToSecondary(tabsLayout);
	}

	private void configureTabs() {
		content.setPadding(false);
		content.setSpacing(false);

		tabs.add(collegesTab);
		tabs.add(erpModulesTab);

		tabs.addSelectedChangeListener(event -> {
			content.removeAll();
			User user = VaadinSession.getCurrent().getAttribute(User.class);
			Tab tab = event.getSelectedTab();
			if (tab == collegesTab) {
				if (userCollegesComponent == null) {
					userCollegesComponent = new UserCollegesComponent(authService, academicService);
				}
				content.add(userCollegesComponent);
				if (user != null) {
					userCollegesComponent.setUserId(user.getId());
					currentTab = collegesTab;
				}

			} else if (tab == erpModulesTab) {
				if (userErpModulesComponent == null) {
					userErpModulesComponent = new UserErpModulesComponent(authService);
				}
				content.add(userErpModulesComponent);
				if (user != null) {
					userErpModulesComponent.setUserId(user.getId());
					currentTab = erpModulesTab;
				}
			}
		});
	}
}
