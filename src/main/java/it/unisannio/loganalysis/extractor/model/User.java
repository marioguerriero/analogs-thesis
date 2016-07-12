package it.unisannio.loganalysis.extractor.model;

import javax.persistence.*;
import java.util.*;

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
    @ElementCollection
    @JoinTable(name = "usertype", joinColumns = @JoinColumn(name ="idUser"))
    private Set<String> types = new HashSet<>();
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

    public void addProperty(String key, UserProperty property) {
        properties.put(key, property);
    }

    public void setProperties(Map<String, UserProperty> properties) {
        this.properties = properties;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    public void addType(String type) {
        types.add(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (idUser != user.idUser) return false;
        if (fullname != null ? !fullname.equals(user.fullname) : user.fullname != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;
        if (types != null ? !types.equals(user.types) : user.types != null) return false;
        return properties != null ? properties.equals(user.properties) : user.properties == null;

    }

    @Override
    public int hashCode() {
        int result = idUser;
        result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (types != null ? types.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
