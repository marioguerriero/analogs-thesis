package it.unisannio.loganalysis.extractor.model;

import java.util.Collection;

/**
 * Created by paolomoriello on 30/06/16.
 */
public class Model {
    private Collection<Resource> resources;
    private Collection<User> users;
    private Collection<Action> actions;

    public Model(Collection<Resource> resources, Collection<User> users, Collection<Action> actions) {
        this.resources = resources;
        this.users = users;
        this.actions = actions;
    }

    public Model() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Model model = (Model) o;

        if (resources != null ? !resources.equals(model.resources) : model.resources != null) return false;
        if (users != null ? !users.equals(model.users) : model.users != null) return false;
        return actions != null ? actions.equals(model.actions) : model.actions == null;

    }

    @Override
    public int hashCode() {
        int result = resources != null ? resources.hashCode() : 0;
        result = 31 * result + (users != null ? users.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        return result;
    }

    public Collection<Resource> getResources() {

        return resources;
    }

    public void CollectionResources(Collection<Resource> resources) {
        this.resources = resources;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void CollectionUsers(Collection<User> users) {
        this.users = users;
    }

    public Collection<Action> getActions() {
        return actions;
    }

    public void CollectionActions(Collection<Action> actions) {
        this.actions = actions;
    }
}
