package gui;

import domain.Cache;
import domain.Item;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * A ListCell that holds an {@link ItemManagementListItem}, used in {@link ItemManagement}
 * 
 * @author Frederik De Smedt
 */
public class ItemManagementListItemCell extends ListCell<Item> {

    public static Callback<ListView<Item>, ListCell<Item>> forListView() {
	return i -> new ItemManagementListItemCell();
    }

    private ItemManagementListItem listItem;

    public ItemManagementListItemCell() {
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
	super.updateItem(item, empty);
	if (super.isEmpty()) {
	    listItem = null;
	    Platform.runLater(() -> super.setGraphic(listItem));
	} else {
	    final ChangeListener<Boolean> listener = (obs, ov, nv) -> {
		if (!nv) {
		    super.setGraphic(null);
		} else {
		    super.setGraphic(listItem);
		}
	    };

	    if (listItem != null) {
		listItem.getItem().visibleProperty().removeListener(listener);
	    }

	    listItem = Cache.getItemCache().get(item);
	    listItem.getItem().visibleProperty().addListener(listener);
	    if (listItem != null) {
		if (Platform.isFxApplicationThread()) {
		    if (listItem != null && listItem.getItem().getVisible()) {
			super.setGraphic(listItem);
		    } else {
			listItem = null;
			super.setGraphic(listItem);
		    }
		} else {
		    Platform.runLater(() -> {
			if (listItem != null && listItem.getItem() != null && listItem.getItem().getVisible()) {
			    super.setGraphic(listItem);
			} else {
			    listItem = null;
			    super.setGraphic(listItem);
			}
		    });
		}
	    }
	}
    }
}
