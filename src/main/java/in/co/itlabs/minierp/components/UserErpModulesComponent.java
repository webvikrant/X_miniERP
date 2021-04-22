package in.co.itlabs.minierp.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import in.co.itlabs.minierp.entities.User;
import in.co.itlabs.minierp.entities.UserErpModule;
import in.co.itlabs.minierp.services.AuthService;
import in.co.itlabs.minierp.services.AuthService.ErpModule;

public class UserErpModulesComponent extends VerticalLayout {

	// ui

	private ComboBox<ErpModule> erpModuleCombo;
	private Button addButton;
	private Grid<UserErpModule> grid;

	private Button saveButton;
	private Button cancelButton;

	// non-ui
	private int userId;
	private Map<ErpModule, UserErpModule> accessMap;

	private AuthService authService;

	private final List<String> messages = new ArrayList<String>();

	public UserErpModulesComponent(AuthService authService) {
		this.authService = authService;

		erpModuleCombo = new ComboBox<AuthService.ErpModule>();
		configureModuleCombo();

		addButton = new Button("Add", VaadinIcon.PLUS.create());
		configureAddButton();

		HorizontalLayout toolBar = new HorizontalLayout();
		buildToolBar(toolBar);

		accessMap = new HashMap<AuthService.ErpModule, UserErpModule>();
		grid = new Grid<>(UserErpModule.class);
		configureGrid();

		saveButton = new Button("Save", VaadinIcon.CHECK.create());
		cancelButton = new Button("Cancel", VaadinIcon.CLOSE.create());

		HorizontalLayout buttonBar = new HorizontalLayout();
		buildButtonBar(buttonBar);

		add(toolBar, grid, buttonBar);
	}

	private void buildButtonBar(HorizontalLayout root) {
		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		saveButton.addClickListener(e -> {
			messages.clear();
			List<UserErpModule> userErpModules = new ArrayList<>(accessMap.values());
			boolean success = authService.updateUserErpModules(messages, userId, userErpModules);
			if (success) {
				Notification.show("Permissions updated successfully", 3000, Position.TOP_CENTER);
				reload();
			} else {
				Notification.show(messages.toString(), 3000, Position.TOP_CENTER);
			}
		});

		cancelButton.addClickListener(e -> {
			reload();
		});
		root.add(saveButton, cancelButton);
	}

	private void configureModuleCombo() {
		erpModuleCombo.setWidth("300px");
		erpModuleCombo.setLabel("miniERP modules");
		erpModuleCombo.setPlaceholder("Select a module");
		erpModuleCombo.setItems(ErpModule.values());
	}

	private void configureAddButton() {
		addButton.addClickListener(e -> {
			ErpModule erpModule = erpModuleCombo.getValue();
			if (erpModule != null) {
				if (accessMap.containsKey(erpModule)) {
					Notification.show("Erp module already added", 3000, Position.TOP_CENTER);
				} else {
					UserErpModule userErpModule = new UserErpModule();
					userErpModule.setUserId(userId);
					userErpModule.setErpModule(erpModule);

					userErpModule.setCanCreate(false);
					userErpModule.setCanRead(false);
					userErpModule.setCanUpdate(false);
					userErpModule.setCanDelete(false);

					accessMap.put(erpModule, userErpModule);

					reloadGrid();
				}
			}
		});
	}

	private void buildToolBar(HorizontalLayout toolBar) {
		toolBar.setAlignItems(Alignment.END);
		toolBar.add(erpModuleCombo, addButton);
	}

	private void configureGrid() {
		grid.removeAllColumns();
		grid.setHeightByRows(true);

		grid.addColumn("erpModule").setWidth("250px");

		grid.addComponentColumn(userErpModule -> {
			Checkbox checkbox = new Checkbox();
			checkbox.setValue(userErpModule.isCanCreate());
			checkbox.addValueChangeListener(e -> {
				userErpModule.setCanCreate(e.getValue());
				grid.getDataProvider().refreshItem(userErpModule);
			});
			return checkbox;
		}).setHeader("Can Create").setWidth("70px");

		grid.addComponentColumn(userErpModule -> {
			Checkbox checkbox = new Checkbox();
			checkbox.setValue(userErpModule.isCanRead());
			checkbox.addValueChangeListener(e -> {
				userErpModule.setCanRead(e.getValue());
				grid.getDataProvider().refreshItem(userErpModule);
			});
			return checkbox;
		}).setHeader("Can Read").setWidth("60px");

		grid.addComponentColumn(userErpModule -> {
			Checkbox checkbox = new Checkbox();
			checkbox.setValue(userErpModule.isCanUpdate());
			checkbox.addValueChangeListener(e -> {
				userErpModule.setCanUpdate(e.getValue());
				grid.getDataProvider().refreshItem(userErpModule);
			});
			return checkbox;
		}).setHeader("Can Update").setWidth("70px");

		grid.addComponentColumn(userErpModule -> {
			Checkbox checkbox = new Checkbox();
			checkbox.setValue(userErpModule.isCanDelete());
			checkbox.addValueChangeListener(e -> {
				userErpModule.setCanDelete(e.getValue());
				grid.getDataProvider().refreshItem(userErpModule);
			});
			return checkbox;
		}).setHeader("Can Delete").setWidth("70px");

		grid.addComponentColumn(userErpModule -> {
			Button button = new Button("Remove", VaadinIcon.MINUS.create());
			button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			button.addClickListener(e -> {
				accessMap.remove(userErpModule.getErpModule());
				reloadGrid();
			});
			return button;
		}).setHeader("Remove").setWidth("90px");

	}

	public void setUserId(int userId) {
		this.userId = userId;
		reload();
	}

	private void reload() {
		User user = authService.getUserById(userId);
		List<UserErpModule> userErpModules = user.getUserErpModules();
		accessMap.clear();
		for (UserErpModule userErpModule : userErpModules) {
			accessMap.put(userErpModule.getErpModule(), userErpModule);
		}
		reloadGrid();
	}

	private void reloadGrid() {
		grid.setItems(accessMap.values());
	}
}
