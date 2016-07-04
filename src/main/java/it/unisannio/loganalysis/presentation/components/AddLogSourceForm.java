package it.unisannio.loganalysis.presentation.components;

import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;

/**
 * Created by mario on 03/07/16.
 */
public class AddLogSourceForm extends CustomComponent {

    private ComboBox serviceTypeCb;
    private TextField host;
    private TextField port;
    private TextField dbname;
    private TextField username;
    private TextField password;

    public AddLogSourceForm() {
        FormLayout form = new FormLayout();

        serviceTypeCb = new ComboBox();

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

        form.addComponent(serviceTypeCb);
        form.addComponent(host);
        form.addComponent(port);
        form.addComponent(dbname);
        form.addComponent(username);
        form.addComponent(password);

        form.setMargin(true);
        form.setSpacing(true);

        setCompositionRoot(form);
    }

}
