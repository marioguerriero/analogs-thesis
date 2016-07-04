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
    private User user;

    @ManyToOne
    @JoinColumn(name = "idResource")
    private Resource resource;

    @Column(name = "millis")
    private long millis;

    @OneToMany(mappedBy = "action")
    @MapKey(name = "key")
    private Map<String, ActionProperty> properties;

    public Action() { }

    public Action(User user, Resource resource, long millis, Map properties) {
        this.user = user;
        this.resource = resource;
        this.millis = millis;
        this.properties = properties;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (idAction != action.idAction) return false;
        if (millis != action.millis) return false;
        if (user != null ? !user.equals(action.user) : action.user != null) return false;
        if (resource != null ? !resource.equals(action.resource) : action.resource != null) return false;
        return properties != null ? properties.equals(action.properties) : action.properties == null;

    }

    @Override
    public int hashCode() {
        int result = idAction;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (resource != null ? resource.hashCode() : 0);
        result = 31 * result + (int) (millis ^ (millis >>> 32));
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    public int getIdAction() {

        return idAction;
    }

    public void setIdAction(int idAction) {
        this.idAction = idAction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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
}
