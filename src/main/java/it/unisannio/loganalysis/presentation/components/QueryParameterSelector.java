package it.unisannio.loganalysis.presentation.components;

import com.vaadin.ui.*;

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

        normalized = new CheckBox("Normalizza il risultato");

        layout.addComponents(users, from, to, queryType, normalized);
        layout.setSpacing(true);

        setCompositionRoot(layout);
    }

}
