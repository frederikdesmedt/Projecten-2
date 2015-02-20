package persistence;

import domain.Item;
import domain.ItemCopy;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javax.persistence.EntityManager;

/**
 * The repository used to handle the storing of items, can synchronize save changes with the database (using JPA).
 *
 * @author Brent
 */
public class ItemRepository {

    private static ItemRepository INSTANCE;

    private ObservableList<Item> items = FXCollections.observableArrayList();
    private ObservableList<ItemCopy> itemCopies = FXCollections.observableArrayList();

    private ItemRepository() {
	ListChangeListener changeListener = c -> {
	    while (c.next()) {
		c.getAddedSubList().forEach(i -> JPAUtil.getInstance().getEntityManager().persist(i));
	    }
	};
		
	items.addListener(changeListener);
	itemCopies.addListener(changeListener);
    }

    /**
     * Will get all items from the database and add them in the internal list. Observers of the ObservableList will receive updates containg the new data.
     */
    public void sync() {
	items.setAll(JPAUtil.getInstance().getEntityManager().createNamedQuery("Item.findAll", Item.class).getResultList());
	itemCopies.setAll(JPAUtil.getInstance().getEntityManager().createNamedQuery("ItemCopy.findAll", ItemCopy.class).getResultList());
    }

    public static ItemRepository getInstance() {
	if (INSTANCE == null) {
	    INSTANCE = new ItemRepository();
	}
	return INSTANCE;
    }

    public void add(Item item) {
	items.add(item);
    }

    public void remove(Item item) {
	items.remove(item);
    }

    public void add(ItemCopy itemCopy) {
	itemCopies.add(itemCopy);
    }

    public void remove(ItemCopy itemCopy) {
	items.remove(itemCopy);
    }

    public ObservableList<? extends Item> getItems() {
	return items;
    }

    public ObservableList<ItemCopy> getItemCopies() {
	return itemCopies;
    }

    public ObservableList<? extends Item> getItemsByClass(Class<? extends Item> clazz) {
	return getItemsByPredicate(i -> {
	    try {
		clazz.cast(i);
		return true;
	    } catch (ClassCastException ex) {
		return false;
	    }
	});
    }

    public ObservableList<ItemCopy> getItemCopiesByPredicate(Predicate<ItemCopy> predicate) {
	ObservableList<ItemCopy> filteredList = itemCopies.filtered(predicate);
	return filteredList;
    }

    public ObservableList<? extends Item> getItemsByPredicate(Predicate<Item> predicate) {
	ObservableList<? extends Item> filteredList = items.filtered(predicate);
	return filteredList;
    }

    /**
     * Saves every item in the internal list to the database.
     */
    public void saveChanges() {
	EntityManager manager = JPAUtil.getInstance().getEntityManager();
	manager.getTransaction().begin();
	items.forEach(manager::merge);
	itemCopies.forEach(manager::merge);
	manager.getTransaction().commit();
    }

    /**
     * Clear the internal list.
     */
    public void clearItems() {
	items.clear();
    }

    // Add mock data
//    public static void main(String[] args) {
//	Book b = new Book("Romantiek", "8+", "Romeo en Julia", "Het beroemde verhaal van Shakespeare", "Shakespeare", "Shakespeare");
//	Book b2 = new Book("Actie", "12+", "Stormbreaker", "Een 15-jarige jongen treedt in dienst bij de geheime diensten.", "Anthony Horowitz", "Facet");
//	Game g = new Game("Financieel", "12+", "Monopolie", "Het beroemde Monopolie-spel", "Parkser Brothers");
//	ItemCopy copy = new ItemCopy(5, "Achterste rij, links", b, Damage.MODERATE_DAMAGE);
//	ItemRepository.getInstance().add(b);
//	ItemRepository.getInstance().add(b2);
//	ItemRepository.getInstance().add(g);
//	ItemRepository.getInstance().add(copy);
//	ItemRepository.getInstance().saveChanges();
//	ItemRepository.getInstance().sync();
//    }
}
