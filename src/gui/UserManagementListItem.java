package gui;

import domain.User;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * A component representing a single User, used in the {@link UserManagement}
 * GUI.
 *
 * @author Frederik
 */
public class UserManagementListItem extends HBox {

    @FXML
    private Text name;
    
    @FXML
    private Text classroom;

    @FXML
    private Text email;

    private User user;

    public UserManagementListItem(User user) {
	FXUtil.loadFXML(this, "listview_user");
	name.textProperty().bind(user.nameProperty());
	classroom.textProperty().bind(user.classRoomProperty());
	email.textProperty().bind(user.emailProperty());
	this.user = user;
    }

    public User getUser() {
	return this.user;
    }
}
