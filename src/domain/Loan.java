package domain;

import java.io.Serializable;
import java.util.Calendar;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * An entity representing a loan stored in the database.
 *
 * @author Frederik De Smedt
 */
@Entity
@Access(AccessType.PROPERTY)
@NamedQueries({
    @NamedQuery(name = "Loan.findAll", query = "SELECT l FROM Loan l")
})
public class Loan implements Serializable, Searchable {

    private int id;

    private final ObjectProperty<ItemCopy> itemCopy = new SimpleObjectProperty<>();
    private final ObjectProperty<User> user = new SimpleObjectProperty<>();
    private final ObjectProperty<Calendar> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<Calendar> date = new SimpleObjectProperty<>();
    private final BooleanProperty returned = new SimpleBooleanProperty(false);
    private final IntegerProperty amountOfExtensions = new SimpleIntegerProperty(0);

    public Loan() {
    }

    public Loan(ItemCopy copy, User user, int days) {
	this.itemCopy.set(copy);
	this.user.set(user);
	startDate.set(Calendar.getInstance());
	date.set(Calendar.getInstance());
	date.get().add(Calendar.DAY_OF_YEAR, days);
    }

    public boolean getReturned() {
	return returned.get();
    }

    public void setReturned(boolean isReturned) {
	this.returned.set(isReturned);
    }

    public BooleanProperty returnedProperty() {
	return this.returned;
    }

    public int getAmountOfExtensions() {
	return amountOfExtensions.get();
    }

    public void setAmountOfExtensions(int count) {
	amountOfExtensions.set(count);
    }

    public IntegerProperty amountOfExtensions() {
	return amountOfExtensions;
    }

    @ManyToOne(cascade = {CascadeType.MERGE})
    public ItemCopy getItemCopy() {
	return itemCopy.get();
    }

    public void setItemCopy(ItemCopy copy) {
	this.itemCopy.set(copy);
    }

    public ObjectProperty<ItemCopy> itemCopyProperty() {
	return itemCopy;
    }

    @ManyToOne(cascade = {CascadeType.MERGE})
    public User getUser() {
	return user.get();
    }

    public void setUser(User user) {
	this.user.set(user);
    }

    public ObjectProperty<User> userProperty() {
	return this.user;
    }

    @Temporal(TemporalType.DATE)
    public Calendar getDate() {
	return this.date.get();
    }

    public void setDate(Calendar date) {
	this.date.set(date);
    }

    public ObjectProperty<Calendar> dateProperty() {
	return this.date;
    }

    @Temporal(TemporalType.DATE)
    public Calendar getStartDate() {
	return this.startDate.get();
    }

    public void setStartDate(Calendar date) {
	this.startDate.set(date);
    }

    public ObjectProperty<Calendar> startDateProperty() {
	return this.startDate;
    }

    @Override
    public boolean test(String t) {
	for (String token : t.split("\\s+")) {
	    boolean inDate = SearchPredicate.containsIgnoreCase(String.valueOf(date.get().get(Calendar.YEAR)), token)
		    || SearchPredicate.containsIgnoreCase(String.valueOf(date.get().get(Calendar.MONTH)), token)
		    || SearchPredicate.containsIgnoreCase(String.valueOf(date.get().get(Calendar.DAY_OF_MONTH)), token);

	    if (inDate || user.get().test(token) || itemCopy.get().test(token)) {
		continue;
	    } else {
		return false;
	    }
	}

	return true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 53 * hash + this.id;
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final Loan other = (Loan) obj;
	if (this.getId() != other.getId()) {
	    return false;
	}
	return true;
    }

    @Override
    public String toString() {
	return getItemCopy().toString() + ", uitgeleend aan: " + getUser().toString();
    }
}
