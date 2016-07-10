package it.unisannio.loganalysis.extractor.model;

import javax.persistence.*;
import java.util.*;

/**
 * Created by paolomoriello on 29/06/16.
 */

/**
 * Created by paolomoriello on 29/06/16.
 */
@Entity
@Table(name = "user")
public class User {
    @Id
    private int idUser;
    @Column(name = "fullname")
    private String fullname;
    @Column(name = "username")
    private String username;
    @OneToMany(mappedBy = "user")
    @MapKey(name = "key")
    private Map<String, UserProperty> properties;

    public User() { }

    public User(String fullname, String username, Map properties) {
        this.fullname = fullname;
        this.username = username;
        this.properties = properties;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, UserProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }

    public void addProperty(String key, UserProperty property) {
        properties.put(key, property);
    }

    public void appendProperty(String key, String value) {
        if(properties.containsKey(key)) {
            if (!properties.get(key).getValue().contains(value)) {
                properties.get(key).setValue(properties.get(key).getValue() + "," + value);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (idUser != user.idUser) return false;
        if (fullname != null ? !fullname.equals(user.fullname) : user.fullname != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        return properties != null ? properties.equals(user.properties) : user.properties == null;

    }

    @Override
    public int hashCode() {
        int result = idUser;
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
