package it.unisannio.loganalysis.presentation.components;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.Query;
import it.unisannio.loganalysis.analysis.QueryType;
import org.renjin.sexp.ListVector;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Calendar;

/**
 * Created by mario on 04/07/16.
 */
public class QueryParameterSelector extends CustomComponent {
    private TextField users;
    private DateField from;
    private DateField to;
    private ComboBox queryType;
    private CheckBox normalized;
    private Button executeButton;
    private Query query;
    private ExecuteQueryListener executeQueryListener;
    private int[] user;


    public QueryParameterSelector() {
        Panel panel = new Panel("Query Selection");
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        panel.setResponsive(true);
        //panel.setWidth("300px");
        //panel.setHeight("200px");
        panel.setContent(layout);


        users = new TextField("Utenti");

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
                    if ((event.getProperty().getValue().toString()).equals(QueryType.getQueryTypes())){
                        //  layout.addComponent(new Label("Selected: " + event.getProperty().getValue()));
                        users.setVisible(false);
                        from.setVisible(false);
                        to.setVisible(false);
                    }
                    if((event.getProperty().getValue().toString()).equals("Tempo di utilizzo delle risorse")){
                        users.setVisible(false);
                        from.setVisible(false);
                        to.setVisible(false);
                    }

                    if((event.getProperty().getValue().toString()).equals("Attività giornaliere degli utenti")){
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);

                    }

                    if((event.getProperty().getValue().toString()).equals("Attività giornaliere sulle risorse")){
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);
                    }

                    if((event.getProperty().getValue().toString()).equals("Attività giornaliere")){
                        users.setVisible(false);
                        from.setVisible(true);
                        to.setVisible(true);
                    }

                    if((event.getProperty().getValue().toString()).equals("Fasce temporali di utilizzo")){
                        users.setVisible(true);
                        from.setVisible(true);
                        to.setVisible(true);
                    }

                    if((event.getProperty().getValue().toString()).equals("Sistemi operativi più utilizzati")){
                        users.setVisible(false);

                    }

                    if((event.getProperty().getValue().toString()).equals("Risorse aggiunte al giorno")){
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

    public int[] getUsers(){
        return  user;
    }

    public ListVector getAttributes(){
        return null;
    }

    public boolean isNormalized() {
        return normalized.getValue();
    }

    public interface ExecuteQueryListener {
        void onExecuteListener() throws FileNotFoundException, ScriptException;
    }

}
