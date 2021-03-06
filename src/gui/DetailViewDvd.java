package gui;

import domain.DetailViewUtil;
import domain.Dvd;
import domain.PropertyListBinding;
import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A DetailView which can be bound to a specific Dvd.
 *
 */
public class DetailViewDvd extends TabPane implements Binding<Dvd> {

    @FXML
    private TextField name;
    @FXML
    private TextArea description;
    @FXML
    private HBox themes;
    @FXML
    private TextField ageCategory;
    @FXML
    private TextField director;
    @FXML
    private ImageView image;

    private PropertyListBinding themesBinding;
    private ThemeManager themeManager;

    private Dvd boundedDvd;

    public DetailViewDvd() {
	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/gui/detailview_dvd.fxml"));
	themesBinding = new PropertyListBinding();

	try {
	    loader.setRoot(this);
	    loader.setController(this);
	    loader.load();
	    description.setWrapText(true);
	    DetailViewUtil.initImageDragAndDrop(image);
	    DetailViewUtil.setBounds(this);
	} catch (IOException ex) {
	    throw new RuntimeException(ex);
	}
	
	themeManager = new ThemeManager(themes);
    }

    @FXML
    public void onSaveImage() {
	DetailViewUtil.selectImage(image);
    }

    @Override
    public void bind(Dvd t) {
	if (boundedDvd != null) {
	    Bindings.unbindBidirectional(name.textProperty(), boundedDvd.nameProperty());
	    Bindings.unbindBidirectional(description.textProperty(), boundedDvd.descriptionProperty());
	    themesBinding.unbind();
	    Bindings.unbindBidirectional(ageCategory.textProperty(), boundedDvd.ageCategoryProperty());
	    Bindings.unbindBidirectional(director.textProperty(), boundedDvd.directorProperty());
	    Bindings.unbindBidirectional(image.imageProperty(), boundedDvd.imageProperty());
	}

	Bindings.bindBidirectional(name.textProperty(), t.nameProperty());
	Bindings.bindBidirectional(description.textProperty(), t.descriptionProperty());
	themeManager.bind(t.getThemeFX());
	Bindings.bindBidirectional(ageCategory.textProperty(), t.ageCategoryProperty());
	Bindings.bindBidirectional(director.textProperty(), t.directorProperty());
	Bindings.bindBidirectional(image.imageProperty(), t.imageProperty());
	boundedDvd = t;
    }

}
