package gui;

import domain.Book;
import domain.PropertyListBinding;
import domain.ThemeConverter;
import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DetailViewBook extends TabPane implements Binding<Book> {

    @FXML
    private TextField name;
    @FXML
    private TextArea description;
    @FXML
    private TextField themes;
    @FXML
    private TextField ageCategory;
    @FXML
    private TextField author;
    @FXML
    private TextField publisher;
    
    private PropertyListBinding themesBinding;

    public DetailViewBook() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/gui/detailview_book.fxml"));
	themesBinding = new PropertyListBinding();
	
        try {
            loader.setRoot(this);
            loader.setController(this);
            loader.load();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void bind(Book t) {
        Bindings.bindBidirectional(name.textProperty(), t.nameProperty());
        Bindings.bindBidirectional(description.textProperty(), t.descriptionProperty());
        themesBinding.bind(themes.textProperty(), t.getThemeFX(), new ThemeConverter());
        Bindings.bindBidirectional(ageCategory.textProperty(), t.ageCategoryProperty());
        Bindings.bindBidirectional(author.textProperty(), t.authorProperty());
        Bindings.bindBidirectional(publisher.textProperty(), t.publisherProperty());
    }

}