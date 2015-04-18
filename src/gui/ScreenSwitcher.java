package gui;

import domain.IconConfig;
import domain.Item;
import domain.ItemCopy;
import domain.User;
import domain.controllers.ItemManagementController;
import domain.controllers.LoanManagementListItemController;
import domain.controllers.MainMenuController;
import domain.controllers.TitleBarController;
import gui.dialogs.LoginPanel;
import gui.dialogs.PopupUtil;
import java.util.List;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PopOver;
import persistence.UserRepository;

/**
 *
 * @author Frederik De Smedt
 */
public class ScreenSwitcher extends BorderPane {

    private MainMenu menu;
    private ItemManagement itemManagement;
    private LoanManagement loanManagement;
    private UserManagement userManagement;
    private Titlebar titlebar;

    private UserRepository USER_REPO_INSTANCE = UserRepository.getInstance();

    private MainMenuController mainMenuController;
    private ItemManagementController itemManagementController;
    private LoanManagementListItemController loanManagementController;

    public ScreenSwitcher() {
	initialize();
	setPrefSize(1024, 768);
	setMaxSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
	PopupUtil.switcher = this;
	getStylesheets().add(getClass().getResource("/resources/css/global.css").toExternalForm());
	setTop(titlebar);

	loadIcons(menu);
	loadIcons(itemManagement);
	loadIcons(loanManagement);
	loadIcons(userManagement);
	loadIcons(titlebar);
    }

    private void initialize() {
	titlebar = new Titlebar(this);
	titlebar.setController(new TitleBarController(titlebar, this));
	this.menu = new MainMenu(this, mainMenuController);
	this.userManagement = new UserManagement();
	this.itemManagement = new ItemManagement(itemManagementController);
	this.loanManagement = new LoanManagement(loanManagementController);

	mainMenuController = new MainMenuController(menu, this);
	itemManagementController = new ItemManagementController(itemManagement, this);

	itemManagement.setController(itemManagementController);
    }

    public void loadIcons(Node node) {
	if (node instanceof Parent) {
	    Parent p = (Parent) node;
	    if (!p.getChildrenUnmodifiable().isEmpty()) {
		p.getChildrenUnmodifiable().forEach(this::loadIcons);
	    }
	}

	List<String> classes = node.getStyleClass();
	classes.stream().forEach(clazz -> IconConfig.identify(node, clazz));
    }

    public void openMainMenu() {
	User authenticatedUser = UserRepository.getInstance().getAuthenticatedUser();
	if (authenticatedUser == null || authenticatedUser.getUserType() == User.UserType.STUDENT) {
	    openItemManagement();
	    return;
	}

	titlebar.setTitle("Hoofdmenu");
	setCenter(menu);
    }

    public void openItemManagement() {
	titlebar.setTitle("Voorwerpen beheren");
	setCenter(itemManagement);
    }

    public void openUserManagement() {
	titlebar.setTitle("Gebruikers beheren");
	setCenter(userManagement);
    }

    public void openLoanManagement() {
	titlebar.setTitle("Uitleningen beheren");
	setCenter(loanManagement);
    }

    public void openExcelImport() {
	throw new UnsupportedOperationException();
    }

    public boolean openDeletePopup(Object o) {
	Alert a = new Alert(AlertType.CONFIRMATION);
	if (o instanceof ItemCopy) {
	    ItemCopy copy = (ItemCopy) o;
	    buildDeleteAlert(copy, a);
	} else if (o instanceof Item) {
	    Item item = (Item) o;
	    buildDeleteAlert(item, a);
	} else {
	    return false;
	}

	ButtonType okButton = new ButtonType("Ja", ButtonData.OK_DONE);
	ButtonType cancelButton = new ButtonType("Nee", ButtonData.CANCEL_CLOSE);

	a.getButtonTypes().setAll(okButton, cancelButton);

	Optional<ButtonType> result = a.showAndWait();
	return result.get() == okButton;
    }

    private void buildDeleteAlert(Item item, Alert a) {
	a.setTitle("Voorwerp verwijderen");
	a.setHeaderText("Bent u zeker dat u dit voorwerp wilt verwijderen?");
	a.setContentText("U staat op het punt om '" + item.getName() + "' definitief te verwijderen. Alle exemplaren van dit voorwerp zullen ongeldig zijn en ook verwijderd worden.");
    }

    private void buildDeleteAlert(ItemCopy copy, Alert a) {
	a.setTitle("Exemplaar verwijderen");
	a.setHeaderText("Bent u zeker dat u dit exemplaar wilt verwijderen?");
	a.setContentText("U staat op het punt om '" + copy.getItem().getName() + " #" + copy.getCopyNumber() + "' definitief te verwijderen.");
    }

    /**
     * Log in using the email of the user and a non-encrypted password.
     *
     * @param email the email of the user
     * @param password the password of the user
     * @return true if the email and password are correct
     */
    public boolean login(String email, String password) {
	return USER_REPO_INSTANCE.authenticate(email, password);
    }

    public void processLoginRequest(Button loginButton, Label authenticatedUserLabel) {
	if (USER_REPO_INSTANCE.getAuthenticatedUser() != null) {
	    USER_REPO_INSTANCE.logout();
	    loginButton.setText("Aanmelden");
	    authenticatedUserLabel.setText("");
	    openMainMenu();
	} else {
	    if (MainApp.DEVELOPMENT_MODE) {
		login("", "");
		authenticatedUserLabel.setText("Welkom " + USER_REPO_INSTANCE.getAuthenticatedUser().getName());
		loginButton.setText("Afmelden");
		//PopupUtil.showNotification("Development Mode", "Aangemeld met maximale rechten!", PopupUtil.Notification.INFORMATION);
		return;
	    }
	    LoginPanel loginPanel = new LoginPanel();
	    PopOver pop = PopupUtil.showPopOver(loginButton, loginPanel);
	    loginPanel.setOnLogin(e -> {
		if (login(loginPanel.getUsername(), loginPanel.getPassword())) {
		    authenticatedUserLabel.setText("Welkom " + USER_REPO_INSTANCE.getAuthenticatedUser().getName());
		    pop.hide();
		    loginButton.setText("Afmelden");
		} else {
		    loginPanel.resetPanel(true);
		    authenticatedUserLabel.setText("");
		}
	    });
	}

    }
}
