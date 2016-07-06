package it.unisannio.loganalysis.extractor.model;

import javax.persistence.*;
import java.util.*;

/**
 * Created by paolomoriello on 29/06/16.
 */
@Entity
@Table(name = "action")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="hibernate_sequence")
    @SequenceGenerator(name="hibernate_sequence", sequenceName="hibernate_sequence", allocationSize=1)
    @Column(name = "idAction")
    private int idAction;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User userFrom;

    @ManyToOne
    @JoinColumn(name = "idUserTo")
    private User userTo;

    @ManyToOne
    @JoinColumn(name = "idResource")
    private Resource resource;

    @Column(name = "type")
    private char type;

    @Column(name = "millis")
    private long millis;

    @OneToMany(mappedBy = "action")
    @MapKey(name = "key")
    private Map<String, ActionProperty> properties;

    public Action() { }

    public Action(User userFrom, User userTo, Resource resource, char type, long millis, Map<String, ActionProperty> properties) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.resource = resource;
        this.type = type;
        this.millis = millis;
        this.properties = properties;
    }

    public int getIdAction() {
        return idAction;
    }

    public void setIdAction(int idAction) {
        this.idAction = idAction;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public Map<String, ActionProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ActionProperty> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (idAction != action.idAction) return false;
        if (type != action.type) return false;
        if (millis != action.millis) return false;
        if (userFrom != null ? !userFrom.equals(action.userFrom) : action.userFrom != null) return false;
        if (userTo != null ? !userTo.equals(action.userTo) : action.userTo != null) return false;
        if (resource != null ? !resource.equals(action.resource) : action.resource != null) return false;
        return properties != null ? properties.equals(action.properties) : action.properties == null;

    }

    @Override
    public int hashCode() {
        int result = idAction;
        result = 31 * result + (userFrom != null ? userFrom.hashCode() : 0);
        result = 31 * result + (userTo != null ? userTo.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (int) type;
        result = 31 * result + (int) (millis ^ (millis >>> 32));
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
