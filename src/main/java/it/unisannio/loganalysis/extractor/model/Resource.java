package it.unisannio.loganalysis.extractor.model;

import javax.persistence.*;
import java.util.Map;

/**
 * Created by paolomoriello on 29/06/16.
 */
@Entity
@Table(name = "resource")
public class Resource {
    @Id
    private int idResource;
    @Column(name = "type")
    private String type;
    @ManyToOne
    @JoinColumn(name = "idParent")
    private Resource idParent;
    @OneToMany(mappedBy = "resource")
    @MapKey(name = "key")
    private Map<String, ResourceProperty> properties;

    public Resource() { }

    public Resource(String type, Map<String, ResourceProperty> properties) {
        this.type = type;
        this.properties = properties;
    }

    public int getIdResource() {
        return idResource;
    }

    public void setIdResource(int idResource) {
        this.idResource = idResource;
    }

    public Map<String, ResourceProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, ResourceProperty> properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Resource getIdParent() {
        return idParent;
    }

    public void setIdParent(Resource resourceParent) {
        this.idParent = resourceParent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (idResource != resource.idResource) return false;
        if (type != null ? !type.equals(resource.type) : resource.type != null) return false;
        return properties != null ? properties.equals(resource.properties) : resource.properties == null;

    }

    @Override
    public int hashCode() {
        int result = idResource;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
