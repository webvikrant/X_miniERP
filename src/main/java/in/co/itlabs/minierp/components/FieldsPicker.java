package in.co.itlabs.minierp.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dnd.GridDragEndEvent;
import com.vaadin.flow.component.grid.dnd.GridDragStartEvent;
import com.vaadin.flow.component.grid.dnd.GridDropEvent;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;

public class FieldsPicker extends HorizontalLayout {

	private Grid<String> availableFieldsGrid = new Grid<>(String.class);
	private Grid<String> selectedFieldsGrid = new Grid<>(String.class);

	private List<String> draggedFields;
	private Grid<String> dragSource;

	public FieldsPicker(List<String> availableFields) {

		availableFieldsGrid.setItems(availableFields);

		availableFieldsGrid.setHeightFull();
		selectedFieldsGrid.setHeightFull();

		add(availableFieldsGrid, selectedFieldsGrid);

		ComponentEventListener<GridDragStartEvent<String>> dragStartListener = event -> {
			draggedFields = event.getDraggedItems();
			dragSource = event.getSource();
			availableFieldsGrid.setDropMode(GridDropMode.BETWEEN);
			selectedFieldsGrid.setDropMode(GridDropMode.BETWEEN);
		};

		ComponentEventListener<GridDragEndEvent<String>> dragEndListener = event -> {
			draggedFields = null;
			dragSource = null;
			availableFieldsGrid.setDropMode(null);
			selectedFieldsGrid.setDropMode(null);
		};

		ComponentEventListener<GridDropEvent<String>> dropListener = event -> {
			Optional<String> target = event.getDropTargetItem();

			if (target.isPresent() && draggedFields.contains(target.get())) {
				return;
			}

			// Remove the items from the source grid
			@SuppressWarnings("unchecked")
			ListDataProvider<String> sourceDataProvider = (ListDataProvider<String>) dragSource.getDataProvider();
			List<String> sourceItems = new ArrayList<>(sourceDataProvider.getItems());
			sourceItems.removeAll(draggedFields);
			dragSource.setItems(sourceItems);

			// Add dragged items to the target Grid
			Grid<String> targetGrid = event.getSource();
			@SuppressWarnings("unchecked")
			ListDataProvider<String> targetDataProvider = (ListDataProvider<String>) targetGrid.getDataProvider();
			List<String> targetItems = new ArrayList<>(targetDataProvider.getItems());

			int index = target.map(
					person -> targetItems.indexOf(person) + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0))
					.orElse(0);

			targetItems.addAll(index, draggedFields);
			targetGrid.setItems(targetItems);
		};

		availableFieldsGrid.removeAllColumns();
		availableFieldsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
		availableFieldsGrid.addDropListener(dropListener);
		availableFieldsGrid.addDragStartListener(dragStartListener);
		availableFieldsGrid.addDragEndListener(dragEndListener);
		availableFieldsGrid.setRowsDraggable(true);
		availableFieldsGrid.addColumn(s -> {
			return s;
		}).setHeader("Available fields");

		selectedFieldsGrid.removeAllColumns();
		selectedFieldsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
		selectedFieldsGrid.addDropListener(dropListener);
		selectedFieldsGrid.addDragStartListener(dragStartListener);
		selectedFieldsGrid.addDragEndListener(dragEndListener);
		selectedFieldsGrid.setRowsDraggable(true);
		selectedFieldsGrid.addColumn(s -> {
			return s;
		}).setHeader("Selected fields");
	}

	public List<String> getSelectedFields() {
		return selectedFieldsGrid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
	}
}
