package gui.controls;

import domain.Book;
import domain.Cd;
import domain.Damage;
import domain.Dvd;
import domain.FontCache;
import domain.Game;
import domain.ItemCopy;
import domain.Loan;
import domain.StoryBag;
import gui.FXUtil;
import gui.LoanEvent;
import gui.dialogs.PopupUtil;
import java.util.Comparator;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.PopOver;
import persistence.ItemRepository;

/**
 * A button that holds the {@link domain.Item} icon and {@link ItemCopy} number.
 *
 * @author Frederik De Smedt
 */
public class CopyButton extends HBox implements Comparator<CopyButton> {

    @FXML
    private Label copyId;

    @FXML
    private Label icon;

    private ItemCopy copy;

    private CopyPopOver popOverContent;
    private PopOver popOver;

    public CopyButton(ItemCopy copy) {
	FXUtil.loadFXML(this, "exemplaar_button");

//	DropShadow ds = new DropShadow();
//	ds.setOffsetX(1f);
//	ds.setOffsetY(1f);
//	copyId.setEffect(ds);
	this.copy = copy;
	popOverContent = new CopyPopOver(copy);
	super.setOnMouseClicked(e -> showDetails());
	copyId.setText(copy.getCopyNumber());
	createIcon();

	final InvalidationListener update = i -> updateIconAvailability();
	copy.damageProperty().addListener(update);
	copy.getObservableLoans().addListener((ListChangeListener.Change<? extends Loan> c) -> {
	    while (c.next()) {
		if (c.wasAdded()) {
		    c.getAddedSubList().stream().map(Loan::returnedProperty).forEach(p -> p.addListener(update));
		}

		if (c.wasRemoved()) {
		    c.getRemoved().stream().map(Loan::returnedProperty).forEach(p -> p.removeListener(update));
		}
	    }
	    
	    updateIconAvailability();
	});

	updateIconAvailability();
    }

    public ItemCopy getCopy() {
	return copy;
    }

    public void updateIconAvailability() {
	popOverContent.updateStartLoan();
	if (copy.getLoans().stream().anyMatch(i -> !i.getReturned())) {
	    icon.setTextFill(Color.RED);
	} else if (copy.damageProperty().get() == Damage.MODERATE_DAMAGE || copy.damageProperty().get() == Damage.HIGH_DAMAGE) {
	    icon.setTextFill(Color.GREY);
	} else {
	    icon.setTextFill(Color.GREEN);
	}
    }

    private void createIcon() {
	icon.setFont(FontCache.getIconFont(30));
	icon.setTextFill(Color.BLACK);
	if (copy.getItem() instanceof Book) {
	    icon.setText("\uf02d");
	} else if (copy.getItem() instanceof Game) {
	    icon.setText("\uf091");
	} else if (copy.getItem() instanceof Dvd) {
	    icon.setText("\uf008");
	} else if (copy.getItem() instanceof Cd) {
	    icon.setText("\uf025");
	} else if (copy.getItem() instanceof StoryBag) {
	    icon.setText("\uf0b1");
	}
    }

    public void showDetails() {
//	ItemRepository.getInstance().saveItemCopy(copy);
	popOverContent.update();
	popOver = PopupUtil.showPopOver(this, popOverContent);
    }

    public void setOnDelete(EventHandler<LoanEvent> handler) {
	popOverContent.setOnDelete(addPopUpCloseHandle(handler));
    }

    public void setOnStartLoan(EventHandler<LoanEvent> handler) {
	popOverContent.setOnStartLoan(addPopUpCloseHandle(handler));
    }

    private EventHandler<LoanEvent> addPopUpCloseHandle(EventHandler<LoanEvent> handler) {
	return evt -> {
	    if (popOver != null) {
		popOver.hide();
	    }

	    handler.handle(evt);
	};
    }

    @Override
    public int compare(CopyButton o1, CopyButton o2) {
	return getCopy().getCopyNumber().compareTo(o2.getCopy().getCopyNumber());
    }
}
