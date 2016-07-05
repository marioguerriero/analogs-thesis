package it.unisannio.loganalysis.presentation.components;

import com.vaadin.ui.*;
import it.unisannio.loganalysis.analysis.QueryType;

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

    private ExecuteListener executeListener;

    public QueryParameterSelector() {
        VerticalLayout layout = new VerticalLayout();

        users = new TextField("Utenti");

        from = new DateField("Da");
        from.setDateFormat("dd MMM yyyy");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, c.get(Calendar.MONTH)-1);
        from.setValue(c.getTime());

        to = new DateField("A");
        to.setDateFormat("dd MMM yyyy");
        to.setValue(new Date());

        queryType = new ComboBox("Tipo della query");
        queryType.addItems(QueryType.getQueryTypes());

        normalized = new CheckBox("Normalizza il risultato");

        executeButton = new Button("Esegui");
        executeButton.addClickListener((e) -> {
            if(executeListener != null)
                executeListener.onExecuteListener();
        });

        layout.addComponents(users, from, to, queryType, normalized, executeButton);
        layout.setSpacing(true);

        setCompositionRoot(layout);
    }

    public void setExecuteListener(ExecuteListener executeListener) {
        this.executeListener = executeListener;
    }

    public Date getFrom() {
        return from.getValue();
    }

    public Date getTo() {
        return to.getValue();
    }

    public int getQueryType() {
        return 0;
    }

    public boolean isNormalized() {
        return normalized.getValue();
    }

    public interface ExecuteListener {
        void onExecuteListener();
    }

}
