package it.unisannio.loganalysis.presentation.components;

import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

/**
 * Created by graziano on 03/07/16.
 */
public class AddLogSourceForm extends CustomComponent {

    private ComboBox serviceTypeCb;
    private ComboBox dialect;
    private TextField host;
    private TextField port;
    private TextField dbname;
    private TextField username;
    private TextField password;
    private Button confirmButton;

    public AddLogSourceForm() {
        FormLayout form = new FormLayout();

        serviceTypeCb = new ComboBox();

        dialect = new ComboBox("Tipo Database");
        dialect.addItem("MySQL");
        dialect.addItem("PostgreSQL");

        host = new TextField("Host");
        host.setRequired(true);
        host.addValidator(new NullValidator("Campo obbligatorio", false));

        port = new TextField("Port");
        port.setRequired(true);
        port.addValidator(new NullValidator("Campo obbligatorio", false));
        port.addValidator(new IntegerRangeValidator("Numero di porta errato", 1, 65535));

        dbname = new TextField("Nome del database");
        dbname.setRequired(true);
        dbname.addValidator(new NullValidator("Campo obbligatorio", false));

        username = new TextField("Username");
        username.setIcon(FontAwesome.USER);
        username.setRequired(true);
        username.addValidator(new NullValidator("Campo obbligatorio", false));

        password = new TextField("Password");
        password.setRequired(true);
        password.addValidator(new NullValidator("Campo obbligatorio", false));

        confirmButton = new Button("Aggiungi");

        form.addComponent(serviceTypeCb);
        form.addComponent(dialect);
        form.addComponent(host);
        form.addComponent(port);
        form.addComponent(dbname);
        form.addComponent(username);
        form.addComponent(password);
        form.addComponent(confirmButton);

        form.setMargin(true);
        form.setSpacing(true);

        setCompositionRoot(form);
    }

    public void setAddListener(Button.ClickListener listener) {
        confirmButton.addClickListener(listener);
    }

    public String getType() {
        return (String) serviceTypeCb.getValue();
    }

    public String getDialect() {
        return (String) dialect.getValue();
    }

    public String getHost() {
        return host.getValue();
    }

    public String getPort() {
        return port.getValue();
    }

    public String getSourceDb() {
        return dbname.getValue();
    }

    public String getUsername() {
        return username.getValue();
    }

    public String getpassword() {
        return password.getValue();
    }
}
