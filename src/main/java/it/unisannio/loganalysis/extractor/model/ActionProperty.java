package it.unisannio.loganalysis.extractor.model;

import javax.persistence.*;

/**
 * Created by paolomoriello on 01/07/16.
 */
@Entity
@Table(name = "aav")
public class ActionProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="hibernate_sequence")
    @SequenceGenerator(name="hibernate_sequence", sequenceName="hibernate_sequence", allocationSize=1)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "entityId")
    private Action action;
    @Column(name = "attribute")
    private String key;
    @Column(name = "value")
    private String value;

    public ActionProperty(Action action, String key, String value) {
        this.action = action;
        this.key = key;
        this.value = value;
    }

    public ActionProperty() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionProperty that = (ActionProperty) o;

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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Action getAction() {

        return action;
    }

    public void setAction(Action action) {
        this.action = action;
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
