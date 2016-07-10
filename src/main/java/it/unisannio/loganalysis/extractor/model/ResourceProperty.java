package it.unisannio.loganalysis.extractor.model;


import javax.persistence.*;

/**
 * Created by paolomoriello on 01/07/16.
 */
@Entity
@Table(name = "rav")
public class ResourceProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="hibernate_sequence")
    @SequenceGenerator(name="hibernate_sequence", sequenceName="hibernate_sequence", allocationSize=1)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "entityId")
    private Resource resource;
    @Column(name = "attribute")
    private String key;
    @Column(name = "value")
    private String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceProperty that = (ResourceProperty) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    public ResourceProperty() { }

    public ResourceProperty(Resource resource, String key, String value) {
        this.resource = resource;
        this.key = key;
        this.value = value;
    }

    public Resource getResource() {

        return resource;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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
}
