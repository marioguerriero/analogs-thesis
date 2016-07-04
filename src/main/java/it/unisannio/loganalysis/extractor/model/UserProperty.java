package it.unisannio.loganalysis.extractor.model;

import javax.persistence.*;

/**
 * Created by paolomoriello on 01/07/16.
 */
@Entity
@Table(name = "uav")
public class UserProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="hibernate_sequence")
    @SequenceGenerator(name="hibernate_sequence", sequenceName="hibernate_sequence", allocationSize=1)
    private int id;
    @ManyToOne
    @JoinColumn(name = "entityId")
    private User user;
    @Column(name = "attribute")
    private String key;
    @Column(name = "value")
    private String value;

    public UserProperty() {
    }

    public UserProperty(User user, String key, String value) {
        this.user = user;
        this.key = key;
        this.value = value;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void put(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserProperty property = (UserProperty) o;

        if (id != property.id) return false;
        if (key != null ? !key.equals(property.key) : property.key != null) return false;
        return value != null ? value.equals(property.value) : property.value == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
