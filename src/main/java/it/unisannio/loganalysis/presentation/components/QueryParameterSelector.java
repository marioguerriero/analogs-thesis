package it.unisannio.loganalysis.presentation.components;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.Query;
import it.unisannio.loganalysis.analysis.QueryController;
import it.unisannio.loganalysis.analysis.QueryType;
import org.renjin.sexp.*;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Calendar;

/**
 * Created by graziano on 04/07/16.
 */
public class QueryParameterSelector extends CustomComponent {
    private TwinColSelect users;
    private DateField from;
    private DateField to;
    private ComboBox queryType;
    private CheckBox normalized;
    private Button executeButton;
    private ExecuteQueryListener executeQueryListener;

    private Map<Integer, String> usersMap;


    public QueryParameterSelector() {
        Panel panel = new Panel("Query Selection");
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        panel.setResponsive(true);
        //panel.setWidth("300px");
        //panel.setHeight("200px");
        panel.setContent(layout);


        //TwinColSelect
        users = new TwinColSelect("Selezione utenti");

        from = new DateField("Da");
        from.setDateFormat("dd MMM yyyy");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
        from.setValue(c.getTime());

        to = new DateField("A");
        to.setDateFormat("dd MMM yyyy");
        to.setValue(new Date());
        normalized = new CheckBox("Normalizza il risultato");

        users.setVisible(false);
        from.setVisible(false);
        to.setVisible(false);

        queryType = new ComboBox("Tipo della query");
        for(Query q : QueryType.getQueries()) {
            queryType.addItem(q);
            queryType.setItemCaption(q, QueryType.getDescription(q));
        }

        queryType.addValueChangeListener(
                (Property.ValueChangeListener) event -> {
                    if (getQueryType() == Query.DAILY_ACTIVE_RESOURCES){
                        //  layout.addComponent(new Label("Selected: " + event.getProperty().getValue()));
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);
                    }
                    if(getQueryType() == Query.DAILY_ACTIVE_USERS){
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);
                    }

                    if(getQueryType() == Query.DAILY_ACTIVITIES){
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);

                    }

                    if(getQueryType() == Query.MOST_USED_OS){
                        users.setVisible(true);
                        from.setVisible(false);
                        to.setVisible(false);
                    }

                    if(getQueryType() == Query.RESOURCE_ADDED_PER_DAY){
                        users.setVisible(true);
                        from.setVisible(true);
                        to.setVisible(true);
                    }

                    if(getQueryType() == Query.RESOURCE_USAGE){
                        users.setVisible(true);
                        from.setVisible(true);
                        to.setVisible(true);
                        //attribute
                    }

                    if(getQueryType() == Query.RESOURCE_USAGE_TIME){
                        users.setVisible(true);
                        from.setVisible(true);
                        to.setVisible(true);
                        //attribte

                    }

                    if(getQueryType() == Query.TIME_RANGE_USAGE){
                        users.setVisible(false);
                        from.setVisible(false);
                        to.setVisible(false);
                    }

                });
        queryType.setImmediate(true);




        executeButton = new Button("Esegui");
        executeButton.addClickListener((e) -> {
            if(executeQueryListener != null)
                try {
                    executeQueryListener.onExecuteListener();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (ScriptException e1) {
                    e1.printStackTrace();
                }
        });

        layout.addComponents(queryType,users, from, to, normalized, executeButton);



        layout.setSpacing(true);
        layout.setSizeUndefined();

        setCompositionRoot(panel);

    }

    public void setExecuteQueryListener(ExecuteQueryListener executeQueryListener) {
        this.executeQueryListener = executeQueryListener;
    }

    public long getFrom() {
        return from.getValue().getTime();
    }

    public long getTo() {
        return to.getValue().getTime();
    }

    public Query getQueryType() {
        return (Query) queryType.getValue();
    }

    public Integer[] getUsers(){
        Collection<String> selected = (Collection<String>) users.getValue();

        List<Integer> selectedIds = new ArrayList<>();
        for(int id : usersMap.keySet()) {
            if(selected.contains(usersMap.get(id))) {
                selectedIds.add(id);
            }
        }

        Integer[] ids = new Integer[selectedIds.size()];
        int i = 0;
        for(Integer id : selectedIds) {
            ids[i++] = id;
        }
        return ids;
    }

    public ListVector getAttributes(){
        return null;
    }

    public boolean isNormalized() {
        return normalized.getValue();
    }

    public void updateValues() {
        usersMap = new HashMap<>();
        ListVector usersDf = null;
        try {
            usersDf = QueryController.getInstance().getUsers();
            org.renjin.sexp.Vector ids = usersDf.getElementAsVector("idUser");
            org.renjin.sexp.Vector usernames = usersDf.getElementAsVector("username");

            for(int i = 0; i < ids.length(); i++) {
                int id = ids.getElementAsInt(i);
                String username = usernames.getElementAsString(i);
                usersMap.put(id, username);
            }

        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }

        users.addItems(usersMap.values());
    }

    public interface ExecuteQueryListener {
        void onExecuteListener() throws FileNotFoundException, ScriptException;
    }

}
