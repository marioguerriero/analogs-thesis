package it.unisannio.loganalysis.presentation.components;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.QueryType;
import it.unisannio.loganalysis.analysis.TableHandler;
import it.unisannio.loganalysis.analysis.QueryTypeHandler;
import org.renjin.sexp.*;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Calendar;
//

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
    private Panel panel;

    private Map<Integer, String> usersMap;


    public QueryParameterSelector() {
        panel = new Panel("QueryType Selection");
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        panel.setResponsive(true);
        panel.setContent(layout);

        users = new TwinColSelect("Selezione utenti");
        from = new DateField("Da");
        from.setSizeFull();
        from.setResponsive(true);
        from.setDateFormat("dd MMM yyyy");
        from.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            Date fromDate = (Date) valueChangeEvent.getProperty().getValue();
            if(to != null) to.setRangeStart(fromDate);
        });
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
        from.setValue(c.getTime());
        from.setRangeEnd(new Date());
        to = new DateField("A");
        to.setSizeFull();
        to.setResponsive(true);
        to.setDateFormat("dd MMM yyyy");
        to.addValueChangeListener((Property.ValueChangeListener) valueChangeEvent -> {
            Date toDate = (Date) valueChangeEvent.getProperty().getValue();
            if(from != null) to.setRangeEnd(toDate);
        });
        to.setValue(new Date());
        to.setRangeEnd(new Date());
        normalized = new CheckBox("Normalizza il risultato");
        users.setVisible(false);
        from.setVisible(false);
        to.setVisible(false);
        queryType = new ComboBox("Tipo della query");
        queryType.setSizeFull();
        queryType.setResponsive(true);
        queryType.setNullSelectionAllowed(false);
        for(QueryType q : QueryTypeHandler.getQueries()) {
            queryType.addItem(q);
            queryType.setItemCaption(q, QueryTypeHandler.getDescription(q));
        }

        queryType.addValueChangeListener(
                (Property.ValueChangeListener) event -> {
                    if (getQueryType() == QueryType.DAILY_ACTIVE_RESOURCES){
                        //  layout.addComponent(new Label("Selected: " + event.getProperty().getValue()));

                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);
                    }
                    if(getQueryType() == QueryType.DAILY_ACTIVE_USERS){
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);
                    }

                    if(getQueryType() == QueryType.DAILY_ACTIVITIES){
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);

                    }

                    if(getQueryType() == QueryType.MOST_USED_OS){
                        users.setVisible(true);
                        from.setVisible(false);
                        to.setVisible(false);
                    }

                    if(getQueryType() == QueryType.RESOURCE_ADDED_PER_DAY){
                        users.setVisible(true);
                        from.setVisible(true);
                        to.setVisible(true);
                    }

                    if(getQueryType() == QueryType.RESOURCE_USAGE){
                        users.setVisible(true);
                        from.setVisible(true);
                        to.setVisible(true);

                    }

                    if(getQueryType() == QueryType.RESOURCE_USAGE_TIME){
                        users.setVisible(true);
                        from.setVisible(true);
                        to.setVisible(true);


                    }

                    if(getQueryType() == QueryType.TIME_RANGE_USAGE){
                        users.setVisible(false);
                        from.setVisible(false);
                        to.setVisible(false);
                    }

                });
        queryType.setImmediate(true);

        executeButton = new Button("Esegui");
        executeButton.addClickListener((e) -> {
            if(executeQueryListener != null && this.getTo() !=0 && this.getFrom() !=0)
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
        if(from.getValue() != null )
        return from.getValue().getTime();
        return 0;
    }

    public long getTo() {
       if(to.getValue() != null)
        return to.getValue().getTime();
        return 0;
    }

    public QueryType getQueryType() {
        return (QueryType) queryType.getValue();
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
            usersDf = TableHandler.getInstance().getUsers();
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

        users.removeAllItems();
        users.addItems(usersMap.values());
    }

    public interface ExecuteQueryListener {
        void onExecuteListener() throws FileNotFoundException, ScriptException;
    }



}