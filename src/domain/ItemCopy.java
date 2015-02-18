package domain;

import domain.annotations.Display;
import java.io.Serializable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Frederik
 */
@Entity
@Access(AccessType.PROPERTY)
@NamedQueries({
    @NamedQuery(name = "ItemCopy.findAll", query = "SELECT ic FROM ItemCopy ic")
})
public class ItemCopy implements Serializable {

    private int id;
    private final StringProperty location = new SimpleStringProperty();
    private final ObjectProperty<Item> item = new SimpleObjectProperty<>();
    private final ObjectProperty<Damage> damage = new SimpleObjectProperty<>();
    private int copyNumber;

    public ItemCopy() {
    }

    public ItemCopy(int copyNumber, String location, Item i, Damage d) {
	setCopyNumber(copyNumber);
	setLocation(location);
	setItem(i);
	setDamage(d);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public void setCopyNumber(int copyNumber) {
	this.copyNumber = copyNumber;
    }

    public int getCopyNumber() {
	return this.copyNumber;
    }

    @Display("Locatie")
    public StringProperty locationProperty() {
	return location;
    }

    public String getLocation() {
	return location.get();
    }

    public void setLocation(String location) {
	this.location.set(location);
    }

    @Enumerated(EnumType.STRING)
    public Damage getDamage() {
	return damage.get();
    }

    public void setDamage(Damage damage) {
	this.damage.set(damage);
    }

    @Display("Schade")
    public ObjectProperty<Damage> damageProperty() {
	return damage;
    }

    @Display("Voorwerp")
    public ObjectProperty<Item> itemProperty() {
	return item;
    }

    public void setItem(Item i) {
	this.item.set(i);
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    public Item getItem() {
	return this.item.get();
    }

    @Override
    public int hashCode() {
	int hash = 7;
	hash = 97 * hash + this.id;
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
	final ItemCopy other = (ItemCopy) obj;
	if (this.id != other.id) {
	    return false;
	}
	return true;
    }
}
